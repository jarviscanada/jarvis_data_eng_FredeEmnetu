package ca.jrvs.apps.practice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, Integer> {

  Account getAccountByTraderId(Integer traderId);

}
