package sinnet.grpc.customers;

import java.util.UUID;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;

/** As empty fields in protobuf are serialized as empty strings, we have to add additional step to avoid conversion of empty string to UUID. */
@Mapper
public interface UuidMapper {

  default UUID map(String source) {
    return UUID.fromString(source);
  }

  @Condition
  default boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }

}
