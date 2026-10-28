package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.SecurityOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityOrderJpaRepoDao extends JpaRepository<SecurityOrder, Integer> {
    SecurityOrder findQuoteById(Integer id);
    SecurityOrder save(SecurityOrder securityOrder);
    List<SecurityOrder> findAll();
    Optional<SecurityOrder> findById(Integer Id);
    boolean existsById(Integer Id);
    void deleteById(Integer Id);
    long count();
    void deleteAll();
    List<SecurityOrder> findByIdIn(List<Integer> ids);

}