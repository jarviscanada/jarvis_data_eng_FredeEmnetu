package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.service.TraderAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


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
//    TraderAccountService  traderAccountService = new TraderAccountService();

  }
}
