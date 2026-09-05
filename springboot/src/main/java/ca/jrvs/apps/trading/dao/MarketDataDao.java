package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.dto.Quote;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.util.UriComponentsBuilder;


public class MarketDataDao {

  @Value("${finnhub.api.base-url}")
  private static String baseUrl;

  @Value("${finnhub.api.token}")
  private static String apiToken;

  private static UriComponentsBuilder url = UriComponentsBuilder
      .fromHttpUrl(baseUrl + "/quote")
      .queryParam("token", apiToken);

  /**
   * Get a Finnhub Quote
   *
   * @param ticker
   * @throws IllegalArgumentException if a given ticker is invalid
   * @throws DataRetrievalFailureException if HTTP request failed
   */
  public Optional<Quote> findById(String ticker) {
    if(Objects.isNull(ticker) || ticker.isEmpty()) {
      throw new IllegalArgumentException("Ticker cannot be null or empty");
    }
    String uri = url
        .queryParam("symbol", ticker).toUriString();

    try {
      Optional<String> repsone = executeHttpGet(uri);


    }


  }

  /**
   * Get quotes from Finnhub
   * @param tickers is a list of tickers
   * @return a list of IexQuote objects
   * @throws IllegalArgumentException if a given ticker is invalid
   * @throws DataRetrievalFailureException if HTTP request failed
   */
  public List<Quote> findAllById(Iterable<String> tickers) {
    //TODO

    return new List<Quote>();
  }

  /**
   * Execute a GET request and return http entity/body as a string
   * Tip: use EntitiyUtils.toString to process HTTP entity
   * TODO
   * FIXME Does not adhere to defensive coding practices
   *
   * @param url resource URL
   * @return http response body or Optional.Empty for 404 response
   * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected
   */
  public Optional<String> executeHttpGet(String url) {
    try {
      String body;
      HttpClient httpClient = getHttpClient();
      HttpGet request = new HttpGet(url);

      HttpResponse response = httpClient.execute(request);
      HttpEntity entity = response.getEntity();
      body = EntityUtils.toString(entity);
      return Optional.ofNullable(body);



    }catch(DataRetrievalFailureException e) {
      throw new DataRetrievalFailureException(Objects.requireNonNull(e.getMessage()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Borrow a HTTP client from the HttpClientConnectionManager
   * @return a HttpClient
   */
  private HttpClient getHttpClient() {
    // Manager allows connection to host to stay open, instead of opening TCP
    // over and over
    BasicHttpClientConnectionManager cm = new BasicHttpClientConnectionManager();

    return HttpClients
                .custom()
                .setConnectionManager(cm)
                .build();
  }

}



