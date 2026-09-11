package ca.jrvs.apps.trading;
import ca.jrvs.apps.trading.repository.MarketDataDao;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.service.QuoteService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;


@SpringBootApplication(exclude = {JdbcTemplateAutoConfiguration.class,
    DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
class Application implements CommandLineRunner {
  Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {

    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {

  }
}
