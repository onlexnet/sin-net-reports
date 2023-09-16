package sinnet.grpc.customers;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.RpcCommandHandlerBase;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
class CustomersRpcUpdateImpl extends RpcCommandHandlerBase<UpdateCommand, UpdateResult> implements CustomersRpcUpdate {

  private final CustomerRepositoryEx repository;

  @Override
  public UpdateResult apply(UpdateCommand cmd) {
    var emailOfRequestor = ValEmail.of(cmd.getUserToken().getRequestorEmail());
    var model = MapperDto.fromDto(cmd.getModel());
    var newId = repository.write(model);
    return UpdateResult.newBuilder()
        .setEntityId(Mapper.toDto(newId))
        .build();
  }

}
