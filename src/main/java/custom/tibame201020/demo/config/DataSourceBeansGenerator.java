package custom.tibame201020.demo.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

@Component
public class DataSourceBeansGenerator {
    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;
    public DataSourceBeansGenerator(EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        this.entityManagerFactoryBuilder = entityManagerFactoryBuilder;
    }

    /**
     * generate DataSource
     * @return  dataSource
     */
    public DataSource generateDataSource(String url, String username, String password, String uniqueResourceName, String driverClassName) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();

        Properties properties = new Properties();
        properties.setProperty("URL", url);
        properties.setProperty("user", username);
        properties.setProperty("password", password);
        dataSource.setXaProperties(properties);

        dataSource.setUniqueResourceName(uniqueResourceName);
        dataSource.setXaDataSourceClassName(driverClassName);
        dataSource.setMaxPoolSize(10);
        dataSource.setMinPoolSize(3);

        return dataSource;
    }

    /**
     * generate EntityManagerFactory
     * @return entityManagerFactory
     */
    public LocalContainerEntityManagerFactoryBean generateEntityManager(DataSource dataSource, String persistenceUnit, String... packages) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create");

        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .properties(properties)
                .jta(true)
                .persistenceUnit(persistenceUnit)
                .packages(packages)
                .build();
    }

    /**
     * generate TransactionManager
     * @param entityManagerFactory entityManagerFactory bean
     * @return transactionManager
     */
    public PlatformTransactionManager generateTransactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}
