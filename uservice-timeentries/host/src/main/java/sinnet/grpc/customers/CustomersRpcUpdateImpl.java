package sinnet.grpc.customers;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.domain.model.ValEmail;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
class CustomersRpcUpdateImpl implements CustomersRpcUpdate, MapperDto {

  private final CustomerRepositoryEx repository;

  @Override
  public UpdateResult apply(UpdateCommand cmd) {
    var emailOfRequestor = ValEmail.of(cmd.getUserToken().getRequestorEmail());
    var model = fromDto(cmd.getModel());
    var newId = repository.write(model);
    return UpdateResult.newBuilder()
        .setEntityId(toDto(newId))
        .build();
  }

}
