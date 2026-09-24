package ca.jrvs.apps.trading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ca.jrvs.apps.trading.dto.Account;
import ca.jrvs.apps.trading.dto.MarketOrder;
import ca.jrvs.apps.trading.dto.MarketOrder.Option;
import ca.jrvs.apps.trading.dto.SecurityOrder;
import ca.jrvs.apps.trading.dto.Trader;
import ca.jrvs.apps.trading.repository.AccountJpaRepoDao;
import ca.jrvs.apps.trading.repository.TraderJpaRepoDao;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceIntTest {

    @Autowired
    OrderService orderService;

    @Autowired
    AccountJpaRepoDao accountRepo;

    @Autowired
    TraderJpaRepoDao traderRepo;


    Trader testTrader;
    Account testAccount;

    @BeforeEach
    void setUp() {
        testTrader = new Trader();
        testTrader.setFirstName("Test");
        testTrader.setLastName("Trader");
        testTrader.setDob(new Date());
        testTrader.setCountry("Canada");
        testTrader.setEmail("testAccount@jrvs.ca");

        testTrader = traderRepo.save(testTrader);

        testAccount = new Account();
        testAccount.setTraderId(testTrader.getId());
        testAccount.setAmount(10000.00);
        testAccount = accountRepo.save(testAccount);
    }

    @Test
    void testExecuteMarketOrder_buy_withSufficientFunds_fillsOrder() {
        MarketOrder order = new MarketOrder();
        order.setTraderId(testAccount.getId());
        order.setTicker("AAPL");
        order.setSize(10);
        order.setOption(Option.BUY);

        SecurityOrder result = orderService.executeMarketOrder(order);

        assertNotNull(result);
        assertEquals("FILLED", result.getStatus());
        assertEquals("AAPL", result.getTicker());

        Account updated = accountRepo.findById(testAccount.getId()).get();
        assertEquals(10000.00 - (result.getPrice() * 10), updated.getAmount(), 0.001);
    }

    @Test
    void testExecuteMarketOrder_buy_withInsufficientFunds_throwsException() {
        MarketOrder order = new MarketOrder();
        order.setTraderId(testAccount.getId());
        order.setTicker("AAPL");
        order.setSize(1_000_000);
        order.setOption(MarketOrder.Option.BUY);

        assertThrows(IllegalArgumentException.class, () -> orderService.executeMarketOrder(order));
    }

    @Test
    void testExecuteMarketOrder_sell_withNoPosition_throwsException() {
        MarketOrder order = new MarketOrder();
        order.setTraderId(testAccount.getId());
        order.setTicker("AAPL");
        order.setSize(5);
        order.setOption(MarketOrder.Option.SELL);

        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
            () -> orderService.executeMarketOrder(order));
    }

    @Test
    void testExecuteMarketOrder_sell_withSufficientPosition_fillsOrder() {
        MarketOrder buyOrder = new MarketOrder();
        buyOrder.setTraderId(testAccount.getId());
        buyOrder.setTicker("AAPL");
        buyOrder.setSize(20);
        buyOrder.setOption(MarketOrder.Option.BUY);
        orderService.executeMarketOrder(buyOrder);

        MarketOrder sellOrder = new MarketOrder();
        sellOrder.setTraderId(testAccount.getId());
        sellOrder.setTicker("AAPL");
        sellOrder.setSize(5);
        sellOrder.setOption(MarketOrder.Option.SELL);

        // Act
        SecurityOrder result = orderService.executeMarketOrder(sellOrder);

        // Assert
        assertEquals("FILLED", result.getStatus());
    }

    @AfterEach
    void tearDown() {
        // @Transactional rolls this back automatically
    }
}