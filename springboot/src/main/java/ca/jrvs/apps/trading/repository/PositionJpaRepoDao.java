package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Position;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionJpaRepoDao extends JpaRepository<Position, Integer> {
    Position findQuoteById(Integer id);
    List<Position> findAll();
    Optional<Position> findById(Integer Id);
    boolean existsById(Integer Id);
    long count();
    List<Position> findByIdIn(List<Integer> ids);
}
