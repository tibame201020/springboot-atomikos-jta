package custom.tibame201020.demo.service;

import custom.tibame201020.demo.entity1.Entity1;
import custom.tibame201020.demo.entity2.Entity2;
import custom.tibame201020.demo.entity3.Entity3;
import custom.tibame201020.demo.repository1.Entity1Repository;
import custom.tibame201020.demo.repository2.Entity2Repository;
import custom.tibame201020.demo.repository3.Entity3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;


@Service
public class JtaTestService {

    private static final Logger logger = LoggerFactory.getLogger(JtaTestService.class);

    private final EntityManager entityManager1;
    private final EntityManager entityManager2;
    private final EntityManager entityManager3;

    private final Entity1Repository entity1Repository;
    private final Entity2Repository entity2Repository;
    private final Entity3Repository entity3Repository;

    public JtaTestService(@Qualifier("dataSource1EntityManager") EntityManager entityManager1,
                          @Qualifier("dataSource2EntityManager") EntityManager entityManager2,
                          @Qualifier("dataSource3EntityManager") EntityManager entityManager3,
                          Entity1Repository entity1Repository,
                          Entity2Repository entity2Repository,
                          Entity3Repository entity3Repository) {
        this.entityManager1 = entityManager1;
        this.entityManager2 = entityManager2;
        this.entityManager3 = entityManager3;
        this.entity1Repository = entity1Repository;
        this.entity2Repository = entity2Repository;
        this.entity3Repository = entity3Repository;
    }

    @Transactional
    public void testAll() {
        logger.info("begin JTA transaction");
        logger.info("isJoinedToTransaction EntityManager1: {}", entityManager1.isJoinedToTransaction());
        logger.info("isJoinedToTransaction EntityManager2: {}", entityManager2.isJoinedToTransaction());
        logger.info("isJoinedToTransaction EntityManager3: {}", entityManager3.isJoinedToTransaction());


        logger.info("before Entity1 count: {}", entity1Repository.count());
        logger.info("before Entity2 count: {}", entity2Repository.count());
        logger.info("before Entity3 count: {}", entity3Repository.count());

        entityManager1.persist(new Entity1());
        entityManager1.flush();
        entityManager2.persist(new Entity2());
        entityManager2.flush();
        entityManager3.persist(new Entity3());
        entityManager3.flush();

        logger.info("end JTA transaction");

        logger.info("after Entity1 count: {}", entity1Repository.count());
        logger.info("after Entity2 count: {}", entity2Repository.count());
        logger.info("after Entity3 count: {}", entity3Repository.count());

        // simulate failure to test rollback
        throw new RuntimeException("Simulated failure");
    }

    @Transactional
    public void testJtaTransaction() {
        logger.info("begin JTA transaction");
        logger.info("before Entity1 count: {}", entity1Repository.count());

        Entity1 entity1 = new Entity1();
        entity1.setName("data1");
        entityManager1.persist(entity1);
        entityManager1.flush();

        logger.info("Entity1 persisted");
        logger.info("after Entity1 count: {}", entity1Repository.count());

        // simulate failure to test rollback
        throw new RuntimeException("Simulated failure");
    }

    @Transactional
    public void testJtaTransaction2() {
        logger.info("begin JTA transaction for DataSource2");
        logger.info("before Entity2 count: {}", entity2Repository.count());

        Entity2 entity2 = new Entity2();
        entity2.setName("data2");
        entityManager2.persist(entity2);
        entityManager2.flush();

        logger.info("Entity2 persisted");
        logger.info("after Entity2 count: {}", entity2Repository.count());
    }

    @Transactional
    public void testJtaTransaction3() {
        logger.info("begin JTA transaction for DataSource3");
        logger.info("before Entity3 count: {}", entity3Repository.count());

        Entity3 entity3 = new Entity3();
        entity3.setName("data3");
        entityManager3.persist(entity3);
        entityManager3.flush();

        logger.info("Entity3 persisted");
        logger.info("after Entity3 count: {}", entity3Repository.count());
    }
}
