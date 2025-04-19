package custom.tibame201020.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "custom.tibame201020.demo.repository2",
        entityManagerFactoryRef = "dataSource2EntityManager"
)
public class DataSource2Config {

    private final DataSourceBeansGenerator dataSourceBeansGenerator;
    private final String url;
    private final String username;
    private final String password;

    public DataSource2Config(DataSourceBeansGenerator dataSourceBeansGenerator, Environment environment) {
        this.dataSourceBeansGenerator = dataSourceBeansGenerator;
        this.url = environment.getRequiredProperty("datasource2.url");
        this.username = environment.getRequiredProperty("datasource2.username");
        this.password = environment.getRequiredProperty("datasource2.password");
    }
    @Bean(name = "dataSource2")
    public DataSource dataSource() {
        return dataSourceBeansGenerator.generateDataSource(
                url,
                username,
                password,
                "dataSource2",
                "org.h2.jdbcx.JdbcDataSource"
        );
    }

    @Bean(name = "dataSource2EntityManager")
    public LocalContainerEntityManagerFactoryBean dataSourceEntityManager() {
        return dataSourceBeansGenerator.generateEntityManager(
                dataSource(),
                "dataSource2PU",
                "custom.tibame201020.demo.entity2"
        );
    }

}
