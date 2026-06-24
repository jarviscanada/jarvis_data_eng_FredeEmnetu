package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.dao.Implementation.PositionDao;
import ca.jrvs.apps.stockquote.dao.Implementation.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.sql.Connection;
import java.sql.SQLException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main{
  static String HOST = System.getenv("LOCALHOST");
  static String PORT = System.getenv("PORT");
  static String DATABASE = System.getenv("DATABASE");
  static String USER = System.getenv("USER");
  static String PASSWORD = System.getenv("PASSWORD");
  static String APIKEY = System.getenv("APIKEY");
  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  public static void main (String[] args) {
    try{
      Connection conn = new DatabaseConnectionManager(HOST,PORT,DATABASE,USER, PASSWORD).getConnection();
      QuoteService quoteService = new QuoteService(new QuoteDao(conn), new QuoteHttpHelper(APIKEY, new OkHttpClient()));
      PositionService positionService = new PositionService(new PositionDao(conn));
      StockQuoteController controller = new StockQuoteController(quoteService, positionService);
      controller.initClient();
    } catch (SQLException e){
      logger.error("Unable to connect to database");
    }

  }
}
