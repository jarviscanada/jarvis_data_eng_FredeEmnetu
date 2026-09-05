package ca.jrvs.apps.trading.config;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AppConfig {

  private Logger logger = LoggerFactory.getLogger(AppConfig.class);

  @Autowired
  private DataSource dataSource; // DataSource to talk to database

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("ca.jrvs.apps.trading.dto"); // wherever your @Entity classes live

    /*
    JPA is an interface — Hibernate is the implementation that does the work.
    This line says "use Hibernate as the JPA provider." (There are other JPA providers like EclipseLink,
    but Hibernate is by far the most common, and it's what Spring Boot uses by default too.)
     */
    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    Properties props = new Properties(); // A glorified String-to-String Map, that set Properties Requires
    /*
    Controls whether Hibernate tries to auto-create/modify your tables based on your @Entity classes.
    validate represents "check my entity classes match the existing tables, but don't touch the schema"
     */
    props.setProperty("hibernate.hbm2ddl.auto", "validate");
    /*
    Tells Hibernate what "flavor" of SQL to generate,
    since different databases have slightly different SQL syntax. In this case, Postgres Dialect
     */
    props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    em.setJpaProperties(props);

    return em;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    /*
    JpaTransactionManager is the object that actually
    starts/commits/rolls back transactions against your EntityManagerFactory.
     */
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    /*
    Tells the transaction manager "when you start a transaction, get an EntityManager from EntityManagerFactory
    (the one wired to DataSource, entities and Postgres dialect)."
     */
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }
}
