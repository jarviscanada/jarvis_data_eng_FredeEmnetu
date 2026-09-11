package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Quote;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class MarketDataDao {

    @Value("${finnhub.api.base-url}")
    private String baseUrl;

    @Value("${finnhub.api.token}")
    private String apiToken;

    private static final Logger logger = LoggerFactory.getLogger(MarketDataDao.class);

    private String buildUrl(String symbol) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("token", apiToken)
            .queryParam("symbol", symbol)
            .toUriString();
    }


    public Quote jsonToQuote(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Quote.class);
    }

    /**
     * Get a Finn hub Quote
     *
     * @param ticker
     * @throws IllegalArgumentException      if a given ticker is invalid
     * @throws DataRetrievalFailureException if HTTP request failed
     **/

    public Optional<Quote> findFinnQuoteByTicker(String ticker) throws DataRetrievalFailureException , IOException {
        if (Objects.isNull(ticker) || ticker.isEmpty()) {
            throw new IllegalArgumentException("Ticker cannot be null or empty");
        }

        try {
            Optional<String> response = executeHttpGet(buildUrl(ticker));
            if (!response.isPresent()) {
                return Optional.empty();
            }

            Quote quote = jsonToQuote(response.get());
            quote.setTicker(ticker);

            return Optional.of(quote);

        } catch (DataRetrievalFailureException e) {
            throw new DataRetrievalFailureException("Unable to fetch Api: " + e.getMessage());
        }catch(IOException e) {
            throw new IOException("Error parsing Json" +  e.getMessage());
        }
    }

    /**
     * Get quotes from Finn hub
     * @param tickers is a list of tickers
     * @return a list of Finn hub objects
     * @throws IllegalArgumentException if a given ticker is invalid
     * @throws DataRetrievalFailureException if HTTP request failed
     **/
    public List<Quote> findAllById(Iterable<String> tickers) {
        List<Quote> quotes = new ArrayList<>();
        StreamSupport
            .stream(tickers.spliterator(), false)
            .map(ticker -> {
                try {
                    return findById(ticker);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(quotes::add);

        return quotes;
    }

    /**
     * Execute a GET request and return http entity/body as a string Tip: use EntitiyUtils.toString
     * to process HTTP entity
     *
     * @param url resource URL
     * @return http response body or Optional.Empty for 404 response
     * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected
     */
    public Optional<String> executeHttpGet(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url must not be null or blank");
        }

        HttpClient httpClient = getHttpClient();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String body = (entity != null) ? EntityUtils.toString(entity) : null;
            if (statusCode < 200 || statusCode >= 300) {
                logger.warn(
                    String.format("HTTP GET to %s returned status %s: %s", url, statusCode, body));
                return Optional.empty();
            }

            return Optional.ofNullable(body).filter(s -> !s.isEmpty());

        } catch (IOException e) {
            logger.error("Failed to execute HTTP GET to ", url, e);
            throw new DataRetrievalFailureException("Failed to execute HTTP GET to " + url, e);
        }
    }


    /**
     * Borrow an HTTP client from the HttpClientConnectionManager
     *
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



