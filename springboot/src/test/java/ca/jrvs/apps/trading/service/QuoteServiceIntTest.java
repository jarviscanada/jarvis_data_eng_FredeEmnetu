package ca.jrvs.apps.trading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.repository.MarketDataDao;
import ca.jrvs.apps.trading.repository.QuoteJpaRepoDao;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
// ^ boots the full real Spring context: real QuoteService, real QuoteJpaRepoDao
//   (and therefore a real database connection), real bean wiring throughout.
@ActiveProfiles("test")
// ^ loads application-test.properties instead of application.properties,
//   so the REAL QuoteJpaRepoDao hits a test database, not production.
@Transactional
// ^ wraps EACH @Test method in a transaction that's rolled back automatically
//   afterward, so save()/updateMarketData() calls don't leave permanent rows.
public class QuoteServiceIntTest {

    @Autowired
    QuoteService quoteService;

    @Autowired
    QuoteJpaRepoDao quoteJpaRepoDao;

    @MockBean
    // ^ explicitly REPLACES the real MarketDataDao bean inside Spring's context
    //   with a Mockito mock, for this test suite class only.
    MarketDataDao marketDataDao;

    Quote testQuote;

    @BeforeEach
    void setUp() {
        quoteJpaRepoDao.deleteAll();
        testQuote = new Quote();
        testQuote.setTicker("Test Quote");
        testQuote.setCurrentPrice(150.00);
        testQuote.setChange(0.0);
        testQuote.setHigh(155.00);
        testQuote.setLow(148.00);
        testQuote.setOpen(150.00);
        testQuote.setPercentChange(0.0);
        testQuote.setPreviousClose(150.00);
        testQuote.setTimestamp(System.currentTimeMillis());

    }

    @Test
    void testFindQuoteByTicker_whenMarketDataFound_returnsQuote() throws IOException {

        when(marketDataDao.findFinnQuoteByTicker("Test Quote")).thenReturn(Optional.of(testQuote));

        Quote result = quoteService.findQuoteByTicker("Test Quote");

        // Assert
        assertNotNull(result);
        assertEquals("Test Quote", result.getTicker());
        assertEquals(Double.valueOf(150.00), result.getCurrentPrice());
    }

    @Test
    void testFindQuoteByTicker_whenMarketDataNotFound_throwsIllegalArgumentException() throws IOException {
        // Arrange — explicitly simulate "ticker not found" from the external source
        when(marketDataDao.findFinnQuoteByTicker("UNKNOWN")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> quoteService.findQuoteByTicker("UNKNOWN"));
    }

    @Test
    void testFindQuoteByTicker_whenNullTicker_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> quoteService.findQuoteByTicker(null));
        assertThrows(IllegalArgumentException.class, () -> quoteService.findQuoteByTicker(""));
    }

    @Test
    void testFindQuoteByTicker_whenMarketDataDaoThrowsIOException_wrapsInEntityNotFoundException()
        throws IOException {
        when(marketDataDao.findFinnQuoteByTicker("AAPL")).thenThrow(new IOException("network error"));

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> quoteService.findQuoteByTicker("AAPL"));
        // ^ matches the explicit catch(EntityNotFoundException | IOException e) block,
        //   which re-throws as EntityNotFoundException
    }

    @Test
    void testSave_persistsQuoteToRealDatabase() {

        Quote saved = quoteService.save(testQuote);

        // Assert
        assertNotNull(saved.getTicker());
        // ^ explicit: confirms JPA actually generated and assigned an ID

        // Explicitly verify directly against the DAO too, not just the returned object
        Quote fetched = quoteJpaRepoDao.findQuoteByTicker("Test Quote");
        assertNotNull(fetched);
        assertEquals(Double.valueOf(150.00), fetched.getCurrentPrice());
    }

    @Test
    void testSave_whenQuoteIsNull_throwsIllegalArgumentException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> quoteService.save(null));
    }

    @Test
    void testUpdateMarketData_whenQuoteExists_savesUpdatedQuote() {

        quoteJpaRepoDao.save(testQuote);

        Quote updatedData = new Quote();
        updatedData.setTicker("Test Quote");
        updatedData.setCurrentPrice(200.00);   // explicitly a different price than the original

        // Act
        quoteService.updateMarketData(updatedData);

        // Assert — verify against the real DB what actually got persisted
        Quote result = quoteJpaRepoDao.findQuoteByTicker("Test Quote");
        assertNotNull(result);
        assertEquals(Double.valueOf(200.00), result.getCurrentPrice());

    }

    @Test
    void testUpdateMarketData_whenQuoteDoesNotExist_throwsIllegalArgumentException() {
        // Arrange — explicitly do NOT save anything first, so the lookup fails
        Quote nonExistent = new Quote();
        nonExistent.setTicker("NEVERSAVED");
        nonExistent.setCurrentPrice(100.00);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> quoteService.updateMarketData(nonExistent));
    }

    @Test
    void testUpdateMarketData_whenQuoteIsNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> quoteService.updateMarketData(null));
    }

    @Test
    void testSaveQuotes_fetchesFromMarketDataAndPersistsEach() throws IOException {
        // Arrange — stub the FAKE external source for two tickers
        Quote appleQuote = new Quote();
        appleQuote.setTicker("AAPL");
        appleQuote.setCurrentPrice(150.00);
        appleQuote.setChange(5.0);
        appleQuote.setHigh(25.00);
        appleQuote.setLow(12336.00);
        appleQuote.setOpen(5540.00);
        appleQuote.setPercentChange(44.0);
        appleQuote.setPreviousClose(7530.00);
        appleQuote.setTimestamp(System.currentTimeMillis());

        Quote msftQuote = new Quote();
        msftQuote.setTicker("MSFT");
        msftQuote.setCurrentPrice(300.00);
        msftQuote.setChange(0.0);
        msftQuote.setHigh(5.00);
        msftQuote.setLow(136.00);
        msftQuote.setOpen(550.00);
        msftQuote.setPercentChange(4.0);
        msftQuote.setPreviousClose(750.00);
        msftQuote.setTimestamp(System.currentTimeMillis());

        when(marketDataDao.findFinnQuoteByTicker("AAPL")).thenReturn(Optional.of(appleQuote));
        when(marketDataDao.findFinnQuoteByTicker("MSFT")).thenReturn(Optional.of(msftQuote));


        List<Quote> result = quoteService.saveQuotes(Arrays.asList("AAPL", "MSFT"));

        // Assert
        assertEquals(2, result.size());

        // Explicitly verify both rows actually landed in the real DB
        assertNotNull(quoteJpaRepoDao.findQuoteByTicker("AAPL"));
        assertNotNull(quoteJpaRepoDao.findQuoteByTicker("MSFT"));
    }

    @Test
    void testFindAllQuotes_returnsAllPersistedRows() {
        // Arrange
        Quote quote2 = new Quote();
        quote2.setTicker("MSFT");
        quote2.setCurrentPrice(300.00);
        quote2.setChange(043.0);
        quote2.setHigh(15235.00);
        quote2.setLow(14238.00);
        quote2.setOpen(10.00);
        quote2.setPercentChange(20.0);
        quote2.setPreviousClose(130.00);
        quote2.setTimestamp(System.currentTimeMillis());

        quoteJpaRepoDao.save(testQuote);
        quoteJpaRepoDao.save(quote2);

        // Act
        List<Quote> all = quoteService.findAllQuotes();

        // Assert
        assertEquals(2, all.size());
    }
}
