package ca.jrvs.apps.stockquote.service;


import ca.jrvs.apps.stockquote.dao.Implementation.PositionDao;
import ca.jrvs.apps.stockquote.dto.Position;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PositionServiceUnitTest {

  @Mock
  private PositionDao mockDao;

  private PositionService positionService;

  @Before
  public void setUp() {
    positionService = new PositionService(mockDao);
  }

  // ---------- buy() ----------

  @Test
  public void buy_newPosition_savesAndReturnsPosition() {
    when(mockDao.findById("AAPL")).thenReturn(Optional.empty());
    when(mockDao.save(any(Position.class))).thenAnswer(i -> i.getArgument(0));

    Position result = positionService.buy("AAPL", 10, 150.0);

    assertEquals("AAPL", result.getSymbol());
    assertEquals(10, result.getNumOfShares());
    verify(mockDao).save(any(Position.class));
  }

  @Test
  public void buy_existingPosition_accumulatesShares() {
    Position existing = new Position();
    existing.setSymbol("AAPL");
    existing.setNumOfShares(10);
    existing.setValuePaid(1500.0);

    when(mockDao.findById("AAPL")).thenReturn(Optional.of(existing));
    when(mockDao.save(any(Position.class))).thenAnswer(i -> i.getArgument(0));

    Position result = positionService.buy("AAPL", 5, 160.0);

    assertEquals(15, result.getNumOfShares());
    assertEquals(1500.0 + (5 * 160.0), result.getValuePaid(), 0.001);
    verify(mockDao).save(existing);
  }

  @Test(expected = IllegalArgumentException.class)
  public void buy_nullTicker_throwsIllegalArgumentException() {
    positionService.buy(null, 10, 150.0);
  }

  // ---------- sell() ----------

  @Test
  public void sell_existingPosition_deletesById() {
    Position existing = new Position();
    existing.setSymbol("AAPL");

    when(mockDao.findById("AAPL")).thenReturn(Optional.of(existing));

    positionService.sell("AAPL");

    verify(mockDao).deleteById("AAPL");
  }

  @Test(expected = IllegalArgumentException.class)
  public void sell_nonExistingPosition_throwsIllegalArgumentException() {
    when(mockDao.findById("FAKE")).thenReturn(Optional.empty());

    positionService.sell("FAKE");
  }

  @Test(expected = IllegalArgumentException.class)
  public void sell_emptyTicker_throwsIllegalArgumentException() {
    positionService.sell("");
  }

  // ---------- viewPortfolio() ----------

  @Test
  public void viewPortfolio_returnsAllPositions() {
    Position p1 = new Position();
    p1.setSymbol("AAPL");
    Position p2 = new Position();
    p2.setSymbol("GOOG");

    when(mockDao.findAll()).thenReturn(Arrays.asList(p1, p2));

    Iterable<Position> result = positionService.viewPortfolio();

    int count = 0;
    for (Position p : result) count++;
    assertEquals(2, count);
    verify(mockDao).findAll();
  }

  @Test
  public void viewPortfolio_emptyPortfolio_returnsEmptyIterable() {
    when(mockDao.findAll()).thenReturn(Arrays.asList());

    Iterable<Position> result = positionService.viewPortfolio();

    assertFalse(result.iterator().hasNext());
  }
}