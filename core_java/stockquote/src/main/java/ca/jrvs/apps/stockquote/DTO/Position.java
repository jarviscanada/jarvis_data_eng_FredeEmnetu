package ca.jrvs.apps.stockquote.DTO;

public class Position {

  private String symbol; //id
  private int numOfShares;
  private double valuePaid; //total amount paid for shares

  public int getNumOfShares() {
    return numOfShares;
  }

  public void setNumOfShares(int numOfShares) {
    this.numOfShares = numOfShares;
  }

  public double getValuePaid() {
    return valuePaid;
  }

  public void setValuePaid(double valuePaid) {
    this.valuePaid = valuePaid;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {this.symbol = symbol.trim().toUpperCase(); }
}
