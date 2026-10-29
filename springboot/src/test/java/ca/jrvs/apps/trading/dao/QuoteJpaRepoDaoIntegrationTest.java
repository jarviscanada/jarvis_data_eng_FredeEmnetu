package ca.jrvs.apps.trading.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.repository.QuoteJpaRepoDao;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
// ^ boots ONLY the JPA slice: real @Entity classes, real JpaRepository beans,
//   an embedded/test database. No @Service, no @Controller beans are loaded.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// ^ explicitly says "do NOT auto-swap in an H2 in-memory DB" — use whatever real
//   test datasource is configured in application-test.properties instead.
@ActiveProfiles("test")
// ^ loads application-test.properties instead of application.properties
@Transactional
public class QuoteJpaRepoDaoIntegrationTest {

    @Autowired
    QuoteJpaRepoDao quoteJpaRepoDao;

    Quote testQuote;

    @BeforeEach
    void setUp() {
        testQuote = new Quote();
        testQuote.setTicker("AAPL");
        testQuote.setCurrentPrice(150.00);
        testQuote.setChange(0.0);
        testQuote.setHigh(155.00);
        testQuote.setLow(148.00);
        testQuote.setOpen(150.00);
        testQuote.setPercentChange(0.0);
        testQuote.setPreviousClose(150.00);
        testQuote.setTimestamp(System.currentTimeMillis());
    }

    @AfterEach
    void tearDown() {
        quoteJpaRepoDao.deleteAll();
    }


    @Test
    void testFindQuoteByTicker_returnsMatchingQuote() {
        // Arrange
        quoteJpaRepoDao.save(testQuote);

        // Act
        Quote result = quoteJpaRepoDao.findQuoteByTicker("AAPL");

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(Double.valueOf(150.00), result.getCurrentPrice());
    }

    @Test
    void testFindByTicker_whenNotFound_returnsEmptyOptional() {
        // Act
        Optional<Quote> result = quoteJpaRepoDao.findByTicker("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
        // ^ explicit: no row was saved with this ticker, so Optional must be empty
    }

    @Test
    void testExistsByTicker_afterSave_returnsTrue() {
        // Arrange
        quoteJpaRepoDao.save(testQuote);

        // Act
        boolean exists = quoteJpaRepoDao.existsByTicker("AAPL");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testDeleteByTicker_removesRow() {
        // Arrange
        quoteJpaRepoDao.save(testQuote);

        // Act
        quoteJpaRepoDao.deleteByTicker("AAPL");

        // Assert
        assertFalse(quoteJpaRepoDao.existsByTicker("AAPL"));
    }


}
