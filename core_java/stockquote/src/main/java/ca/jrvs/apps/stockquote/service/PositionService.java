package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.Implementation.PositionDao;
import ca.jrvs.apps.stockquote.dto.Position;
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
   * @return All positions
   */
  public Iterable<Position> viewPortfolio() {
    return dao.findAll();
  }

  /**
   * Sells all shares of the given ticker symbol.
   *
   * @param ticker - stock symbol to sell
   */
  public void sell(String ticker) {
    if (ticker == null || ticker.isEmpty()) throw new IllegalArgumentException("PositionDao.sell: Ticker is null or empty");

    Optional<Position> prevPosition = dao.findById(ticker);

    if (!prevPosition.isPresent()){

      throw new IllegalArgumentException("You do not own any shares of: " + ticker);
    }
    dao.deleteById(prevPosition.get().getSymbol());
  }




}
