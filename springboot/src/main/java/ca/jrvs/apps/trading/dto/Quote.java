package ca.jrvs.apps.trading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Ticker;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "quote")
public class Quote {


    @Id
    @Column(name="ticker")
    private String ticker;


    @Column(name = "timestamp")
    private Timestamp timestamp;


    @Column(name = "current_price")
    @JsonProperty("c")
    private Double currentPrice;

    @Column(name = "change")
    @JsonProperty("d")
    private Double change;

    @Column(name = "percent_change")
    @JsonProperty("dp")
    private Double percentChange;

    @Column(name = "high")
    @JsonProperty("h")
    private Double high;

    @Column(name = "low")
    @JsonProperty("l")
    private Double low;

    @Column(name = "open")
    @JsonProperty("o")
    private Double open;

    @Column(name = "previous_close")
    @JsonProperty("pc")
    private Double previousClose;


    public String getTicker() {
        return this.ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @JsonProperty("t")
    public void setTimestamp(long epochSeconds) {
        this.timestamp = Timestamp.from(Instant.ofEpochSecond(epochSeconds));
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(Double percentChange) {
        this.percentChange = percentChange;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }
    @Override
    public String toString() {
        return "Quote{" +
            "timestamp=" + timestamp +
            ", current_price=" + currentPrice +
            ", change=" + change +
            ", percent_change=" + percentChange +
            ", high=" + high +
            ", low=" + low +
            ", open=" + open +
            ", previous_close=" + previousClose +
            '}';
    }






}