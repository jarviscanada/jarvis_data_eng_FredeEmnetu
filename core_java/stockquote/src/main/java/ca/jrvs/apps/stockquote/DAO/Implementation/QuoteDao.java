package ca.jrvs.apps.stockquote.DAO.Implementation;

import static ca.jrvs.apps.stockquote.util.DatabaseConnectionManager.exceptionFormat;

import ca.jrvs.apps.stockquote.DAO.Interface.CrudDao;
import ca.jrvs.apps.stockquote.DTO.Quote;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

  private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class.getName());

  private final Connection c;
  // TODO: Define SQL constants for all CRUD operations.
  //
  // UPSERT - Use INSERT ... ON CONFLICT (symbol) DO UPDATE SET ...
  //   This single statement handles both insert (new quote) and update (existing quote).
  //   Columns: symbol, open, high, low, price, volume, latest_trading_day,
  //            previous_close, change, change_percent, timestamp
  //
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
   * TODO: Save (upsert) a quote to the database.
   * Steps:
   * 1. Validate: if entity or ticker is null, throw IllegalArgumentException
   * 2. Create a PreparedStatement with your UPSERT SQL
   * 3. Set all 11 parameters using ps.setString(), ps.setDouble(), ps.setInt(),
   *    ps.setDate(), ps.setTimestamp()
   * 4. Execute the update: ps.executeUpdate()
   * 5. Return the entity
   * 6. Catch SQLException and wrap in RuntimeException
   *
   * Hint: Parameter order must match your SQL's VALUES (?, ?, ?, ...)
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
   * TODO: Find a quote by its ticker symbol.
   *
   * Steps:
   * 1. Validate: if id is null, throw IllegalArgumentException
   * 2. Create a PreparedStatement with FIND_BY_ID SQL
   * 3. Set the symbol parameter
   * 4. Execute the query and get ResultSet
   * 5. If rs.next() is true, map the row to a Quote using mapRowToQuote()
   *    and return Optional.of(quote)
   * 6. Otherwise return Optional.empty()
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
   * TODO: Find all quotes in the database.
   *
   * Steps:
   * 1. Create a list to collect results
   * 2. Execute FIND_ALL query
   * 3. Loop through ResultSet, map each row with mapRowToQuote(), add to list
   * 4. Return the list
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
   * TODO: Delete a quote by its ticker symbol.
   *
   * Steps:
   * 1. Validate: if id is null, throw IllegalArgumentException
   * 2. Create PreparedStatement with DELETE_BY_ID SQL
   * 3. Set the symbol parameter
   * 4. Execute the update
   *
   * Note: If the symbol doesn't exist, executeUpdate() returns 0 - that's fine,
   * silently ignore it (per CrudDao contract).
   */
  @Override
  public void deleteById(String id) throws IllegalArgumentException {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException("QuoteDao.deleteById : Id can not be null or Empty");

    try (PreparedStatement statement = c.prepareStatement(DELETE_BY_ID)) {
      statement.setString(1, id);
      int rowsAffected = statement.executeUpdate();
      if(rowsAffected == 0) throw new IllegalStateException("QuoteDao.deleteById: unable to delete row");

    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "QuoteDao.findByID", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   * TODO: Delete all quotes from the database.
   *
   * Warning: This will fail if any positions still reference quotes (FK constraint).
   * Always delete positions first!
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
   * TODO: Map a ResultSet row to a Quote object.
   *
   * Use rs.getString("symbol"), rs.getDouble("open"), rs.getInt("volume"),
   * rs.getDate("latest_trading_day"), rs.getTimestamp("timestamp"), etc.
   *
   * Column names must match what's in setup.sql.
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