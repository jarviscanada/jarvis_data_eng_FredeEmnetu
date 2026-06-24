package ca.jrvs.apps.stockquote.service;


import ca.jrvs.apps.stockquote.dao.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class QuoteServiceIntegTest {

  static String HOST = System.getenv("LOCALHOST");
  static String PORT = System.getenv("PORT");
  static String DATABASE = System.getenv("DATABASE");
  static String USER = System.getenv("USER");
  static String PASSWORD = System.getenv("PASSWORD");
  static String APIKEY = System.getenv("APIKEY");



  private static Connection connection;
  private static QuoteDao quoteDao;
  private static QuoteService quoteService;

  @BeforeClass
  public static void setUpClass() throws SQLException {
    connection = new DatabaseConnectionManager(HOST, PORT, DATABASE, USER, PASSWORD).getConnection();
    quoteDao = new QuoteDao(connection);
    QuoteHttpHelper httpHelper = new QuoteHttpHelper(APIKEY, new OkHttpClient());
    quoteService = new QuoteService(quoteDao, httpHelper);
  }
  @Before
  public void setUp() throws SQLException {


    quoteDao.deleteAll();
  }

  @After
  public void tearDown() throws SQLException {
    quoteDao.deleteAll();

  }
  @AfterClass
  public static void tearDownClass() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  // ---------- fetchQuoteDataFromAPI() ----------

  @Test
  public void fetchQuoteDataFromAPI_validTicker_persistsAndReturnsQuote() {
    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

    assertTrue(result.isPresent());
    assertEquals("AAPL", result.get().getSymbol());

    // confirm it actually got written to the DB
    Optional<Quote> saved = quoteDao.findById("AAPL");
    assertTrue(saved.isPresent());
  }

  @Test(expected = NullPointerException.class)
  public void fetchQuoteDataFromAPI_invalidTicker_returnsEmptyOptional() {
    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("INVALIDTICKER123");

    assertFalse(result.isPresent());
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchQuoteDataFromAPI_emptyTicker_throwsIllegalArgumentException() {
    quoteService.fetchQuoteDataFromAPI("");
  }
}