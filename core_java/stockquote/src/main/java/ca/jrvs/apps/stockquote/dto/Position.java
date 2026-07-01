package ca.jrvs.apps.stockquote.dto;

public class Position {

  private String symbol; //id
  private int numOfShares;
  private double valuePaid; //total amount paid for shares

  public int getNumOfShares() {
    return numOfShares;
  }

  public void setNumOfShares(int numOfShares) {
    if (numOfShares < 0) throw new IllegalArgumentException("numOfShares cannot be negative");
    this.numOfShares = numOfShares;
  }

  public double getValuePaid() {
    return valuePaid;
  }

  public void setValuePaid(double valuePaid) {
    if (valuePaid < 0) throw new IllegalArgumentException("Value paid cannot be negative");
    this.valuePaid = valuePaid;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    if(symbol == null || symbol.isEmpty()) throw new IllegalArgumentException("symbol cannot be null or empty");
    this.symbol = symbol.trim().toUpperCase();
  }
}
