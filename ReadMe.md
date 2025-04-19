#### dependencies
```
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
```
<dependency>
  <groupId>org.eclipse.persistence</groupId>
  <artifactId>eclipselink</artifactId>
  <version>4.0.3</version>
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
#### jpa vendor adapter: 
###### props.put("eclipselink.target-server", CustomAtomikosPlatform.class.getName());
```
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

## description why use CustomAtomikosPlatform

#### AtomikosPlatform from com.atomikos:transactions-eclipselink:6.0.0
```
AtomikosPlatform uses AtomikosTransactionController
AtomikosTransactionController use javax.transaction.TransactionManager
```
```
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
		this.serverNameAndVersion="Atomikos: "+Atomikos.VERSION;
	}
}

```

```
package com.atomikos.eclipselink.platform;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;

import com.atomikos.icatch.jta.UserTransactionManager;

public class AtomikosTransactionController extends JTATransactionController {

	private UserTransactionManager utm;

	public AtomikosTransactionController() {
		utm = new UserTransactionManager();
	}
	/**
	 * INTERNAL: Obtain and return the JTA TransactionManager on this platform
	 */
	protected TransactionManager acquireTransactionManager() throws Exception {
		return utm;
	}

	@Override
	public TransactionManager getTransactionManager() {

		return utm;
	}

}
```