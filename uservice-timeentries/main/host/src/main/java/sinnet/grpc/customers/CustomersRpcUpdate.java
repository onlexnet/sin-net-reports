package sinnet.grpc.customers;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.grpc.customers.UpdateResult;
import sinnet.grpc.mapping.RpcCommandHandler;
import sinnet.models.Email;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomersRpcUpdate implements
    RpcCommandHandler<UpdateCommand, UpdateResult>,
    MapperDto {

  private final CustomerRepositoryEx repository;

  @Override
  public UpdateResult apply(UpdateCommand cmd) {
    var emailOfRequestor = Email.of(cmd.getUserToken().getRequestorEmail());
    var model = fromDto(cmd.getModel());
    var newId = repository.write(model);
    return UpdateResult.newBuilder()
        .setEntityId(toDto(newId))
        .build();
  }

}
