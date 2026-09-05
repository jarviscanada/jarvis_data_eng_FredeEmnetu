package ca.jrvs.apps.trading.repository;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.dto.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderJpaRepo extends JpaRepository<Trader, Integer> {


}
