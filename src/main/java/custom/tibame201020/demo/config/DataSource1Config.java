package custom.tibame201020.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "custom.tibame201020.demo.repository1",
        entityManagerFactoryRef = "dataSource1EntityManager"
)
public class DataSource1Config {

    private final DataSourceBeansGenerator dataSourceBeansGenerator;

    private final String url;
    private final String username;
    private final String password;

    public DataSource1Config(DataSourceBeansGenerator dataSourceBeansGenerator, Environment environment) {
        this.dataSourceBeansGenerator = dataSourceBeansGenerator;
        this.url = environment.getRequiredProperty("datasource1.url");
        this.username = environment.getRequiredProperty("datasource1.username");
        this.password = environment.getRequiredProperty("datasource1.password");
    }
    @Bean(name = "dataSource1")
    public DataSource dataSource() {
        return dataSourceBeansGenerator.generateDataSource(
                url,
                username,
                password,
                "dataSource1",
                "org.h2.jdbcx.JdbcDataSource"
        );
    }

    @Bean(name = "dataSource1EntityManager")
    public LocalContainerEntityManagerFactoryBean dataSourceEntityManager() {
        return dataSourceBeansGenerator.generateEntityManager(
                dataSource(),
                "dataSource1PU",
                "custom.tibame201020.demo.entity1"
        );
    }

}
