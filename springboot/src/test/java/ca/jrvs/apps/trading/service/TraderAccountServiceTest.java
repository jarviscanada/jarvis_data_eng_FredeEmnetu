package ca.jrvs.apps.trading.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.trading.ResourceNotFoundException;
import ca.jrvs.apps.trading.dto.Account;
import ca.jrvs.apps.trading.dto.Position;
import ca.jrvs.apps.trading.dto.Trader;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.repository.AccountJpaRepoDao;
import ca.jrvs.apps.trading.repository.PositionJpaRepoDao;
import ca.jrvs.apps.trading.repository.SecurityOrderJpaRepoDao;
import ca.jrvs.apps.trading.repository.TraderJpaRepoDao;
import java.util.Date;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TraderAccountServiceTest {

    @Mock
    private AccountJpaRepoDao accountJpaRepoDao;
    @Mock
    private TraderJpaRepoDao traderJpaRepoDao;
    @Mock
    private PositionJpaRepoDao positionJpaRepoDao;
    @Mock
    private SecurityOrderJpaRepoDao securityOrderJpaRepoDao;

    private TraderAccountService service;

    @Before
    public void setUp() {
        service = new TraderAccountService(accountJpaRepoDao, traderJpaRepoDao, positionJpaRepoDao,
            securityOrderJpaRepoDao);
    }

    private Trader validTrader() {
        Trader trader = new Trader();
        trader.setFirstName("Jane");
        trader.setLastName("Doe");
        trader.setDob(new Date());
        trader.setCountry("CA");
        trader.setEmail("jane@example.com");
        return trader;
    }

    // ---------- allFilled ----------

    @Test
    public void allFilled_ignoresId_returnsTrue_whenOtherFieldsPresent() {
        Trader trader = validTrader();
        trader.setId(null); // id is excluded from the check
        assertTrue(TraderAccountService.allFilled(trader));
    }

    @Test
    public void allFilled_returnsFalse_whenNonIdFieldMissing() {
        Trader trader = validTrader();
        trader.setEmail(null);
        assertFalse(TraderAccountService.allFilled(trader));
    }

    // ---------- createTraderAndAccount ----------

    @Test
    public void createTraderAndAccount_throws_whenTraderIncomplete() {
        Trader trader = validTrader();
        trader.setCountry(null);

        try {
            service.createTraderAndAccount(trader);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(traderJpaRepoDao, never()).save(any());
    }

    @Test
    public void createTraderAndAccount_savesTraderAndZeroBalanceAccount() {
        Trader trader = validTrader();
        trader.setId(7);

        TraderAccountView view = service.createTraderAndAccount(trader);

        verify(traderJpaRepoDao).save(trader);
        verify(accountJpaRepoDao).save(any(Account.class));
        assertEquals(trader, view.getTrader());
        assertEquals(Double.valueOf(0.0), view.getAccount().getAmount());
        assertEquals(Integer.valueOf(7), view.getAccount().getTraderId());
    }

    @Test
    public void createTraderAndAccount_wrapsDaoFailure_asRuntimeException() {
        Trader trader = validTrader();
        when(traderJpaRepoDao.save(any())).thenThrow(new RuntimeException("db down"));

        try {
            service.createTraderAndAccount(trader);
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }
    }

    // ---------- deleteTraderById ----------

    @Test
    public void deleteTraderById_throws_whenIdNull() {
        try {
            service.deleteTraderById(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteTraderById_throws_whenTraderNotFound() {
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.empty());

        service.deleteTraderById(1);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteTraderById_throws_whenAccountNotFound() {
        Trader trader = validTrader();
        trader.setId(1);
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(trader));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.empty());

        service.deleteTraderById(1);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteTraderById_throws_whenAccountHasNonZeroBalance() {
        Trader trader = validTrader();
        trader.setId(1);
        Account account = new Account();
        account.setId(1);
        account.setAmount(50.0);
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(trader));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));

        service.deleteTraderById(1);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteTraderById_throws_whenPositionNotFound() {
        Trader trader = validTrader();
        trader.setId(1);
        Account account = new Account();
        account.setId(1);
        account.setAmount(0.0);
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(trader));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));
        when(positionJpaRepoDao.findById(1)).thenReturn(Optional.empty());

        service.deleteTraderById(1);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteTraderById_throws_whenOpenPositionExists() {
        Trader trader = validTrader();
        trader.setId(1);
        Account account = new Account();
        account.setId(1);
        account.setAmount(0.0);
        Position position = new Position();
        position.setPosition(10);
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(trader));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));
        when(positionJpaRepoDao.findById(1)).thenReturn(Optional.of(position));

        service.deleteTraderById(1);
    }

    @Test
    public void deleteTraderById_deletesOrderAccountAndTrader_whenEligible() {
        Trader trader = validTrader();
        trader.setId(1);
        Account account = new Account();
        account.setId(1);
        account.setAmount(0.0);
        Position position = new Position();
        position.setPosition(0);
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(trader));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));
        when(positionJpaRepoDao.findById(1)).thenReturn(Optional.of(position));

        service.deleteTraderById(1);

        verify(securityOrderJpaRepoDao).deleteById(1);
        verify(accountJpaRepoDao).deleteById(1);
        verify(traderJpaRepoDao).deleteById(1);
    }

    @Test(expected = RuntimeException.class)
    public void deleteTraderById_wrapsDeletionFailure_asRuntimeException() {
        Trader trader = validTrader();
        trader.setId(1);
        Account account = new Account();
        account.setId(1);
        account.setAmount(0.0);
        Position position = new Position();
        position.setPosition(0);
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(trader));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));
        when(positionJpaRepoDao.findById(1)).thenReturn(Optional.of(position));
        doThrow(new RuntimeException("db error")).when(securityOrderJpaRepoDao).deleteById(1);

        service.deleteTraderById(1);
    }

    // ---------- deposit ----------

    @Test(expected = ResourceNotFoundException.class)
    public void deposit_throws_whenTraderNotFound() {
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.empty());

        service.deposit(1, 100.0);
    }

    @Test(expected = IllegalStateException.class)
    public void deposit_throws_whenFundNotPositive() {
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(validTrader()));

        service.deposit(1, 0.0);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deposit_throws_whenAccountNotFound() {
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(validTrader()));
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.empty());

        service.deposit(1, 100.0);
    }

    @Test
    public void deposit_increasesBalanceAndSaves() {
        when(traderJpaRepoDao.findById(1)).thenReturn(Optional.of(validTrader()));
        Account account = new Account();
        account.setId(1);
        account.setAmount(100.0);
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));

        Account result = service.deposit(1, 50.0);

        assertEquals(Double.valueOf(150.0), result.getAmount());
        verify(accountJpaRepoDao).save(account);
    }

    // ---------- withdraw ----------

    @Test
    public void withdraw_throws_whenIdNull() {
        try {
            service.withdraw(null, 10.0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test(expected = IllegalStateException.class)
    public void withdraw_throws_whenFundNotPositive() {
        service.withdraw(1, -5.0);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void withdraw_throws_whenAccountNotFound() {
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.empty());

        service.withdraw(1, 10.0);
    }

    @Test
    public void withdraw_decreasesBalanceAndSaves() {
        Account account = new Account();
        account.setId(1);
        account.setAmount(100.0);
        when(accountJpaRepoDao.findById(1)).thenReturn(Optional.of(account));

        Account result = service.withdraw(1, 40.0);

        assertEquals(Double.valueOf(60.0), result.getAmount());
        verify(accountJpaRepoDao).save(account);
    }
}
