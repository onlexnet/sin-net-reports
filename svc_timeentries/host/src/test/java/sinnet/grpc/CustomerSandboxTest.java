package sinnet.grpc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import sinnet.db.SqlServerDbExtension;
import sinnet.features.ClientContext;
import sinnet.features.TestApi;
import sinnet.grpc.customers.CustomerMapper;
import sinnet.grpc.customers.CustomerRepository;
import sinnet.grpc.customers.LogAssert;
import sinnet.models.CustomerModel;
import sinnet.models.ShardedId;
import sinnet.models.ValName;

// Playground for various database-related tests, and monitoring SQL entries in logs
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      // Enable SQL logging to console as the test uses logs to verify number of executed statements
      "logging.level.org.hibernate.SQL=debug",
      "logging.level.org.hibernate.type.descriptor.sql=trace"
    }
)
@ActiveProfiles("test")
@ExtendWith(SqlServerDbExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Slf4j
public class CustomerSandboxTest {

  @Nested
  class CustomersShould {

    @Autowired
    TestApi testApi;

    @Autowired
    ClientContext ctx;

    @Autowired
    CustomerRepository repo;

    @Autowired
    EntityManager em;

    UUID projectId = UUID.fromString("00000000-0000-0000-0001-000000000001");

    @BeforeEach
    @Transactional
    void init() {
      var item1 = new CustomerRepository.CustomerDbo()
          .setProjectId(projectId)
          .setEntityId(UUID.randomUUID())
          .setCustomerName("entity1");
      repo.saveAndFlush(item1);

      var item2 = new CustomerRepository.CustomerDbo()
          .setProjectId(projectId)
          .setEntityId(UUID.randomUUID())
          .setCustomerName("entity2");
      repo.saveAndFlush(item2);
      em.clear();
    }

    @Test
    @Transactional
    void persist_customers() {
      String jpql = "SELECT c FROM CustomerDbo c "
          + "LEFT JOIN FETCH c.secrets "
          + "LEFT JOIN FETCH c.secretsEx "
          + "LEFT JOIN FETCH c.contacts "
          + "WHERE c.projectId = :projectId";

      var users = em.createQuery(jpql, CustomerRepository.CustomerDbo.class)
          .setParameter("projectId", projectId)
          .getResultList();
      Assertions.assertThat(users).hasSize(2);
      users.get(0).getSecrets().size();
      users.get(0).getContacts().size();
      users.get(0).getSecretsEx().size();

      var items = repo.findByProjectId(projectId);

      items.get(0).getSecrets().size();
      items.get(0).getContacts().size();
      items.get(0).getSecretsEx().size();

      Assertions.assertThat(items).isNotNull();
    }

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Transactional
    void shouldPersistAndReadCustomer() {

      var expected = Instancio.of(CustomerModel.class)
          .supply(Select.all(LocalDateTime.class), gen -> LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
          .create();
      var customerName = UUID.randomUUID().toString();
      expected.getValue().setCustomerName(ValName.of(customerName));
      expected.setId(ShardedId.of(projectId, expected.getId().id(), -1));
      var expectedDbo = CustomerMapper.INSTANCE.toJpaDbo(expected);

      var logObserver = LogAssert.ofHibernate();
      int expectedSelects = 0;

      customerRepository.saveAndFlush(expectedDbo);
      // expectedSelects++;

      var customers = customerRepository.findByProjectId(projectId);
      expectedSelects++;
      var maybeActual = customers.stream().filter(it -> it.getCustomerName().equals(customerName)).findFirst();
      Assertions.assertThat(maybeActual).isNotEmpty();

      var actualDbo = maybeActual.get();

      var actual = CustomerMapper.INSTANCE.fromDbo2(actualDbo);
      
      // as data is unsorted, we have to sort it for comparison
      sortSubcollections(actual);
      sortSubcollections(expected);
      // set version on initial model to same as we should have returned from DB
      expected.setId(expected.getId().next());
      Assertions.assertThat(actual).isEqualTo(expected);

      LogAssert.assertThat(logObserver)
        .as("Number of observed SELECTs should bas as expected")
        .selectOperationsSizeIs(expectedSelects);
      
      Assertions.assertThat(customers.size()).isGreaterThan(0);
    }

  }

  public static void sortSubcollections(CustomerModel dbo) {
    dbo.getContacts().sort(Comparator.comparing(a -> a.getEmail(), String.CASE_INSENSITIVE_ORDER));
    dbo.getSecrets().sort(Comparator.comparing(a -> a.getUsername(), String.CASE_INSENSITIVE_ORDER));
    dbo.getSecretsEx().sort(Comparator.comparing(a -> a.getUsername(), String.CASE_INSENSITIVE_ORDER));
  }

}
