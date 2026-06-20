package ca.jrvs.apps.stockquote.DTO;

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
  private double open;

  @JsonProperty("03. high")
  private double high;

  @JsonProperty("04. low")
  private double low;

  @JsonProperty("05. price")
  private double price;

  @JsonProperty("06. volume")
  private long volume;

  @JsonProperty("07. latest trading day")
  private Timestamp latestTradingDay;

  @JsonProperty("08. previous close")
  private double previousClose;

  @JsonProperty("09. change")
  private double change;

  @JsonProperty("10. change percent")
  private String changePercent;

  private Timestamp timestamp;

  @JsonProperty("01. symbol")
  public String getSymbol() { return symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol.trim().toUpperCase(); }

  @JsonProperty("02. open")
  public double getOpen() { return open; }
  public void setOpen(double open) { this.open = open; }

  @JsonProperty("03. high")
  public double getHigh() { return high; }
  public void setHigh(double high) { this.high = high; }

  @JsonProperty("04. low")
  public double getLow() { return low; }
  public void setLow(double low) { this.low = low; }

  @JsonProperty("05. price")
  public double getPrice() { return price; }
  public void setPrice(double price) { this.price = price; }

  @JsonProperty("06. volume")
  public long getVolume() { return volume; }
  public void setVolume(long volume) { this.volume = volume; }

  @JsonProperty("07. latest trading day")
  public Timestamp getLatestTradingDay() { return latestTradingDay; }
  public void setLatestTradingDay(Timestamp latestTradingDay) { this.latestTradingDay = latestTradingDay; }

  @JsonProperty("08. previous close")
  public double getPreviousClose() { return previousClose; }
  public void setPreviousClose(double previousClose) { this.previousClose = previousClose; }

  @JsonProperty("09. change")
  public double getChange() { return change; }
  public void setChange(double change) { this.change = change; }

  @JsonProperty("10. change percent")
  public String getChangePercent() { return changePercent; }
  public void setChangePercent(String changePercent) { this.changePercent = changePercent; }

  public Timestamp getTimestamp() { return timestamp; }
  public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}