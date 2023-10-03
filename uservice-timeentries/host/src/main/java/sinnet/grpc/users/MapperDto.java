package sinnet.grpc.users;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import io.vavr.collection.List;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.PropsBuilder;
import sinnet.read.UserModel;

/**
 * Domain <-> Dto translations.
 */
public interface MapperDto extends Mapper {

  /**
   * TBD.
   */
  default java.util.List<sinnet.grpc.users.UsersSearchModel> toDto(List<UserModel> model) {
    return model.map(this::toDto).toJavaList();
  }

  /**
   * TBD.
   */
  default sinnet.grpc.users.UsersSearchModel toDto(UserModel model) {
    var customName = model.getCustomName().getValue();
    var email = model.getEmail().value();
    if (StringUtils.isBlank(customName)) {
      if (email.contains("@")) {
        customName = email.split("@")[0];
      } else {
        customName = email;
      }
    }

    return PropsBuilder.build(sinnet.grpc.users.UsersSearchModel.newBuilder())
        .tset(Optional.ofNullable(email), b -> b::setEmail)
        .tset(Optional.ofNullable(customName), b -> b::setCustomName)
        .set(UUID.randomUUID().toString(), b -> b::setEntityId)
        .done().build();
  }
}
