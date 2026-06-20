package ca.jrvs.apps.stockquote.util;

import ca.jrvs.apps.stockquote.DAO.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.DTO.Quote;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;


public class Main {
  private static final Logger log = Logger.getLogger(Main.class.getName());
  private static final String API_KEY = "3SXVOC66SF4SEMZ2";
  private static Connection createConn() throws SQLException {
    DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager("localhost","5432",
        "stock_quote","postgres", "password");
    return databaseConnectionManager.getConnection();
  }
  public static void main(String[] args) throws SQLException {
    Connection conn =  Main.createConn();
    QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper(API_KEY, new OkHttpClient());
    Quote apple = quoteHttpHelper.fetchQuoteInfo("AAPL");
//    Quote google = quoteHttpHelper.fetchQuoteInfo("GOOG");
//    Quote microsoft = quoteHttpHelper.fetchQuoteInfo("MSFT");

    QuoteDao quoteDao = new QuoteDao(conn);
    quoteDao.save(apple);
    quoteDao.deleteById("MSFT");
//    quoteDao.save(microsoft);



  }

}

