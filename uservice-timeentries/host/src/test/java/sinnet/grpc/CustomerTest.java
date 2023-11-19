package sinnet.grpc;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.hibernate.jpa.QueryHints;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import sinnet.db.SqlServerDbExtension;
import sinnet.features.ClientContext;
import sinnet.features.TestApi;
import sinnet.grpc.customers.CustomerRepository;
import sinnet.models.CustomerValue;
import sinnet.models.ValName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SqlServerDbExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CustomerTest {

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

    // @Test
    void persist_customers() {

      var projectId = UUID.fromString("00000000-0000-0000-0001-000000000001");
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

      String jpql1 = "SELECT c FROM CustomerDbo c "
          + "LEFT JOIN FETCH c.secrets "
          + "WHERE c.projectId = :projectId";
      String jpql = "SELECT c FROM CustomerDbo c "
          + "WHERE c.projectId = :projectId";

      var users = em.createQuery(jpql, CustomerRepository.CustomerDbo.class)
          .setParameter("projectId", projectId)
          .getResultList();
      Assertions.assertThat(users).hasSize(2);

      var items = repo.findAll();

      items.get(0).getSecrets().size();
      items.get(0).getContacts().size();
      items.get(0).getSecretsEx().size();

      Assertions.assertThat(items).isNotNull();
    }

  }

}
