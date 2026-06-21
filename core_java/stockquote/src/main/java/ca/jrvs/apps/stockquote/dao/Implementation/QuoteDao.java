package ca.jrvs.apps.stockquote.dao.Implementation;

import static ca.jrvs.apps.stockquote.util.DatabaseConnectionManager.exceptionFormat;

import ca.jrvs.apps.stockquote.dao.Interface.CrudDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

  private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class.getName());

  private final Connection c;
  private static final String UPSERT = "INSERT INTO quote (symbol, open, high, low, price, volume, "
    + "latest_trading_day, previous_close, change, change_percent, timestamp) "
    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
    + "ON CONFLICT (symbol) DO UPDATE SET "
    + "open = EXCLUDED.open, "
    + "high = EXCLUDED.high, "
    + "low = EXCLUDED.low, "
    + "price = EXCLUDED.price, "
    + "volume = EXCLUDED.volume, "
    + "latest_trading_day = EXCLUDED.latest_trading_day, "
    + "previous_close = EXCLUDED.previous_close, "
    + "change = EXCLUDED.change, "
    + "change_percent = EXCLUDED.change_percent, "
    + "timestamp = EXCLUDED.timestamp;";
  private static final String FIND_BY_ID = "SELECT * FROM quote WHERE symbol = ?";


  private static final String FIND_ALL = "SELECT * FROM quote";

  private static final String DELETE_BY_ID = "DELETE FROM quote WHERE symbol = ?";

  private static final String DELETE_ALL = "DELETE FROM quote";

  public QuoteDao(Connection c) {
    this.c = c;
  }

  /**
   *
   */
  @Override
  public Quote save(Quote entity) throws IllegalArgumentException {
    if (entity == null || entity.getSymbol() == null || entity.getSymbol().isEmpty()) throw new IllegalArgumentException("Symbol could not be Found");

    try(PreparedStatement preparedStatement = c.prepareStatement(UPSERT)){
      preparedStatement.setString(1, entity.getSymbol());
      preparedStatement.setDouble(2, entity.getOpen());
      preparedStatement.setDouble(3, entity.getHigh());
      preparedStatement.setDouble(4, entity.getLow());
      preparedStatement.setDouble(5, entity.getPrice());
      preparedStatement.setLong(6, entity.getVolume());
      preparedStatement.setTimestamp(7, entity.getLatestTradingDay());
      preparedStatement.setDouble(8, entity.getPreviousClose());
      preparedStatement.setDouble(9, entity.getChange());
      preparedStatement.setString(10, entity.getChangePercent());
      preparedStatement.setTimestamp(11, entity.getTimestamp());
      preparedStatement.executeUpdate();
      return entity;
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "QuoteDao.save", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   *
   *
   */
  @Override
  public Optional<Quote> findById(String id) throws IllegalArgumentException {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id can not be null or Empty");

    try (PreparedStatement statement = c.prepareStatement(FIND_BY_ID)) {
      statement.setString(1, id);
      ResultSet rs = statement.executeQuery();
      while(rs.next()){
        Quote quote = mapRowToQuote(rs);
        return Optional.of(quote);
      }
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "QuoteDao.findByID", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  /**
   *
   *
   */
  @Override
  public Iterable<Quote> findAll() {
    List<Quote> quotes = new ArrayList<>();
    try (Statement statement = c.createStatement()){
      ResultSet rs = statement.executeQuery(FIND_ALL);
      while(rs.next()){
        Quote quote = mapRowToQuote(rs);
        quotes.add(quote);
      }

    } catch (SQLException e){
      logger.warn(String.format(exceptionFormat, "QuoteDao.findlAll", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
    return quotes;
  }

  /**
   *
   *
   */
  @Override
  public void deleteById(String id) throws IllegalArgumentException {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException("QuoteDao.deleteById : Id can not be null or Empty");

    try (PreparedStatement statement = c.prepareStatement(DELETE_BY_ID)) {
      statement.setString(1, id);
      int rowsAffected = statement.executeUpdate();
      if(rowsAffected == 0) {
        logger.warn(String.format(exceptionFormat, "QuoteDao.deleteById", "unable to delete row", "test"));
        throw new IllegalStateException("QuoteDao.deleteById: unable to delete row");
      }

    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "QuoteDao.deleteById", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   *
   *
   */
  @Override
  public void deleteAll() {
    try (Statement statement = c.createStatement()) {
      statement.executeUpdate(DELETE_ALL);
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "QuoteDao.deleteAll", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   *
   *
   */
  private Quote mapRowToQuote(ResultSet rs) throws SQLException {
    Quote quote = new Quote();
    quote.setSymbol(rs.getString("symbol"));
    quote.setOpen(rs.getDouble("open"));
    quote.setHigh(rs.getDouble("high"));
    quote.setLow(rs.getDouble("low"));
    quote.setPrice(rs.getDouble("price"));
    quote.setLatestTradingDay(rs.getTimestamp("latest_trading_day"));
    quote.setPreviousClose(rs.getDouble("previous_close"));
    quote.setChange(rs.getDouble("change"));
    quote.setChangePercent(rs.getString("change_percent"));
    quote.setTimestamp(rs.getTimestamp("timestamp"));

    return quote;
  }
}