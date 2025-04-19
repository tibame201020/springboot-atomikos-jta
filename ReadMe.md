
# Spring Boot + Atomikos JTA 整合指南
---

## 🔧 共通設定項目

### JTA Transaction Beans（通用）

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

---

## ✅ Hibernate 組合設定

### Spring Boot 2 + Hibernate + Atomikos

- 主要依賴：`transactions-jta`, `transactions-jdbc`
- Hibernate 不需額外指定 `target-server`

#### 依賴

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
```

### Spring Boot 3 + Hibernate + Atomikos

- 改為使用 `transactions-spring-boot3`
- 注意 `jakarta.transaction` API 的變更

#### 依賴

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

---

## ✅ EclipseLink 組合設定

### EntityManagerFactory 設定（共同）

```java
return entityManagerFactoryBuilder
        .dataSource(dataSource)
        .properties(properties)
        .jta(true)
        .persistenceUnit(persistenceUnit)
        .packages(packages)
        .build();
```

### Spring Boot 2 + EclipseLink + Atomikos

- 必須指定 `eclipselink.target-server`

#### jpaVendorAdapter

```java
EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
Map<String, Object> props = new HashMap<>();
props.put("eclipselink.target-server", "com.atomikos.eclipselink.platform.AtomikosPlatform");
props.put("eclipselink.weaving", "false");
props.put("eclipselink.ddl-generation", "create-tables");
```

#### 依賴

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

### Spring Boot 3 + EclipseLink + Atomikos

- 原 `com.atomikos.eclipselink.platform.AtomikosPlatform` 使用 `javax.transaction`，與 Jakarta API 不相容
- 需自行實作 `CustomAtomikosPlatform` 與 `AtomikosTransactionController`（支援 `jakarta.transaction`）

#### jpaVendorAdapter（使用自訂 platform）

```java
props.put("eclipselink.target-server", CustomAtomikosPlatform.class.getName());
```

#### 依賴

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
<dependency>
  <groupId>org.eclipse.persistence</groupId>
  <artifactId>eclipselink</artifactId>
  <version>4.0.3</version>
</dependency>
```

#### 自訂平台必要性

- 原生 `AtomikosPlatform` 使用 `javax.transaction.TransactionManager`，無法與 Spring Boot 3 相容
- 自訂版本需改為 `jakarta.transaction.TransactionManager` 並實作 `getTransactionManager()`

---

## 🚨 注意事項

| 項目 | 說明 |
|------|------|
| JTA 設定 | `.jta(true)` 必須設於 `EntityManagerFactoryBuilder` |
| AtomikosPlatform | Spring Boot 3 + EclipseLink 請改用自定義版本 |
| Jakarta 轉移 | Spring Boot 3 開始皆需使用 `jakarta.transaction` |
| transactions-eclipselink | 僅支援 Spring Boot 2，Spring Boot 3 需自行實作 |
