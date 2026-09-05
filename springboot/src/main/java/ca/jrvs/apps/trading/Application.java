package ca.jrvs.apps.trading;
import ca.jrvs.apps.trading.dao.MarketDataDao;
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
  private static final String APIKEY = "da8qajpr01qvfj5thvdgda8qajpr01qvfj5thve0";
  Logger logger = LoggerFactory.getLogger(Application.class);


  @Value("${app.init.dailyList}")
  private String[] iniDailyList;

  @Autowired
  private JdbcTemplate jdbc;

  public static void main(String[] args) {

    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
  }
  @Override
  public void run(String... args) throws Exception {
    try {
      MarketDataDao dao = new MarketDataDao();
      Optional<String> ans = dao.executeHttpGet("https://finnhub.io/api/v1/quote?symbol=AAPL"
          + "&token=da8qajpr01qvfj5thvdgda8qajpr01qvfj5thve0");
      System.out.println(ans.get());

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
