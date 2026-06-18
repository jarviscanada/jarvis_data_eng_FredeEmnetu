package ca.jrvs.apps.stockquote.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;


public class Main {
  private static final Logger log = Logger.getLogger(Main.class.getName());
  private static final String API_KEY = "3SXVOC66SF4SEMZ2";
  private static Connection createConn() throws SQLException {
    DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager("localhost","5432",
        "postgres","postgres", "password");
    return databaseConnectionManager.getConnection();
  }
  public static void main(String[] args) throws SQLException {
    QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper(API_KEY, new OkHttpClient());
    Quote quote = quoteHttpHelper.fetchQuoteInfo("AAPL");
    System.out.println(quote.getOpen());
  }

}

