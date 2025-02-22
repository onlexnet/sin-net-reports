package sinnet.grpc.customers;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.google.common.base.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.RpcCommandHandlerBase;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
@Slf4j
class CustomersRpcUpdateImpl extends RpcCommandHandlerBase<UpdateCommand, UpdateResult> implements CustomersRpcUpdate {

  private final CustomerRepositoryEx repository;

  @Override
  public UpdateResult apply(UpdateCommand cmd) {
    var emailOfRequestor = ValEmail.of(cmd.getUserToken().getRequestorEmail());
    var model = MapperDto.fromDto(cmd.getModel());

    var now = java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    // when the first time we store Customer data, all secrets should have date same as creation time
    if (model.getId().getVersion() == 0) {
      for (var secret : model.getSecrets()) {
        secret.setChangedWhen(now);
      }
      for (var secret : model.getSecretsEx()) {
        secret.setChangedWhen(now);
      }
    }
    
    // when we update a Customer, we need to compare secrets and update time on only those wher ewe have new data
    if (model.getId().getVersion() > 0) {
      var secretsIn = model.getSecrets();
      var secretsExtIn = model.getSecretsEx();
      log.info("Customer update, no of secrets: {}, no of secrets ext: {}", secretsIn.size(), secretsExtIn.size());
      var storedModel = repository.get(model.getId());

      var secretsToUpdate = newIncomingSecrets(model.getSecrets(), storedModel.get().getSecrets());
      for (var secret : secretsToUpdate) {
        secret.setChangedWhen(now);
        secret.setChangedWho(emailOfRequestor);
      }

      var secretsExToUpdate = newIncomingSecretsEx(model.getSecretsEx(), storedModel.get().getSecretsEx());
      for (var secretEx : secretsExToUpdate) {
        secretEx.setChangedWhen(now);
        secretEx.setChangedWho(emailOfRequestor);
      }

      log.info("Customer update, no of new:  secrets: {}, secrets ext: {}", secretsToUpdate.size(), secretsExToUpdate.size());
    }

    var newId = repository.write(model);
    return UpdateResult.newBuilder()
        .setEntityId(Mapper.toDto(newId))
        .build();
  }

  interface EqComparer<T> { boolean equal(T t1, T t2); }

  static List<sinnet.models.CustomerSecret> newIncomingSecrets(List<sinnet.models.CustomerSecret> incoming, List<sinnet.models.CustomerSecret> persisted) {
    return newIncomingItems(incoming, persisted, CustomersRpcUpdateImpl::equalityComparer);
  }

  static List<sinnet.models.CustomerSecretEx> newIncomingSecretsEx(List<sinnet.models.CustomerSecretEx> incoming,
                                                                   List<sinnet.models.CustomerSecretEx> persisted) {
    return newIncomingItems(incoming, persisted, CustomersRpcUpdateImpl::equalityComparer);
  }

  static <T> List<T> newIncomingItems(List<T> incoming, List<T> persisted, EqComparer<T> eqComparer) {
    var incomingUnchanged = new ArrayList<T>();
    var used = new ArrayList<>();

    for (var inc : incoming) {
      var exists = persisted.stream().filter(it -> eqComparer.equal(inc, it) && !used.contains(it)).findAny();
      if (exists.isPresent()) {
        incomingUnchanged.add(inc);
        used.add(exists);
      }
    }

    return incoming.stream().filter(it -> !incomingUnchanged.contains(it)).toList();
  }

  static <T, U> boolean fieldComparer(T o1, T o2, Function<T, U> getter) {
    var fieldValue1 = getter.apply(o1);
    var fieldValue2 = getter.apply(o2);
    return Objects.equal(fieldValue1, fieldValue2);
  }

  static boolean equalityComparer(sinnet.models.CustomerSecret s1, sinnet.models.CustomerSecret s2) {
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecret::getLocation)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecret::getOtpRecoveryKeys)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecret::getOtpSecret)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecret::getPassword)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecret::getUsername)) { 
      return false; 
    }
    return true;
  }

  static boolean equalityComparer(sinnet.models.CustomerSecretEx s1, sinnet.models.CustomerSecretEx s2) {
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getLocation)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getOtpRecoveryKeys)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getOtpSecret)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getPassword)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getUsername)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getEntityCode)) { 
      return false; 
    }
    if (!fieldComparer(s1, s2, sinnet.models.CustomerSecretEx::getEntityName)) { 
      return false; 
    }
    return true;
  }
}
