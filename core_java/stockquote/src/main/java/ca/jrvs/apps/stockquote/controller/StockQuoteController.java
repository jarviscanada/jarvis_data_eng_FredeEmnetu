package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.dto.Position;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Scanner;
public class StockQuoteController {

  private static final Logger logger = LoggerFactory.getLogger(StockQuoteController.class);

  private final QuoteService quoteService;
  private final PositionService positionService;

  public StockQuoteController(QuoteService quoteService, PositionService positionService) {
    this.quoteService = quoteService;
    this.positionService = positionService;
  }

  /**
   * User interface for the stock quote application.
   */
  public void initClient() {
    Scanner scanner = new Scanner(System.in);

    boolean running = true;
    while(running){
      System.out.println("=== Stock Quote App ===");
      System.out.println("--- Menu ---");
      System.out.println("1. View stock quote");
      System.out.println("2. Buy shares");
      System.out.println("3. Sell shares");
      System.out.println("4. View portfolio");
      System.out.println("q. Quit");
      System.out.print("Enter choice: ");
      String input = scanner.nextLine();

      switch (input.trim()){
        case "1":
          handleViewQuote(scanner);
          break;
        case "2":
          handleBuy(scanner);
          break;
        case "3":
          handleSell(scanner);
          break;
        case "4":
          handleViewPortfolio();
          break;
        case "q":
        case "Q":
          System.out.println("Thanks for stopping by!!");
          running = false;
          break;
        default:
            System.out.println("Invalid choice");
      }
    }
    scanner.close();
  }

  /**
   *
   * @param scanner to take input
   */
  private void handleViewQuote(Scanner scanner) {
    System.out.print("Please enter a ticker: ");
    String ticker = scanner.nextLine();

    Optional<Quote> optionalQuote = quoteService.fetchQuoteDataFromAPI(ticker.trim().toUpperCase());

    if(!optionalQuote.isPresent()) {
      System.out.println("Could not find quote for: " + ticker);
    }else{
      Quote quote = optionalQuote.get();
      System.out.println(
          "Ticker: " + quote.getSymbol()
          + "\nPrice: " + quote.getPrice()
          + "\nOpen: " + quote.getOpen()
          + "\nHigh: " + quote.getHigh()
          + "\nLow: " + quote.getLow()
          + "\nVolume: " + quote.getVolume()
          + "\nPrevious Close: " + quote.getPreviousClose()
          + "\nChange: " + quote.getChange()
          + "\nChange Precent: " + quote.getChangePercent()
          + "\nTrading Day: " + quote.getChangePercent()
      );
    }
  }

  /**
   * TODO: Handle buying shares.
   *
   * @param scanner to recieve input
   */
  private void handleBuy(Scanner scanner) {
    System.out.print("Please enter a ticker symbol: ");
    String ticker = scanner.nextLine();
    ticker = ticker.trim().toUpperCase();
    try{
      Optional<Quote> optionalQuote = quoteService.fetchQuoteDataFromAPI(ticker);

      if(!optionalQuote.isPresent()) throw new IllegalStateException("HandleBuy: Illegal ticker entered");
      Quote quote = optionalQuote.get();
      System.out.println("Current price: " + quote.getPrice());

      System.out.print("Enter number of shares: ");
      int numberOfShares = Integer.parseInt(scanner.nextLine());
      if (numberOfShares < 0) throw new IllegalArgumentException("Please enter a valid number of shares");
      Position position = positionService.buy(ticker, numberOfShares, quote.getPrice()*numberOfShares);

      System.out.println(
          "Ticker: " + position.getSymbol()
          + "\nNumber of Shares: " + position.getNumOfShares()
          + "\nValue: " + position.getValuePaid()
      );

    } catch (IllegalArgumentException e) {
      logger.warn("StockQuoteController.handleBuy:  Unable to buy stock");
    }

  }

  /**
   * TODO: Handle selling shares.
   *
   * @param scanner to recieve input
   */
  private void handleSell(Scanner scanner) {
    System.out.print("Please enter a ticker symbol: ");
    String ticker = scanner.nextLine();

    try{
      positionService.sell(ticker);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Sell failed: You do not own any shares of ZZZZ");
    }


  }

  /**
   * TODO: Handle viewing the portfolio.
   *
   * Steps:
   * 1. Call positionService.viewPortfolio()
   * 2. Loop through positions and display: ticker, shares, value paid
   * 3. If no positions, print "No positions held."
   */
  private void handleViewPortfolio() {
    Iterable<Position> positions = positionService.viewPortfolio();
    if(!positions.iterator().hasNext()){
      System.out.println("No positions held");
    }else{
      for(Position p : positions){
        System.out.println(
            "============================================"
            + "\nTicker: " + p.getSymbol()
                + "\nShares: " + p.getNumOfShares()
                + "\nValue: " + p.getValuePaid()
            +"\n============================================"
            + "\n"
      );
      }
    }

  }
}
