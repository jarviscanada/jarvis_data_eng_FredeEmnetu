package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.dto.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountJpaRepoDao extends JpaRepository<Account, Integer> {
    Account findQuoteById(Integer id);
    Account save(Account account);
    List<Account> findAll();
    Optional<Account> findById(Integer Id);
    boolean existsById(Integer Id);
    void deleteById(Integer Id);
    long count();
    void deleteAll();
    List<Account> findByIdIn(List<Integer> ids);

}
