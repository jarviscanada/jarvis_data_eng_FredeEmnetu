package ca.jrvs.apps.stockquote.service;

import static ca.jrvs.apps.stockquote.util.DatabaseConnectionManager.exceptionFormat;

import ca.jrvs.apps.stockquote.dao.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuoteService {

  private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

  private final QuoteDao dao;
  private final QuoteHttpHelper httpHelper;

  public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
    this.dao = dao;
    this.httpHelper = httpHelper;
  }

  /**
   * Fetches latest quote data from Alpha Vantage API and persists it to the database.
   *
   * @param ticker - stock symbol
   * @return Latest quote information or empty optional if ticker symbol not found
   */
  public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
    if(ticker == null || ticker.isEmpty()) throw new IllegalArgumentException("QuoteService.fetchQuoteDataFromAPI; ticker cannot be null or empty");
    try {
      Quote quote = httpHelper.fetchQuoteInfo(ticker);
      if(quote == null ) return Optional.empty();
      dao.save(quote);
      return Optional.of(quote);
    }catch (IllegalArgumentException e){
      logger.warn(String.format(exceptionFormat, "QuoteService.fetchQuoteFromAPI", e.getMessage(), e.getCause()));
    }
    return Optional.empty();
  }
}