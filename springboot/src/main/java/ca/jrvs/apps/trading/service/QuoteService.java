package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.repository.MarketDataDao;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.repository.QuoteJpaRepoDao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

  private final QuoteJpaRepoDao quoteJpaRepoDao;

  private final MarketDataDao marketDataDao;

  @Autowired
  public QuoteService(QuoteJpaRepoDao quoteJpaRepoDao, MarketDataDao marketDataDao) {
    this.quoteJpaRepoDao = quoteJpaRepoDao;
    this.marketDataDao = marketDataDao;
  }

  /**
   * Find a Quote from the given ticker
   *
   * @param ticker
   * @return corresponding Quote object
   * @throws IllegalArgumentException if ticker is invalid
   */
  public Quote findQuoteByTicker(String ticker) {
    if(ticker == null || ticker.isEmpty()) {
      throw new IllegalArgumentException("Ticker is null or empty");
    }
    Optional<Quote> quote;
    try {
      quote = marketDataDao.findFinnQuoteByTicker(ticker);
      if (!quote.isPresent()) {
        throw new IllegalArgumentException("Failed to fetch quote for ticker " + ticker);
      }
      return quote.get();
    } catch(EntityNotFoundException | IOException e){
      throw new EntityNotFoundException("Quote not found");
    }
  }

  /**
   * Update a given quote to the quote table without validation
   *
   * @param quote entity to save
   * @return the saved quote entity
   */
  public Quote save(Quote quote) {
    if (quote == null) {
      throw new IllegalArgumentException("Quote is null");
    }
    return quoteJpaRepoDao.save(quote);
  }

  /**
   * TODO check method with Trevor
   * Updates quote with new given quote
   *
   *
   * @throws ResourceNotFoundException if ticker is not found from IEX
   * @throws DataAccessException if unable to retrieve data
   * @throws IllegalArgumentException for invalid input
   */
  public void updateMarketData(Quote quote) {
    if (quote == null) {
      throw new IllegalArgumentException("Quote is null");
    }
    Quote old = quoteJpaRepoDao.findQuoteByTicker(quote.getTicker());
    if (old == null) {
      throw new  IllegalArgumentException("Quote does not exist in database");
    }
    old = quote;
    quoteJpaRepoDao.save(old);
  }

  /**
   * TODO Check method with trevor
   * Validate (against IEX) and save given tickers to quote table
   *
   * - get IexQuote(s)
   * - convert each IexQuote to Quote entity
   * - persist the quote to db
   *
   * @param tickers
   * @return list of converted quote entities
   * @throws IllegalArgumentException if ticker is not found from IEX
   */
  public List<Quote> saveQuotes(List<String> tickers) {

      return tickers
          .stream()
          .map(this::findQuoteByTicker)
          .map(quoteJpaRepoDao::save)
          .collect(Collectors.toList());
  }

  /**
   * Find all quotes from the quote table
   *
   * @return a list of quotes
   */
  public List<Quote> findAllQuotes() {
      return quoteJpaRepoDao.findAll();

  }





}
