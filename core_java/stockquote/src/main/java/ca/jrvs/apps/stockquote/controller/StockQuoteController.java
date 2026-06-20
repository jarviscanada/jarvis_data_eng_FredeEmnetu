package ca.jrvs.apps.stockquote.controller;

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
    // TODO: implement menu loop
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
    // TODO: implement
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
    // TODO: implement
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
    // TODO: implement
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
    // TODO: implement
  }
}
