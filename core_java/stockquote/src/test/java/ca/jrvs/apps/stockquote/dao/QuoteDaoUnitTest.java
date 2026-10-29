package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
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
public class QuoteDaoUnitTest {

  @Mock
  private Connection mockConnection;

  @Mock
  private PreparedStatement mockPreparedStatement;

  @Mock
  private Statement mockStatement;

  @Mock
  private ResultSet mockResultSet;

  private QuoteDao quoteDao;

  @Before
  public void setUp() {
    quoteDao = new QuoteDao(mockConnection);
  }

  // ---------- save() ----------

  @Test
  public void save_validEntity_returnsSavedEntity() throws SQLException {
    Quote quote = new Quote();
    quote.setSymbol("AAPL");
    quote.setOpen(190.0);
    quote.setHigh(192.0);

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    Quote result = quoteDao.save(quote);

    assertEquals(quote, result);
    verify(mockPreparedStatement).setString(1, "AAPL");
    verify(mockPreparedStatement).executeUpdate();
  }

  @Test(expected = IllegalArgumentException.class)
  public void save_nullSymbol_throwsIllegalArgumentException() {
    Quote quote = new Quote();
    quote.setSymbol(null);
    quoteDao.save(quote);
  }

  @Test(expected = RuntimeException.class)
  public void save_sqlException_throwsRuntimeException() throws SQLException {
    Quote quote = new Quote();
    quote.setSymbol("AAPL");

    when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

    quoteDao.save(quote);
  }

  // ---------- findById() ----------

  @Test
  public void findById_existingId_returnsQuote() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true, false);
    when(mockResultSet.getString("symbol")).thenReturn("AAPL");
    when(mockResultSet.getDouble("open")).thenReturn(190.0);

    Optional<Quote> result = quoteDao.findById("AAPL");

    assertTrue(result.isPresent());
    assertEquals("AAPL", result.get().getSymbol());
  }

  @Test
  public void findById_nonExistingId_returnsEmptyOptional() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(false);

    Optional<Quote> result = quoteDao.findById("FAKE");

    assertFalse(result.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void findById_emptyId_throwsIllegalArgumentException() {
    quoteDao.findById("");
  }

  // ---------- findAll() ----------

  @Test
  public void findAll_multipleRows_returnsAllQuotes() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true, true, false);
    when(mockResultSet.getString("symbol")).thenReturn("AAPL", "GOOG");

    Iterable<Quote> result = quoteDao.findAll();

    int count = 0;
    for (Quote q : result) count++;
    assertEquals(2, count);
  }

  @Test
  public void findAll_noRows_returnsEmptyIterable() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(false);

    Iterable<Quote> result = quoteDao.findAll();

    assertFalse(result.iterator().hasNext());
  }

  @Test(expected = RuntimeException.class)
  public void findAll_sqlException_throwsRuntimeException() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("DB error"));

    quoteDao.findAll();
  }

  // ---------- deleteById() ----------

  @Test
  public void deleteById_existingId_deletesSuccessfully() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);

    quoteDao.deleteById("AAPL");

    verify(mockPreparedStatement).setString(1, "AAPL");
  }

  @Test(expected = IllegalStateException.class)
  public void deleteById_noRowsAffected_throwsIllegalStateException() throws SQLException {
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(0);

    quoteDao.deleteById("FAKE");
  }

  @Test(expected = IllegalArgumentException.class)
  public void deleteById_nullId_throwsIllegalArgumentException() {
    quoteDao.deleteById(null);
  }

  // ---------- deleteAll() ----------

  @Test
  public void deleteAll_executesSuccessfully() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);

    quoteDao.deleteAll();

    verify(mockStatement).executeUpdate(anyString());
  }

  @Test(expected = RuntimeException.class)
  public void deleteAll_sqlException_throwsRuntimeException() throws SQLException {
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeUpdate(anyString())).thenThrow(new SQLException("DB error"));

    quoteDao.deleteAll();
  }
}
