package custom.tibame201020.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "custom.tibame201020.demo.repository3",
        entityManagerFactoryRef = "dataSource3EntityManager"
)
public class DataSource3Config {

    private final DataSourceBeansGenerator dataSourceBeansGenerator;
    private final String url;
    private final String username;
    private final String password;

    public DataSource3Config(DataSourceBeansGenerator dataSourceBeansGenerator, Environment environment) {
        this.dataSourceBeansGenerator = dataSourceBeansGenerator;
        this.url = environment.getRequiredProperty("datasource3.url");
        this.username = environment.getRequiredProperty("datasource3.username");
        this.password = environment.getRequiredProperty("datasource3.password");
    }

    @Bean(name = "dataSource3")
    public DataSource dataSource() {
        return dataSourceBeansGenerator.generateDataSource(
                url,
                username,
                password,
                "dataSource3",
                "org.h2.jdbcx.JdbcDataSource"
        );
    }

    @Bean(name = "dataSource3EntityManager")
    public LocalContainerEntityManagerFactoryBean dataSourceEntityManager() {
        return dataSourceBeansGenerator.generateEntityManager(
                dataSource(),
                "dataSource3PU",
                "custom.tibame201020.demo.entity3"
        );
    }

}
