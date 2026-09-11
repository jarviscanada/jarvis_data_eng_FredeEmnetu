package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Trader;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderJpaRepoDao extends JpaRepository<Trader, Integer> {
    Trader findQuoteById(Integer id);
    Trader save(Trader trader);
    List<Trader> findAll();
    Optional<Trader> findById(Integer Id);
    boolean existsById(Integer Id);
    void deleteById(Integer Id);
    long count();
    void deleteAll();
    List<Trader> findByIdIn(List<Integer> ids);

}
