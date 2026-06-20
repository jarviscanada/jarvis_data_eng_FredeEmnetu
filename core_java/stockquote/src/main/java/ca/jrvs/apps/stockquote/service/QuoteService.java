package ca.jrvs.apps.stockquote.service;

import static ca.jrvs.apps.stockquote.util.DatabaseConnectionManager.exceptionFormat;

import ca.jrvs.apps.stockquote.DAO.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.DTO.Quote;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.text.html.Option;
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
   * TODO: Implement this method. Steps:
   *
   * 1. Call httpHelper.fetchQuoteInfo(ticker) to get the latest quote
   * 2. Save the quote to the database using dao.save(quote)
   * 3. Return Optional.of(quote)
   *
   * 4. If fetchQuoteInfo throws IllegalArgumentException (invalid ticker),
   *    catch it and return Optional.empty()
   *
   * Think about: why do we save the quote to DB even if the user just wants to view it?
   * Hint: the position table has a FK to quote — a quote must exist before you can buy.
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