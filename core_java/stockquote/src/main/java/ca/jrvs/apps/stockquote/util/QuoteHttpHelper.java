package ca.jrvs.apps.stockquote.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QuoteHttpHelper {

  private static final Logger logger = LoggerFactory.getLogger(QuoteHttpHelper.class);
  private static final String BASE_URL =
  "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";

  private final String API_KEY;
  private final OkHttpClient client;

  public QuoteHttpHelper(String apiKey, OkHttpClient client) {
    this.API_KEY = apiKey;
    this.client = client;
  }

  /**
   * Fetch latest quote data from Alpha Vantage endpoint.
   *
   * @param symbol - stock ticker symbol (e.g. "AAPL", "MSFT")
   * @return Quote with latest data
   * @throws IllegalArgumentException if no data was found for the given symbol
   */
  public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
    if (symbol == null || symbol.isEmpty()) throw new IllegalArgumentException("Symbol cannot be null or empty");
    Quote quote;
    Request request = new Request.Builder()
        .url(BASE_URL + "&symbol=" + symbol + "&apikey="+ API_KEY)
        .get()
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (response.body() != null) {
        String json = response.body().string();
         GlobalQuoteResponse wrapper = JsonParser.toObjectFromJson(json, GlobalQuoteResponse.class);
          quote = wrapper.getQuote();

        if (quote == null || quote.getSymbol().isEmpty()) throw new IllegalArgumentException("Bad symbol");
        quote.setTimestamp(Timestamp.from(Instant.now()));
        return quote;

      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Api call response failed: " + e.getMessage());
    }
    throw new IllegalArgumentException("Failed to fetch quote");
  }
}
