package sinnet.gql.customers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Tainted;
import javax.annotation.Untainted;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.vavr.Tuple2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import sinnet.gql.customers.CustomerRepository.CustomerDbo;
import sinnet.gql.customers.CustomerRepository.CustomerDboContact;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.ShardedId;

@RequiredArgsConstructor
public final class CustomerRepositoryEx implements MapperDbo {

  private final CustomerRepository repository;

    /**
   * Returns a model pointed by given {@code id} or empty value if the model does
   * not exists.
   *
   * @param id if of the requested model.
   */
  public Optional<CustomerModel> get(ShardedId id) {
    var probe = new CustomerDbo().setProjectId(id.getProjectId()).setId(id.getId()).setVersion(id.getVersion());
    var example = Example.of(probe);
    var maybeResult = repository.findOne(example);
    return maybeResult.map(this::fromDbo);
  }

  /**
   * Returns a latest model pointed by given {@code projectId} {@code id} or empty
   * value if the model does not exists.
   *
   * @param customerId if of the requested model.
   */
  Optional<CustomerModel> get(UUID projectId, UUID customerId) {
  }

  
  /**
   * Saves a new version of Entity identified by given eid.
   *
   * @return new EID for just stored entity.
   */
  ShardedId write(ShardedId eid,
      CustomerValue value,
      CustomerSecret[] secrets,
      CustomerSecretEx[] secretsEx,
      CustomerContact[] contacts) {
  }

  CustomerDboContact[] map(CustomerContact[] it) {
    return Stream.of(it).map(this::map).toArray(CustomerDboContact[]::new);
  }

  CustomerDboContact map(CustomerContact it) {
    return new CustomerDboContact()
        .setFirstName(it.getFirstName())
        .setLastName(it.getLastName())
        .setPhoneNo(it.getPhoneNo())
        .setEmail(it.getEmail());
  }

  public Boolean remove(ShardedId id) {
    var projectId = id.getProjectId();
    var eid = id.getId();
    var etag = id.getVersion();
    repository.deleteByProjectidEntityidEntityversion(projectId, eid, etag);
    return true;
  }

}
