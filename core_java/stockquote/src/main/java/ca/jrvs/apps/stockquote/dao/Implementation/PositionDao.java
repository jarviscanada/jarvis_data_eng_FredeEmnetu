package ca.jrvs.apps.stockquote.dao.Implementation;

import static ca.jrvs.apps.stockquote.util.DatabaseConnectionManager.exceptionFormat;

import ca.jrvs.apps.stockquote.dao.Interface.CrudDao;
import ca.jrvs.apps.stockquote.dto.Position;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class PositionDao implements CrudDao<Position, String> {

  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);

  private final Connection c;

  private static final String UPSERT = "INSERT INTO position (symbol, number_of_shares, value_paid) "
      + "VALUES (?, ?, ?) "
      + "ON CONFLICT (symbol) DO UPDATE SET "
      + "symbol = EXCLUDED.symbol, "
      + "number_of_shares = EXCLUDED.number_of_shares, "
      + "value_paid = EXCLUDED.value_paid";
  private static final String  FIND_BY_ID = "SELECT * FROM position WHERE symbol = ?";
  private static final String  FIND_ALL = "SELECT * FROM position";
  private static final String  DELETE_BY_ID = "DELETE FROM position WHERE symbol = ?";
  private static final String  DELETE_ALL = "DELETE FROM position";

  public PositionDao(Connection c) {
    this.c = c;
  }

  /**
   * @return Position (saved position)
   *
   * @param entity you would like to save
   */
  @Override
  public Position save(Position entity) throws IllegalArgumentException {
    if (entity == null || entity.getSymbol() == null || entity.getSymbol().isEmpty()) throw new IllegalArgumentException("Symbol could not be Found");

    try(PreparedStatement preparedStatement = c.prepareStatement(UPSERT)){
      preparedStatement.setString(1, entity.getSymbol());
      preparedStatement.setDouble(2, entity.getNumOfShares());
      preparedStatement.setDouble(3, entity.getValuePaid());

      preparedStatement.executeUpdate();
      return entity;
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "PositionDao.save", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   */
  @Override
  public Optional<Position> findById(String id) throws IllegalArgumentException {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException("Id can not be null or Empty");

    try (PreparedStatement statement = c.prepareStatement(FIND_BY_ID)) {
      statement.setString(1, id);
      ResultSet rs = statement.executeQuery();
      while(rs.next()){
        Position position = mapRowToPosition(rs);
        return Optional.of(position);
      }
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "PositionDao.findByID", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  /**
   * TODO: Find all positions.
   * Same pattern as QuoteDao.findAll()
   */
  @Override
  public Iterable<Position> findAll() {
    List<Position> positions = new ArrayList<>();
    try (Statement statement = c.createStatement()) {
      ResultSet rs = statement.executeQuery(FIND_ALL);
      while (rs.next()){
        Position position = mapRowToPosition(rs);
        positions.add(position);
      }
      return positions;
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "PositionDao.findAll", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   * TODO: Delete a position by its ticker symbol.
   * Same pattern as QuoteDao.deleteById()
   */
  @Override
  public void deleteById(String id) throws IllegalArgumentException {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException("PositionDao.deleteById : Id can not be null or Empty");

    try (PreparedStatement statement = c.prepareStatement(DELETE_BY_ID)) {
      statement.setString(1, id);
      int rowsAffected = statement.executeUpdate();
      if(rowsAffected == 0) throw new IllegalStateException("PositionDao.deleteById: unable to delete row");

    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "PositionDao.findByID", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   * TODO: Delete all positions.
   */
  @Override
  public void deleteAll() {
    try (Statement statement = c.createStatement()) {
      statement.executeUpdate(DELETE_ALL);
    } catch (SQLException e) {
      logger.warn(String.format(exceptionFormat, "PositionDao.deleteAll", e.getMessage(), e.getErrorCode()));
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @return returned positon
   * @param rs result
   */
  private Position mapRowToPosition(ResultSet rs) throws SQLException {
    Position position = new Position();
    position.setSymbol(rs.getString("symbol"));
    position.setNumOfShares(rs.getInt("number_of_shares"));
    position.setValuePaid(rs.getDouble("value_paid"));

    return position;
  }
}
