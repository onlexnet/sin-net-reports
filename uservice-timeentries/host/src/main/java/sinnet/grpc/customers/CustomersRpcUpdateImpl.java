package sinnet.grpc.customers;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.google.common.base.Objects;

import lombok.extern.slf4j.Slf4j;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.RpcCommandHandlerBase;
import sinnet.models.Clone;
import sinnet.models.EntityVersion;

/**
 * TBD.
 */
@Component
@Slf4j
class CustomersRpcUpdateImpl extends RpcCommandHandlerBase<UpdateCommand, UpdateResult> implements CustomersRpcUpdate {

  private final CustomerRepositoryEx repository;

  public CustomersRpcUpdateImpl(CustomerRepositoryEx repository) {
    this.repository = repository;
  }

  @Override
  public UpdateResult apply(UpdateCommand cmd) {

    var emailOfRequestor = ValEmail.of(cmd.getUserToken().getRequestorEmail());
    var model = MapperDto.fromDto(cmd.getModel());

    var now = java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    // when the first time we store Customer data, all secrets should have date same as creation time
    switch (model.getId().version()) {
      case EntityVersion.Reserved it -> {
        for (var secret : model.getSecrets()) {
          secret.setChangedWhen(now);
        }
        for (var secret : model.getSecretsEx()) {
          secret.setChangedWhen(now);
        }
      }
      case EntityVersion.Existing existingVersion -> { }
    }
    
    // when we update a Customer, we need to compare secrets and update time on only those wher ewe have new data
    switch (model.getId().version()) {
      case EntityVersion.Reserved it -> {
        // handled above
      }
      case EntityVersion.Existing existingVersion -> {
        var secretsIn = model.getSecrets();
        var secretsExtIn = model.getSecretsEx();
        log.info("Customer update, no of secrets: {}, no of secrets ext: {}", secretsIn.size(), secretsExtIn.size());
        var storedModel = repository.get(model.getId());
        if (storedModel.isEmpty()) {
          log.warn("Customer with id {} not found, cannot update secrets", model.getId());
        }
        log.info("Stored model has no of secrets: {}, no of secrets ext: {}", storedModel.get().getSecrets().size(), storedModel.get().getSecretsEx().size());
        var secrets = normalizeSecrets(model.getSecrets(), storedModel.get().getSecrets(), now, emailOfRequestor);
        var secretsExt = normalizeSecretsExt(model.getSecretsEx(), storedModel.get().getSecretsEx(), now, emailOfRequestor);
        model.setSecrets(secrets);
        model.setSecretsEx(secretsExt);
      }
    }

    var newId = repository.write(model);
    return UpdateResult.newBuilder()
        .setEntityId(Mapper.toDto(newId))
        .build();
  }

  interface EqComparer<T> { boolean equal(T t1, T t2); }

  /**
   * Produces valid list of secrets to be finally stored in DB.
   * - it updates new secrets with new time and user
   * - it updates unchanged secrets to its original time and user
   *
   * @param incoming - DTO secrets
   * @param persisted - already in database
   */
  static List<sinnet.models.CustomerSecret> normalizeSecrets(List<sinnet.models.CustomerSecret> incoming,
                                                             List<sinnet.models.CustomerSecret> persisted,
                                                             java.time.LocalDateTime time,
                                                             ValEmail requestor) {
    return normalizeItems(incoming, persisted,
      CustomersRpcUpdateImpl::equalityComparer,
      Clone.INSTANCE::of,
      it -> { 
        it.setChangedWhen(time);
        it.setChangedWho(requestor);
      },
      (o1, o2) -> {
        o1.setChangedWhen(o2.getChangedWhen());
        o1.setChangedWho(o2.getChangedWho());
      });
  }

  static List<sinnet.models.CustomerSecretEx> normalizeSecretsExt(
         List<sinnet.models.CustomerSecretEx> incoming,
         List<sinnet.models.CustomerSecretEx> persisted,
         java.time.LocalDateTime time, ValEmail requestor) {
                                  
    return normalizeItems(incoming, persisted,
      CustomersRpcUpdateImpl::equalityComparer,
      Clone.INSTANCE::of,
      it -> {
        it.setChangedWhen(time);
        it.setChangedWho(requestor);
      },
      (o1, o2) -> {
        o1.setChangedWhen(o2.getChangedWhen());
        o1.setChangedWho(o2.getChangedWho());
      });
  }

  static <T> List<T> normalizeItems(List<T> incoming, List<T> persisted, 
                                 EqComparer<T> eqComparer,
                                 Function<T, T> clone,
                                 Consumer<T> normalizeNewItem,
                                 BiConsumer<T, T> normalizeExistingItem) {
    var used = new ArrayList<T>();
    var result = new ArrayList<T>();

    for (var inc1 : incoming) {
      var cloned = clone.apply(inc1);
      var maybeExisting = persisted.stream().filter(it -> eqComparer.equal(cloned, it) && !used.contains(it)).findAny();
      if (maybeExisting.isPresent()) {
        var existing = maybeExisting.get();
        normalizeExistingItem.accept(cloned, existing);
        used.add(existing);
      } else {
        normalizeNewItem.accept(cloned);
      }
      result.add(cloned);
    }
    return result;
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
