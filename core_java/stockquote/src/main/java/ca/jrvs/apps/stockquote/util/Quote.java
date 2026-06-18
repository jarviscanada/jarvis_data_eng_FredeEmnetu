package ca.jrvs.apps.stockquote.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "symbol",
    "open",
    "high",
    "low",
    "price",
    "volume",
    "latestTradingDay",
    "previousClose",
    "change",
    "changePercent"
})
public class Quote {

  @JsonProperty("01. symbol")
  private String symbol;

  @JsonProperty("02. open")
  private String open;

  @JsonProperty("03. high")
  private String high;

  @JsonProperty("04. low")
  private String low;

  @JsonProperty("05. price")
  private String price;

  @JsonProperty("06. volume")
  private String volume;

  @JsonProperty("07. latest trading day")
  private String latestTradingDay;

  @JsonProperty("08. previous close")
  private String previousClose;

  @JsonProperty("09. change")
  private String change;

  @JsonProperty("10. change percent")
  private String changePercent;

  private Timestamp timestamp;

  @JsonProperty("01. symbol")
  public String getSymbol() { return symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol; }

  @JsonProperty("02. open")
  public String getOpen() { return open; }
  public void setOpen(String open) { this.open = open; }

  @JsonProperty("03. high")
  public String getHigh() { return high; }
  public void setHigh(String high) { this.high = high; }

  @JsonProperty("04. low")
  public String getLow() { return low; }
  public void setLow(String low) { this.low = low; }

  @JsonProperty("05. price")
  public String getPrice() { return price; }
  public void setPrice(String price) { this.price = price; }

  @JsonProperty("06. volume")
  public String getVolume() { return volume; }
  public void setVolume(String volume) { this.volume = volume; }

  @JsonProperty("07. latest trading day")
  public String getLatestTradingDay() { return latestTradingDay; }
  public void setLatestTradingDay(String latestTradingDay) { this.latestTradingDay = latestTradingDay; }

  @JsonProperty("08. previous close")
  public String getPreviousClose() { return previousClose; }
  public void setPreviousClose(String previousClose) { this.previousClose = previousClose; }

  @JsonProperty("09. change")
  public String getChange() { return change; }
  public void setChange(String change) { this.change = change; }

  @JsonProperty("10. change percent")
  public String getChangePercent() { return changePercent; }
  public void setChangePercent(String changePercent) { this.changePercent = changePercent; }

  public Timestamp getTimestamp() { return timestamp; }
  public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}