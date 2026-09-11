package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Position;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionJpaRepoDao extends JpaRepository<Position, Integer> {
    Position findQuoteById(Integer id);
    Position save(Position account);
    List<Position> findAll();
    Optional<Position> findById(Integer Id);
    boolean existsById(Integer Id);
    void deleteById(Integer Id);
    long count();
    void deleteAll();
    List<Position> findByIdIn(List<Integer> ids);
}
