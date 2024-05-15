package sinnet.gql.api;

import java.util.function.Function;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.customers.Totp;
import sinnet.otp.OtpGenerator;

@Controller
@RequiredArgsConstructor
class CustomersQueryGet implements CustomerMapper {

  private final CustomersGrpcFacade service;
  private final OtpGenerator otpGenerator;

  @SchemaMapping
  public CustomerEntityGql get(CustomersQuery self, @Argument String entityId) {
    var request = GetRequest.newBuilder()
        .setEntityId(EntityId.newBuilder()
            .setProjectId(self.userToken().getProjectId())
            .setEntityId(entityId))
        .build();

    var result = service.get(request);

    Function<Totp, String> totp = dto -> {
      if (dto == null) {
        return null;
      }
      var counter = dto.getCounter();
      var secret = dto.getSecret();
      try (var generator = otpGenerator.values(secret, counter)) {
        var maybe = generator.findFirst();
        return maybe.orElse("no totp code");
      }
    };

    return this.toGql(result, totp);
  }

}
