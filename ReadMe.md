#### dependencies
```
        <dependency>
	    <groupId>com.atomikos</groupId>
            <artifactId>transactions-jta</artifactId>
            <version>4.0.6</version>
	</dependency>

        <dependency>
            <groupId>com.atomikos</groupId>
            <artifactId>transactions-jdbc</artifactId>
            <version>4.0.6</version>
        </dependency>
```

#### entity manager: jta(true)
```
        return entityManagerFactoryBuilder
                .dataSource(dataSource)
                .properties(properties)
                .jta(true)
                .persistenceUnit(persistenceUnit)
                .packages(packages)
                .build();
```

####  jta transaction beans
```
    @Bean
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    @Bean
    public TransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);

        return userTransactionManager;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();

        TransactionManager atomikosTransactionManager = atomikosTransactionManager();
        return new JtaTransactionManager(userTransaction, atomikosTransactionManager);
    }
```
