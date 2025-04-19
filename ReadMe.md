# Spring Boot 2 + EclipseLink + Atomikos + JTA

## ✅Dependencies
```xml
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

<dependency>
    <groupId>com.atomikos</groupId>
    <artifactId>transactions-eclipselink</artifactId>
    <version>4.0.6</version>
</dependency>

<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink</artifactId>
    <version>2.7.10</version>
</dependency>
```

## ✅Entity Manager 設定：jta(true)
```java
return entityManagerFactoryBuilder
        .dataSource(dataSource)
        .properties(properties)
        .jta(true)
        .persistenceUnit(persistenceUnit)
        .packages(packages)
        .build();
```

## ✅JPA Vendor Adapter 設定
> `props.put("eclipselink.target-server", "com.atomikos.eclipselink.platform.AtomikosPlatform");`
```java
@Bean
public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
    EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
    Map<String, Object> props = new HashMap<>();
    props.put("eclipselink.target-server", "com.atomikos.eclipselink.platform.AtomikosPlatform");
    props.put("eclipselink.weaving", "false");
    props.put("eclipselink.ddl-generation", "create-tables");
    return new EntityManagerFactoryBuilder(
            jpaVendorAdapter,
            props,
            null
    );
}
```

## ✅JTA Transaction Beans 設定
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
