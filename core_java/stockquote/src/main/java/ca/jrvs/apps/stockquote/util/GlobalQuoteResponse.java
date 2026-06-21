package ca.jrvs.apps.stockquote.util;

import ca.jrvs.apps.stockquote.dto.Quote;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper for Alpha Vantage API response.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalQuoteResponse {

  @JsonProperty("Global Quote")
  private Quote quote;

  public Quote getQuote() {
    return quote;
  }

  public void setQuote(Quote quote) {
    this.quote = quote;
  }
}