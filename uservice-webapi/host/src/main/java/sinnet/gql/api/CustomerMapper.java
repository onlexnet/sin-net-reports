package sinnet.gql.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.gql.models.CustomerContactGql;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerModelGql;
import sinnet.gql.models.CustomerSecretExGql;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretGql;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.GetReply;

/** Customer mapper facade delegating to adapter-specific mappers. */
public interface CustomerMapper {

  CustomerMapper INSTANCE = new CustomerMapper() { };

  DateTimeFormatter TIMESTAMP_FORMATTER = sinnet.infra.adapters.gql.CustomerMapper.TIMESTAMP_FORMATTER;

  default LocalDateTime map(sinnet.grpc.customers.LocalDateTime it) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.map(it);
  }

  default sinnet.domain.models.EntityId map(EntityGql item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.map(item);
  }

  default CustomerEntry toDomain(CustomerInput item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toDomain(item);
  }

  default CustomerSecret toDomain(CustomerSecretInput item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toDomain(item);
  }

  default sinnet.domain.models.CustomerSecretEx toDomain(CustomerSecretExInput item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toDomain(item);
  }

  default CustomerContact toDomain(CustomerContactInputGql item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toDomain(item);
  }

  default EntityId toGrpc(EntityGql item) {
    return sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(item);
  }

  default sinnet.grpc.customers.CustomerValue toGrpc(CustomerEntry item) {
    return sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(item);
  }

  default sinnet.grpc.customers.CustomerSecretEx toGrpc(sinnet.domain.models.CustomerSecretEx it, LocalDateTime whenChanged,
                                                        String whoChanged) {
    return sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(it, whenChanged, whoChanged);
  }

  default sinnet.grpc.customers.CustomerSecret toGrpc(CustomerSecret it, LocalDateTime whenChanged, String whoChanged) {
    return sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(it, whenChanged, whoChanged);
  }

  default sinnet.grpc.customers.LocalDateTime toGrpc(LocalDateTime from) {
    return sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(from);
  }

  default sinnet.grpc.customers.CustomerContact toGrpc(CustomerContact it) {
    return sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(it);
  }

  default SomeEntityGql toGql(EntityId id) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(id);
  }

  default CustomerContactGql toGql(sinnet.grpc.customers.CustomerContact it) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(it);
  }

  default CustomerSecretExGql toGql(sinnet.grpc.customers.CustomerSecretEx it) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(it);
  }

  default CustomerSecretGql toGql(sinnet.grpc.customers.CustomerSecret it) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(it);
  }

  default CustomerEntityGql toGql(sinnet.grpc.customers.CustomerModel item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(item);
  }

  default CustomerModelGql toGql(sinnet.grpc.customers.CustomerValue item) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(item);
  }

  default CustomerEntityGql toGql(GetReply dto) {
    return sinnet.infra.adapters.gql.CustomerMapper.INSTANCE.toGql(dto);
  }
}
