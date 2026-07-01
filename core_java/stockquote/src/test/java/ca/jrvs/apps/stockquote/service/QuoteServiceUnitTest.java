package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuoteServiceUnitTest {

  @Mock
  private QuoteDao mockDao;

  @Mock
  private QuoteHttpHelper mockHttpHelper;

  private QuoteService quoteService;

  @Before
  public void setUp() {
    quoteService = new QuoteService(mockDao, mockHttpHelper);
  }

  // ---------- fetchQuoteDataFromAPI() ----------

  @Test
  public void fetchQuoteDataFromAPI_validTicker_returnsQuote() {
    Quote fakeQuote = new Quote();
    fakeQuote.setSymbol("AAPL");

    when(mockHttpHelper.fetchQuoteInfo("AAPL")).thenReturn(fakeQuote);
    when(mockDao.save(fakeQuote)).thenReturn(fakeQuote);

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

    assertTrue(result.isPresent());
    assertEquals("AAPL", result.get().getSymbol());
    verify(mockDao).save(fakeQuote);
  }

  @Test
  public void fetchQuoteDataFromAPI_httpHelperReturnsNull_returnsEmptyOptional() {
    when(mockHttpHelper.fetchQuoteInfo("AAPL")).thenReturn(null);

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

    assertFalse(result.isPresent());
    verify(mockDao, never()).save(any(Quote.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchQuoteDataFromAPI_nullTicker_throwsIllegalArgumentException() {
    quoteService.fetchQuoteDataFromAPI(null);
  }

  @Test
  public void fetchQuoteDataFromAPI_httpHelperThrowsIllegalArgument_returnsEmptyOptional() {
    when(mockHttpHelper.fetchQuoteInfo("FAKE"))
        .thenThrow(new IllegalArgumentException("Bad symbol"));

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("FAKE");

    assertFalse(result.isPresent());
    verify(mockDao, never()).save(any(Quote.class));
  }
}