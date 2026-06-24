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

  private static final String DB_USER = "postgres";
  private static final String DB_PASSWORD = "password";
  private static final String API_KEY = "3SXVOC66SF4SEMZ2";
  private static final String PORT = "5432";
  private static final String DATABASE = "stock_quote";
  private static final String HOST = "localhost";



  private static Connection connection;
  private static QuoteDao quoteDao;
  private static QuoteService quoteService;

  @BeforeClass
  public static void setUpClass() throws SQLException {
    connection = new DatabaseConnectionManager(HOST, PORT, DATABASE, DB_USER, DB_PASSWORD).getConnection();
    quoteDao = new QuoteDao(connection);
    QuoteHttpHelper httpHelper = new QuoteHttpHelper(API_KEY, new OkHttpClient());
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