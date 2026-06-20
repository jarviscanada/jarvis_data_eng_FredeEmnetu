package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.DAO.Implementation.PositionDao;
import ca.jrvs.apps.stockquote.DTO.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PositionService {

  private static final Logger logger = LoggerFactory.getLogger(PositionService.class);

  private final PositionDao dao;

  public PositionService(PositionDao dao) {
    this.dao = dao;
  }

  /**
   * Processes a buy order and updates the database accordingly.
   *
   * TODO: Implement this method. Steps:
   *
   * 1. Validate inputs:
   *    - ticker must not be null or empty → throw IllegalArgumentException
   *    - numberOfShares must be positive → throw IllegalArgumentException
   *    - price must be positive → throw IllegalArgumentException
   *
   * 2. Calculate cost: numberOfShares * price
   *
   * 3. Check if a position already exists: dao.findById(ticker)
   *
   * 4. If position EXISTS (accumulate):
   *    - New shares = existing shares + numberOfShares
   *    - New valuePaid = existing valuePaid + cost
   *
   * 5. If position DOES NOT exist (create new):
   *    - Shares = numberOfShares
   *    - ValuePaid = cost
   *
   * 6. Save and return the position via dao.save(position)
   *
   * @param ticker         - stock symbol
   * @param numberOfShares - number of shares to buy
   * @param price          - current price per share
   * @return The position in our database after processing the buy
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    Position position = new Position();

    if (ticker == null || ticker.trim().isEmpty()) {
      throw new IllegalArgumentException("Position.buy: Invalid ticker");
    }
    if (numberOfShares < 0) throw new IllegalArgumentException("Position.buy: Number of shares must be positive");

    if (price < 0) throw new IllegalArgumentException("Position.buy: Price must be positive");

    double cost = (double) numberOfShares * price;

    Optional<Position> prevPosition = dao.findById(ticker);

    if(prevPosition.isPresent()){
      position = prevPosition.get();
      position.setNumOfShares(position.getNumOfShares() + numberOfShares);
      position.setValuePaid(position.getValuePaid() + cost);
      dao.save(position);
      return position;
    }
    position.setSymbol(ticker);
    position.setNumOfShares(numberOfShares);
    position.setValuePaid(price);
    dao.save(position);
    return position;
  }

  /**
   * Returns all positions in the portfolio.
   *
   * TODO: Implement using dao.findAll()
   *
   * @return All positions
   */
  public Iterable<Position> viewPortfolio() {
    return dao.findAll();
  }

  /**
   * Sells all shares of the given ticker symbol.
   *
   * TODO: Implement this method. Steps:
   *
   * 1. Validate: ticker must not be null or empty → throw IllegalArgumentException
   *
   * 2. Check if position exists: dao.findById(ticker)
   *
   * 3. If position DOES NOT exist → throw IllegalArgumentException
   *    with message "You do not own any shares of " + ticker
   *    (Don't silently succeed — the user should know the sell failed)
   *
   * 4. If position exists → delete it: dao.deleteById(ticker)
   *
   * @param ticker - stock symbol to sell
   */
  public void sell(String ticker) {
    if (ticker == null || ticker.isEmpty()) throw new IllegalArgumentException("PositionDao.sell: Ticker is null or empty");

    Optional<Position> prevPosition = dao.findById(ticker);

    if (!prevPosition.isPresent()){

      throw new IllegalArgumentException("You do not own any shares of: " + ticker);
    }
    dao.deleteAll();
  }




}
