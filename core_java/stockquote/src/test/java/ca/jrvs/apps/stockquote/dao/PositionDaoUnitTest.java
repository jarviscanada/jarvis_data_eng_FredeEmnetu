package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.Implementation.PositionDao;
import ca.jrvs.apps.stockquote.dto.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PositionDaoUnitTest {

  @Mock
  private Connection mockConnection;

  @Mock
  private PreparedStatement mockPreparedStatement;

  @Mock
  private Statement mockStatement;

  @Mock
  private ResultSet mockResultSet;

  private PositionDao positionDao;

  @Before
  public void setUp() {
    positionDao = new PositionDao(mockConnection);
  }

  // ---------- save() ----------

  @Test
  public void save_validEntity_returnsSavedEntity() throws SQLException {
    Position position = new Position();
    position.setSymbol("AAPL");
    position.setNumOfShares(10);
    position.setValuePaid(1500.0);

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    Position result = positionDao.save(position);

    assertEquals(position, result);
    verify(mockPreparedStatement).setString(1, "AAPL");
    verify(mockPreparedStatement).setDouble(2, 10);
    verify(mockPreparedStatement).setDouble(3, 1500.0);
    verify(mockPreparedStatement).executeUpdate();
  }

  @Test(expected = IllegalArgumentException.class)
  public void save_nullEntity_throwsIllegalArgumentException() {
    positionDao.save(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void save_nullSymbol_throwsIllegalArgumentException() {
    Position position = new Position();
    position.setSymbol(null);
    positionDao.save(position);
  }

  @Test(expected = IllegalArgumentException.class)
  public void save_emptySymbol_throwsIllegalArgumentException() {
    Position position = new Position();
    position.setSymbol("");
    positionDao.save(position);
  }

  @Test(expected = RuntimeException.class)
  public void save_sqlException_throwsRuntimeException() throws SQLException {
    Position position = new Position();
    position.setSymbol("AAPL");

    when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

    positionDao.save(position);
  }

  // ---------- findById() ----------

  @Test
  public void findById_existingId_returnsPosition() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true, false);
    when(mockResultSet.getString("symbol")).thenReturn("AAPL");
    when(mockResultSet.getInt("number_of_shares")).thenReturn(10);
    when(mockResultSet.getDouble("value_paid")).thenReturn(1500.0);

    Optional<Position> result = positionDao.findById("AAPL");

    assertTrue(result.isPresent());
    assertEquals("AAPL", result.get().getSymbol());
    assertEquals(10, result.get().getNumOfShares());
    assertEquals(1500.0, result.get().getValuePaid(), 0.001);
  }

  @Test
  public void findById_nonExistingId_returnsEmptyOptional() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(false);

    Optional<Position> result = positionDao.findById("FAKE");

    assertFalse(result.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void findById_nullId_throwsIllegalArgumentException() {
    positionDao.findById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void findById_emptyId_throwsIllegalArgumentException() {
    positionDao.findById("");
  }

  @Test(expected = RuntimeException.class)
  public void findById_sqlException_throwsRuntimeException() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
    positionDao.findById("AAPL");
  }

  // ---------- findAll() ----------

  @Test
  public void findAll_multipleRows_returnsAllPositions() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true, true, false);
    when(mockResultSet.getString("symbol")).thenReturn("AAPL", "GOOG");
    when(mockResultSet.getInt("number_of_shares")).thenReturn(10, 5);
    when(mockResultSet.getDouble("value_paid")).thenReturn(1500.0, 2500.0);

    Iterable<Position> result = positionDao.findAll();

    int count = 0;
    for (Position p : result) {
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  public void findAll_noRows_returnsEmptyIterable() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(false);

    Iterable<Position> result = positionDao.findAll();

    assertFalse(result.iterator().hasNext());
  }

  @Test(expected = RuntimeException.class)
  public void findAll_sqlException_throwsRuntimeException() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("DB error"));

    positionDao.findAll();
  }

  // ---------- deleteById() ----------

  @Test
  public void deleteById_existingId_deletesSuccessfully() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);

    positionDao.deleteById("AAPL");

    verify(mockPreparedStatement).setString(1, "AAPL");
    verify(mockPreparedStatement).executeUpdate();
  }

  @Test(expected = IllegalStateException.class)
  public void deleteById_noRowsAffected_throwsIllegalStateException() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(0);

    positionDao.deleteById("FAKE");
  }

  @Test(expected = IllegalArgumentException.class)
  public void deleteById_nullId_throwsIllegalArgumentException() {
    positionDao.deleteById(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void deleteById_emptyId_throwsIllegalArgumentException() {
    positionDao.deleteById("");
  }

  @Test(expected = RuntimeException.class)
  public void deleteById_sqlException_throwsRuntimeException() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
    positionDao.deleteById("AAPL");
  }

  // ---------- deleteAll() ----------

  @Test
  public void deleteAll_executesSuccessfully() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);

    positionDao.deleteAll();

    verify(mockStatement).executeUpdate(anyString());
  }

  @Test(expected = RuntimeException.class)
  public void deleteAll_sqlException_throwsRuntimeException() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeUpdate(anyString())).thenThrow(new SQLException("DB error"));

    positionDao.deleteAll();
  }
}