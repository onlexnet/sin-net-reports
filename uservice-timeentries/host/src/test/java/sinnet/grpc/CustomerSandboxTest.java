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
import sinnet.db.SqlServerDbExtension;
import sinnet.features.ClientContext;
import sinnet.features.TestApi;
import sinnet.grpc.customers.CustomerJdbcRepository;
import sinnet.grpc.customers.CustomerMapper;
import sinnet.grpc.customers.CustomerRepository;
import sinnet.models.CustomerModel;
import sinnet.models.ShardedId;
import sinnet.models.ValName;

// PLayground for manual testing and observe SQL in logs
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SqlServerDbExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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

    @Autowired
    CustomerJdbcRepository customerJdbcRepository;

    @Test
    @Transactional
    void shouldPersistJpaAndReadJdbc() {

      var expected = Instancio.of(CustomerModel.class)
          .supply(Select.all(LocalDateTime.class), gen -> LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
          .create();
      var customerName = UUID.randomUUID().toString();
      expected.getValue().setCustomerName(ValName.of(customerName));
      expected.setId(ShardedId.of(projectId, expected.getId().getId(), 0));
      var expectedDbo = CustomerMapper.INSTANCE.toJpaDbo(expected);

      customerRepository.saveAndFlush(expectedDbo);

      var customers = customerJdbcRepository.findByProjectId(projectId);
      var maybeActual = customers.stream().filter(it -> it.getCustomerName().equals(customerName)).findFirst();
      Assertions.assertThat(maybeActual).isNotEmpty();

      // revert version to 0 as actual has already 1 after saving
      var actualDbo = maybeActual.get();
      actualDbo.setEntityVersion(0L);

      var actual = CustomerMapper.INSTANCE.fromDbo1(actualDbo);
      
      // as data is unsorted, we have to sort it for comparison
      sortItems(actual);
      sortItems(expected);
      Assertions.assertThat(actual).isEqualTo(expected);

      
      Assertions.assertThat(customers.size()).isGreaterThan(0);
    }

  }

  public static void sortItems(CustomerModel dbo) {
    dbo.getContacts().sort(Comparator.comparing(a -> a.getEmail(), String.CASE_INSENSITIVE_ORDER));
    dbo.getSecrets().sort(Comparator.comparing(a -> a.getUsername(), String.CASE_INSENSITIVE_ORDER));
    dbo.getSecretsEx().sort(Comparator.comparing(a -> a.getUsername(), String.CASE_INSENSITIVE_ORDER));
  }

}
