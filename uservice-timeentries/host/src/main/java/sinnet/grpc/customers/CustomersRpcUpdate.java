package sinnet.grpc.customers;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.mapping.RpcCommandHandler;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
public class CustomersRpcUpdate implements
    RpcCommandHandler<UpdateCommand, UpdateResult>,
    MapperDto {

  private final CustomerRepositoryEx repository;

  @Override
  @Transactional
  public UpdateResult apply(UpdateCommand cmd) {
    var emailOfRequestor = ValEmail.of(cmd.getUserToken().getRequestorEmail());
    var model = fromDto(cmd.getModel());
    var newId = repository.write(model);
    return UpdateResult.newBuilder()
        .setEntityId(toDto(newId))
        .build();
  }

}
