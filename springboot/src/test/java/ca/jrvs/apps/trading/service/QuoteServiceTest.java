package ca.jrvs.apps.trading.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.repository.MarketDataDao;
import ca.jrvs.apps.trading.repository.QuoteJpaRepoDao;
import ca.jrvs.apps.trading.service.QuoteService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuoteServiceTest {

    @Mock
    private QuoteJpaRepoDao quoteJpaRepoDao;
    @Mock
    private MarketDataDao marketDataDao;

    private QuoteService quoteService;

    @Before
    public void setUp() {
        quoteService = new QuoteService(quoteJpaRepoDao, marketDataDao);
    }

    private Quote quote(String ticker, double price) {
        Quote quote = new Quote();
        quote.setTicker(ticker);
        quote.setCurrentPrice(price);
        return quote;
    }

    // ---------- findQuoteByTicker ----------

    @Test
    public void findQuoteByTicker_throws_onNullTicker() {
        try {
            quoteService.findQuoteByTicker(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void findQuoteByTicker_throws_onEmptyTicker() {
        try {
            quoteService.findQuoteByTicker("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void findQuoteByTicker_returnsQuote_whenPresent() throws IOException {
        when(marketDataDao.findFinnQuoteByTicker("AAPL"))
            .thenReturn(Optional.of(quote("AAPL", 123.45)));

        Quote result = quoteService.findQuoteByTicker("AAPL");

        assertEquals("AAPL", result.getTicker());
        assertEquals(Double.valueOf(123.45), result.getCurrentPrice());
    }

    @Test
    public void findQuoteByTicker_throws_whenDaoReturnsEmpty() throws IOException {
        when(marketDataDao.findFinnQuoteByTicker("BADTICKER")).thenReturn(Optional.empty());

        try {
            quoteService.findQuoteByTicker("BADTICKER");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test(expected = EntityNotFoundException.class)
    public void findQuoteByTicker_wrapsIOException_asEntityNotFoundException() throws IOException {
        when(marketDataDao.findFinnQuoteByTicker("AAPL")).thenThrow(new IOException("boom"));

        quoteService.findQuoteByTicker("AAPL");
    }

    @Test(expected = EntityNotFoundException.class)
    public void findQuoteByTicker_rewrapsEntityNotFoundException() throws IOException {
        when(marketDataDao.findFinnQuoteByTicker("AAPL"))
            .thenThrow(new EntityNotFoundException("not found upstream"));

        quoteService.findQuoteByTicker("AAPL");
    }

    // ---------- save ----------

    @Test
    public void save_throws_onNullQuote() {
        try {
            quoteService.save(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(quoteJpaRepoDao, never()).save(any());
    }

    @Test
    public void save_delegatesToRepo() {
        Quote quote = quote("AAPL", 100.0);
        when(quoteJpaRepoDao.save(quote)).thenReturn(quote);

        Quote result = quoteService.save(quote);

        assertSame(quote, result);
        verify(quoteJpaRepoDao).save(quote);
    }

    // ---------- updateMarketData ----------

    @Test
    public void updateMarketData_throws_onNullQuote() {
        try {
            quoteService.updateMarketData(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void updateMarketData_throws_whenExistingQuoteNotFound() {
        Quote quote = quote("AAPL", 100.0);
        when(quoteJpaRepoDao.findQuoteByTicker("AAPL")).thenReturn(null);

        try {
            quoteService.updateMarketData(quote);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        verify(quoteJpaRepoDao, never()).save(any());
    }

    @Test
    public void updateMarketData_savesGivenQuote_whenExistingFound() {
        Quote existing = quote("AAPL", 90.0);
        Quote update = quote("AAPL", 100.0);
        when(quoteJpaRepoDao.findQuoteByTicker("AAPL")).thenReturn(existing);

        quoteService.updateMarketData(update);

        verify(quoteJpaRepoDao).save(update);
    }

    // ---------- saveQuotes ----------

    @Test
    public void saveQuotes_fetchesAndSavesEachTicker() throws IOException {
        when(marketDataDao.findFinnQuoteByTicker("AAPL"))
            .thenReturn(Optional.of(quote("AAPL", 100.0)));
        when(marketDataDao.findFinnQuoteByTicker("MSFT"))
            .thenReturn(Optional.of(quote("MSFT", 200.0)));
        when(quoteJpaRepoDao.save(any(Quote.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<Quote> result = quoteService.saveQuotes(Arrays.asList("AAPL", "MSFT"));

        assertEquals(2, result.size());
        verify(quoteJpaRepoDao, times(2)).save(any(Quote.class));
    }

    // ---------- findAllQuotes ----------

    @Test
    public void findAllQuotes_delegatesToRepo() {
        List<Quote> quotes = Arrays.asList(quote("AAPL", 1.0), quote("MSFT", 2.0));
        when(quoteJpaRepoDao.findAll()).thenReturn(quotes);

        List<Quote> result = quoteService.findAllQuotes();

        assertSame(quotes, result);
    }
}
