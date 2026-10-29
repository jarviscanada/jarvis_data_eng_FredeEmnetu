package ca.jrvs.apps.trading.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.repository.MarketDataDao;
import org.junit.Test;

public class MarketDaoTest {

    private final MarketDataDao dao = new MarketDataDao();

    @Test
    public void jsonToQuote_parsesFinnhubStyleJson() throws Exception {
        String json = "{\"c\":261.74,\"d\":0.31,\"dp\":0.12,\"h\":263.31,\"l\":260.68,"
            + "\"o\":261.07,\"pc\":261.43,\"t\":1582641000}";

        Quote quote = dao.jsonToQuote(json);

        assertEquals(Double.valueOf(261.74), quote.getCurrentPrice());
        assertEquals(Double.valueOf(0.31), quote.getChange());
        assertEquals(Double.valueOf(0.12), quote.getPercentChange());
        assertEquals(Double.valueOf(263.31), quote.getHigh());
        assertEquals(Double.valueOf(260.68), quote.getLow());
        assertEquals(Double.valueOf(261.07), quote.getOpen());
        assertEquals(Double.valueOf(261.43), quote.getPreviousClose());
        // ticker isn't part of the Finnhub payload; findFinnQuoteByTicker sets it separately
        assertNull(quote.getTicker());
    }

    @Test(expected = com.fasterxml.jackson.core.JsonProcessingException.class)
    public void jsonToQuote_throws_onMalformedJson() throws Exception {
        dao.jsonToQuote("not valid json");
    }

    @Test
    public void executeHttpGet_throws_onNullUrl() {
        try {
            dao.executeHttpGet(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void executeHttpGet_throws_onEmptyUrl() {
        try {
            dao.executeHttpGet("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void findFinnQuoteByTicker_throws_onNullTicker() {
        try {
            dao.findFinnQuoteByTicker(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("Expected IllegalArgumentException but got " + e);
        }
    }

    @Test
    public void findFinnQuoteByTicker_throws_onEmptyTicker() {
        try {
            dao.findFinnQuoteByTicker("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            fail("Expected IllegalArgumentException but got " + e);
        }
    }
}