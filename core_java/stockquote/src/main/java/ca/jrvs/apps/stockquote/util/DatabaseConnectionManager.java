package ca.jrvs.apps.stockquote.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnectionManager {

  private final String url;
  private final String user;
  private final String password;
  private static final Logger log = Logger.getLogger(DatabaseConnectionManager.class.getName());
  public static final String exceptionFormat = "exception in %s, message: %s, code %s";
  private static Connection connection;
  /**
   *
   * @param host     - database host (e.g. "localhost")
   * @param port     - database port (e.g. "5432")
   * @param database - database name (e.g. "stock_quote")
   * @param user     - database username
   * @param password - database password
   */
  public DatabaseConnectionManager(String host, String port, String database,
      String user, String password) {
    this.url = "jdbc:postgresql://"+host+":"+port+"/"+database;
    this.user = user;
    this.password = password;
  }

  /**
   *
   * @return a live JDBC Connection
   * @throws SQLException if connection fails
   */
  public Connection getConnection() throws SQLException {
    if (connection == null) {
      try {
        connection = DriverManager.getConnection(url,user, password);
      } catch (SQLException e) {
        handleSqlException("DatabaseConnectionManager" , e, log);
      }
    }
    return connection;
  }
  public static void handleSqlException(String method, SQLException e, Logger log){
    log.warning(String.format(exceptionFormat, method, e.getMessage(), e.getErrorCode()));
    throw new RuntimeException(e);
  }
}