package sinnet.grpc.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.PropsBuilder;
import sinnet.read.UserModel;

/**
 * Doman <-> Dto translations
 */
public interface MapperDto extends Mapper {

  default List<sinnet.grpc.users.UsersSearchModel> toDto(List<UserModel> model) {
    return model.stream().map(this::toDto).toList();
  }

  default sinnet.grpc.users.UsersSearchModel toDto(UserModel model) {
    return PropsBuilder.build(sinnet.grpc.users.UsersSearchModel.newBuilder())
        .tset(Optional.ofNullable(model.getEmail().getValue()), b -> b::setEmail)
        .set(UUID.randomUUID().toString(), b -> b::setEntityId)
        .done().build();
  }
}
