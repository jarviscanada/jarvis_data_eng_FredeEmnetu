package ca.jrvs.apps.trading.dto;

public class MarketOrder {

    private String ticker;
    private int size;
    private int traderId;
    private enum Option { BUY, SELL }

}