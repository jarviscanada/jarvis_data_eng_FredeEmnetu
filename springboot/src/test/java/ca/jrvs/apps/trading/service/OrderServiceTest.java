package ca.jrvs.apps.trading.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ca.jrvs.apps.trading.dto.Account;
import ca.jrvs.apps.trading.dto.MarketOrder;
import ca.jrvs.apps.trading.dto.MarketOrder.Option;
import ca.jrvs.apps.trading.dto.Position;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.dto.SecurityOrder;
import ca.jrvs.apps.trading.repository.AccountJpaRepoDao;
import ca.jrvs.apps.trading.repository.PositionJpaRepoDao;
import ca.jrvs.apps.trading.repository.SecurityOrderJpaRepoDao;
import ca.jrvs.apps.trading.repository.TraderJpaRepoDao;
import ca.jrvs.apps.trading.service.OrderService;
import ca.jrvs.apps.trading.service.QuoteService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private AccountJpaRepoDao accountRepo;
    @Mock
    private SecurityOrderJpaRepoDao securityOrderRepo;
    @Mock
    private TraderJpaRepoDao traderRepo;
    @Mock
    private PositionJpaRepoDao positionRepo;
    @Mock
    private QuoteService quoteService;

    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService(accountRepo, securityOrderRepo,
            traderRepo, positionRepo, quoteService);
    }


    private MarketOrder validOrder(Option option, int size) {
        MarketOrder order = new MarketOrder();
        order.setTicker("AAPL");
        order.setSize(size);
        order.setTraderId(1);
        order.setOption(option);
        return order;
    }

    private Account accountWith(int id, double amount) {
        Account account = new Account();
        account.setId(id);
        account.setTraderId(id);
        account.setAmount(amount);
        return account;
    }

    private Quote quoteWithPrice(double price) {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
        quote.setCurrentPrice(price);
        return quote;
    }

    // ---------- allFilled ----------

    @Test
    public void allFilled_returnsTrue_whenNoEmptyOrNullFields() {
        MarketOrder order = validOrder(Option.BUY, 10);
        assertTrue(OrderService.allFilled(order));
    }

    @Test
    public void allFilled_returnsFalse_whenAStringFieldIsNull() {
        MarketOrder order = validOrder(Option.BUY, 10);
        order.setTicker(null);
        assertFalse(OrderService.allFilled(order));
    }

    // ---------- executeMarketOrder: validation ----------

    @Test
    public void executeMarketOrder_throws_whenTickerMissing() {
        MarketOrder order = validOrder(Option.BUY, 10);
        order.setTicker(null);
        try {
            orderService.executeMarketOrder(order);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected - fails the allFilled() check before any repo interaction
        }
        verify(accountRepo, never()).findById(any());
    }

    @Test
    public void executeMarketOrder_throws_whenSizeIsZeroOrLess() {
        MarketOrder order = validOrder(Option.BUY, 0);
        try {
            orderService.executeMarketOrder(order);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("size"));
        }
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void executeMarketOrder_throws_whenTraderAccountNotFound() {
        MarketOrder order = validOrder(Option.BUY, 10);
        when(accountRepo.findById(1)).thenReturn(Optional.empty());

        orderService.executeMarketOrder(order);
    }

    // ---------- executeMarketOrder: BUY ----------

    @Test
    public void executeMarketOrder_buy_success_deductsBalanceAndFillsOrder() {
        MarketOrder order = validOrder(Option.BUY, 10);
        Account account = accountWith(1, 5000.0);
        when(accountRepo.findById(1)).thenReturn(Optional.of(account));
        when(quoteService.findQuoteByTicker("AAPL")).thenReturn(quoteWithPrice(100.0));
        when(securityOrderRepo.save(any(SecurityOrder.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SecurityOrder result = orderService.executeMarketOrder(order);

        assertEquals("FILLED", result.getStatus());
        assertEquals(Double.valueOf(100.0), result.getPrice());
        assertEquals(Integer.valueOf(10), result.getSize());
        assertEquals(Double.valueOf(4000.0), account.getAmount()); // 5000 - 10*100
        verify(accountRepo).save(account);
        verify(securityOrderRepo).save(any(SecurityOrder.class));
    }

    @Test
    public void executeMarketOrder_buy_insufficientFunds_throwsAndDoesNotSaveOrder() {
        MarketOrder order = validOrder(Option.BUY, 100);
        Account account = accountWith(1, 10.0);
        when(accountRepo.findById(1)).thenReturn(Optional.of(account));
        when(quoteService.findQuoteByTicker("AAPL")).thenReturn(quoteWithPrice(100.0));

        try {
            orderService.executeMarketOrder(order);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("insufficient"));
        }

        verify(accountRepo, never()).save(any());
        verify(securityOrderRepo, never()).save(any());
    }

    // ---------- executeMarketOrder: SELL ----------

    @Test
    public void executeMarketOrder_sell_success_increasesBalanceAndFillsOrder() {
        MarketOrder order = validOrder(Option.SELL, 5);
        Account account = accountWith(1, 1000.0);
        Position position = new Position();
        position.setAccountId(1);
        position.setTicker("AAPL");
        position.setPosition(10);

        when(accountRepo.findById(1)).thenReturn(Optional.of(account));
        when(positionRepo.findByAccountIdAndTicker(1, "AAPL")).thenReturn(Optional.of(position));
        when(quoteService.findQuoteByTicker("AAPL")).thenReturn(quoteWithPrice(50.0));
        when(securityOrderRepo.save(any(SecurityOrder.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SecurityOrder result = orderService.executeMarketOrder(order);

        assertEquals("FILLED", result.getStatus());
        assertEquals(Double.valueOf(1250.0), account.getAmount()); // 1000 + 5*50
        verify(accountRepo).save(account);
    }

    @Test
    public void executeMarketOrder_sell_noPosition_throwsEmptyResult() {
        MarketOrder order = validOrder(Option.SELL, 5);
        Account account = accountWith(1, 1000.0);
        when(accountRepo.findById(1)).thenReturn(Optional.of(account));
        when(positionRepo.findByAccountIdAndTicker(1, "AAPL")).thenReturn(Optional.empty());

        try {
            orderService.executeMarketOrder(order);
            fail("Expected EmptyResultDataAccessException");
        } catch (EmptyResultDataAccessException e) {
            // expected
        }
        verify(securityOrderRepo, never()).save(any());
    }

    @Test
    public void executeMarketOrder_sell_insufficientPosition_throws() {
        MarketOrder order = validOrder(Option.SELL, 20);
        Account account = accountWith(1, 1000.0);
        Position position = new Position();
        position.setAccountId(1);
        position.setTicker("AAPL");
        position.setPosition(5);

        when(accountRepo.findById(1)).thenReturn(Optional.of(account));
        when(positionRepo.findByAccountIdAndTicker(1, "AAPL")).thenReturn(Optional.of(position));

        try {
            orderService.executeMarketOrder(order);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Insufficient position"));
        }
        verify(accountRepo, never()).save(any());
    }
}
