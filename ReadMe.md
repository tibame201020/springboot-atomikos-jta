
# Spring Boot 3 + EclipseLink + Atomikos JTA 整合指南

## ✅ dependencies

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

---

## ✅ EntityManager 使用 `.jta(true)`

```java
return entityManagerFactoryBuilder
        .dataSource(dataSource)
        .properties(properties)
        .jta(true)
        .persistenceUnit(persistenceUnit)
        .packages(packages)
        .build();
```

---

## ✅ JPA Vendor Adapter 設定（使用 CustomAtomikosPlatform）

```java
@Bean
public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
    EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
    Map<String, Object> props = new HashMap<>();
    props.put("eclipselink.target-server", CustomAtomikosPlatform.class.getName());
    props.put("eclipselink.weaving", "false");
    props.put("eclipselink.ddl-generation", "create-tables");

    return new EntityManagerFactoryBuilder(
            jpaVendorAdapter,
            props,
            null
    );
}
```

---

## ✅ JTA Transaction Beans 設定

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

## ℹ️ 為什麼需要 `CustomAtomikosPlatform`？

Spring Boot 3 使用 `jakarta.transaction`，但：

```text
AtomikosPlatform from com.atomikos:transactions-eclipselink:6.0.0
依賴 javax.transaction.TransactionManager，
導致在 Spring Boot 3 (Jakarta EE 10) 中無法使用。
```

---

## ❌ 官方 `AtomikosPlatform`（無法用於 Spring Boot 3）

```java
package com.atomikos.eclipselink.platform;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;
import com.atomikos.util.Atomikos;

public class AtomikosPlatform extends ServerPlatformBase {
    public AtomikosPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        disableRuntimeServices();
    }

    @Override
    public Class<?> getExternalTransactionControllerClass() {
        return AtomikosTransactionController.class;
    }

    @Override
    protected void initializeServerNameAndVersion() {
        this.serverNameAndVersion = "Atomikos: " + Atomikos.VERSION;
    }
}
```

```java
package com.atomikos.eclipselink.platform;

import javax.transaction.TransactionManager; // ⛔ javax 不相容 Spring Boot 3

import org.eclipse.persistence.transaction.JTATransactionController;
import com.atomikos.icatch.jta.UserTransactionManager;

public class AtomikosTransactionController extends JTATransactionController {
    private UserTransactionManager utm;

    public AtomikosTransactionController() {
        utm = new UserTransactionManager();
    }

    protected TransactionManager acquireTransactionManager() throws Exception {
        return utm;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return utm;
    }
}
```

---

## ✅ CustomAtomikosPlatform（支援 Spring Boot 3）

```java
package your.package;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

public class CustomAtomikosPlatform extends ServerPlatformBase {
    public CustomAtomikosPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        disableRuntimeServices();
    }

    @Override
    public Class<?> getExternalTransactionControllerClass() {
        return CustomAtomikosTransactionController.class;
    }

    @Override
    protected void initializeServerNameAndVersion() {
        this.serverNameAndVersion = "CustomAtomikos";
    }
}
```

---

## ✅ CustomAtomikosTransactionController（使用 `jakarta.transaction`）

```java
package your.package;

import jakarta.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;
import com.atomikos.icatch.jta.UserTransactionManager;

public class CustomAtomikosTransactionController extends JTATransactionController {

    private UserTransactionManager utm;

    public CustomAtomikosTransactionController() {
        utm = new UserTransactionManager();
    }

    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        return utm;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return utm;
    }
}
```
