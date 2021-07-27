package sinnet.gql.actions;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.ActionRepository;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.Name;
import sinnet.read.CustomerProjection;

/** Fixme. */
@Component
public class ActionsMutationUpdate implements GraphQLResolver<ActionsMutation> {

  @Autowired
  private ActionRepository actionService;

  @Autowired
  private CustomerProjection customerReader;

  /**
   * FixMe.
   *
   * @param gcontext ignored
   * @param content fixme.
   * @return fixme
   */
  // https://www.appsdeveloperblog.com/spring-security-preauthorize-annotation-example/
  // @PreAuthorize("hasAuthority('SCOPE_Actions.Write')")
  public CompletionStage<Boolean> update(ActionsMutation gcontext,
                                         UUID entityId,
                                         int entityVersion,
                                         ServiceEntry content) {
    var projectId = gcontext.getProjectId();
    var customerId = content.getCustomerId(); // TODO change to untainted ID
    return customerReader
            .get(projectId, customerId)
            .flatMap(it -> {
                var id = EntityId.of(projectId, entityId, entityVersion);
                var model = ActionValue.builder()
                    .who(Email.of(content.getServicemanName()))
                    .when(content.getWhenProvided())
                    .what(content.getDescription())
                    .howLong(ActionDuration.of(content.getDuration()))
                    .howFar(Distance.of(content.getDistance()));
                if (it.isDefined()) {
                    var customerModel = it.get();
                    model.whom(customerModel.getId().getId());
                }
                return actionService
                    .update(id, model.build())
                    .map(o -> Boolean.TRUE);
            })
            .toCompletionStage();
    }

}
