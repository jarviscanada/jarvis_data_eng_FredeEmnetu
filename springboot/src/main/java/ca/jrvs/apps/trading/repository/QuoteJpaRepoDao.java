package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Quote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteJpaRepoDao extends JpaRepository<Quote, Integer> {

    // These methods are predefined by JPA, though I've placed them here for documentation

    Quote findQuoteByTicker(String ticker);
    Quote save(Quote quote);
    List<Quote> findAll();
    Optional<Quote> findByTicker(String ticker);
    boolean existsByTicker(String ticker);
    void deleteByTicker(String ticker);
    long count();
    void deleteAll();
    List<Quote> findByTickerIn(List<String> ids);

}