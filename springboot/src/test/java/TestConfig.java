import ca.jrvs.apps.trading.config.MarketDataConfig;
import javax.sql.DataSource;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ca.jrvs.apps.trading.dao","ca.jrvs.apps.trading.service"})
public class TestConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver}")
    private String driverClassName;

    @Value("${finnhub.api.base}")
    private String finnhubBaseUrl;

    @Value("${finnhub.api.token}")
    private String finnhubApiKey;

    @Bean
    public DataSource dataSourceTest() {
        return DataSourceBuilder.create()
            .url(dbUrl)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build();
    }

    @Bean
    public MarketDataConfig marketDataConfig() {
        MarketDataConfig marketDataConfig = new MarketDataConfig();
        marketDataConfig.setHost(finnhubBaseUrl);
        marketDataConfig.setToken(finnhubApiKey);
        return marketDataConfig;
    }

    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(50);
        cm.setMaxTotal(50);
        return cm;
    }
}