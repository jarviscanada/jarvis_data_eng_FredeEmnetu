package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteJpaRepo extends JpaRepository<Quote, Integer> {

//  Quote getAccountByTraderId(Integer ticker);

}