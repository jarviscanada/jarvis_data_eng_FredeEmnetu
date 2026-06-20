package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.DTO.Position;
import ca.jrvs.apps.stockquote.DTO.Quote;
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
   *
   * TODO: Implement a menu loop with these options:
   *
   * === Stock Quote App ===
   * --- Menu ---
   * 1. View stock quote
   * 2. Buy shares
   * 3. Sell shares
   * 4. View portfolio
   * q. Quit
   * Enter choice:
   *
   * Steps:
   * 1. Create a Scanner for user input
   * 2. Loop until user enters "q" or "Q"
   * 3. Based on user choice, call the appropriate handler method below
   * 4. Close the scanner when done
   *
   * Remember: the controller only orchestrates — it calls services and displays results.
   * No business logic here.
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
      String input = scanner.next();

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
          running = false;
          break;
        default:
            System.out.println("Invalid choice");
      }
    }
    scanner.close();
  }

  /**
   * TODO: Handle viewing a stock quote.
   *
   * Steps:
   * 1. Prompt for ticker symbol
   * 2. Call quoteService.fetchQuoteDataFromAPI(ticker)
   * 3. If quote is present, display: price, open, high, low, volume,
   *    previous close, change, change percent, trading day
   * 4. If empty, print "Could not find quote for: " + ticker
   *
   * Hint: use scanner.nextLine().trim().toUpperCase() for the ticker
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
          + "Price: " + quote.getPrice()
          + "Open: " + quote.getOpen()
          + "High: " + quote.getHigh()
          + "Low: " + quote.getLow()
          + "Volume: " + quote.getVolume()
          + "Previous Close: " + quote.getPreviousClose()
          + "Change: " + quote.getChange()
          + "Change Precent: " + quote.getChangePercent()
          + "Trading Day: " + quote.getChangePercent()
      );
    }


  }

  /**
   * TODO: Handle buying shares.
   *
   * Steps:
   * 1. Prompt for ticker symbol
   * 2. Fetch the latest quote first (this also satisfies the FK constraint)
   *    - If quote not found, print error and return
   * 3. Display the current price
   * 4. Prompt for number of shares (parse as int)
   *    - If invalid number, print error and return
   * 5. Call positionService.buy(ticker, shares, quote.getPrice())
   * 6. Display confirmation with total position
   * 7. Wrap in try/catch for IllegalArgumentException
   */
  private void handleBuy(Scanner scanner) {
    System.out.println("Please enter a ticker symbol: ");
    String ticker = scanner.nextLine();
    ticker = ticker.trim().toUpperCase();
    try{
      Optional<Quote> optionalQuote = quoteService.fetchQuoteDataFromAPI(ticker);

      if(!optionalQuote.isPresent()) throw new IllegalStateException("HandleBuy: Illegal ticker entered");
      Quote quote = optionalQuote.get();
      System.out.println("Current price: " + quote.getPrice());

      System.out.println("Enter number of shares: ");
      int numberOfShares = scanner.nextInt();
      if (numberOfShares < 0) throw new IllegalArgumentException("Please enter a valid number of shares");
      Position position = positionService.buy(ticker, numberOfShares, quote.getPrice());

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
   * Steps:
   * 1. Prompt for ticker symbol
   * 2. Call positionService.sell(ticker)
   * 3. Print confirmation
   * 4. Catch IllegalArgumentException and display error
   *    (e.g. "Sell failed: You do not own any shares of ZZZZ")
   */
  private void handleSell(Scanner scanner) {
    System.out.println("Please enter a ticker symbol: ");
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
            "Ticker: " + p.getSymbol()
                + "\nShares: " + p.getNumOfShares()
                + "\nValue: " + p.getValuePaid()
      );
      }
    }

  }
}
