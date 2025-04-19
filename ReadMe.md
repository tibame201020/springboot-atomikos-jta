# Spring Boot 3 + Hibernate + Atomikos JTA

## Dependencies
```xml
<dependency>
    <groupId>com.atomikos</groupId>
    <artifactId>transactions-spring-boot3</artifactId>
    <version>6.0.0</version>
</dependency>

<dependency>
    <groupId>com.atomikos</groupId>
    <artifactId>transactions-jdbc</artifactId>
    <version>6.0.0</version>
</dependency>
```

## Entity Manager Configuration (`jta(true)`)
```java
return entityManagerFactoryBuilder
        .dataSource(dataSource)
        .properties(properties)
        .jta(true)
        .persistenceUnit(persistenceUnit)
        .packages(packages)
        .build();
```

## JTA Transaction Beans
```java
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