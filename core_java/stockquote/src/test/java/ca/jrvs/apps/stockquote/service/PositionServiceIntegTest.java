package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.Implementation.PositionDao;
import ca.jrvs.apps.stockquote.dto.Position;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class PositionServiceIntegTest {


  private static final String DB_USER = "postgres";
  private static final String DB_PASSWORD = "password";
  private static final String PORT = "5432";
  private static final String HOST = "localhost";
  private static final String DATABASE = "stock_quote";

  private static Connection connection;
  private static PositionDao positionDao;
  private static PositionService positionService;

  @BeforeClass
  public static void setUpClass ()throws SQLException {
    connection = new DatabaseConnectionManager(HOST, PORT, DATABASE, DB_USER, DB_PASSWORD).getConnection();
    positionDao = new PositionDao(connection);
    positionService = new PositionService(positionDao);


  }
  @Before
  public void setUp ()throws SQLException {
    // Clean slate before every test
    positionDao.deleteAll();
  }
  @AfterClass
  public static void tearDownClass() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
  @After
  public void tearDown() throws SQLException {
    positionDao.deleteAll();

  }

  // ---------- buy() ----------

  @Test
  public void buy_newTicker_createsPosition() {
    Position result = positionService.buy("AAPL", 10, 150.0);

    assertEquals("AAPL", result.getSymbol());
    assertEquals(10, result.getNumOfShares());
    assertEquals(150.0, result.getValuePaid(), 0.001);

    Optional<Position> saved = positionDao.findById("AAPL");
    assertTrue(saved.isPresent());
    assertEquals(10, saved.get().getNumOfShares());
  }

  @Test
  public void buy_existingTicker_accumulatesSharesAndValue() {
    positionService.buy("AAPL", 10, 150.0);
    Position result = positionService.buy("AAPL", 5, 160.0);

    assertEquals(15, result.getNumOfShares());
    assertEquals(150.0 + (5 * 160.0), result.getValuePaid(), 0.001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void buy_emptyTicker_throwsIllegalArgumentException() {
    positionService.buy("", 10, 150.0);
  }

  // ---------- sell() ----------

  @Test
  public void sell_existingTicker_removesPosition() {
    positionService.buy("AAPL", 10, 150.0);

    positionService.sell("AAPL");

    Optional<Position> result = positionDao.findById("AAPL");
    assertFalse(result.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void sell_nonExistingTicker_throwsIllegalArgumentException() {
    positionService.sell("FAKE");
  }

  @Test(expected = IllegalArgumentException.class)
  public void sell_emptyTicker_throwsIllegalArgumentException() {
    positionService.sell("");
  }

  // ---------- viewPortfolio() ----------

  @Test
  public void viewPortfolio_multiplePositions_returnsAll() {
    positionService.buy("AAPL", 10, 150.0);
    positionService.buy("GOOG", 5, 2800.0);

    Iterable<Position> portfolio = positionService.viewPortfolio();

    int count = 0;
    for (Position p : portfolio) count++;
    assertEquals(2, count);
  }

  @Test
  public void viewPortfolio_emptyPortfolio_returnsNoPositions() {
    Iterable<Position> portfolio = positionService.viewPortfolio();

    assertFalse(portfolio.iterator().hasNext());
  }
}
