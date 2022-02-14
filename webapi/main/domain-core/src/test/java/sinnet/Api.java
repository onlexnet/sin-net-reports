package sinnet;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import io.vertx.core.eventbus.EventBus;
import sinnet.bus.AskTemplate;
import sinnet.bus.commands.ChangeCustomerData;
import sinnet.bus.commands.CreateNewProject;
import sinnet.bus.commands.IncludeServicemanInProject;
import sinnet.bus.query.FindCustomers;
import sinnet.bus.query.ListServicemen;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.ProjectId;
import sinnet.models.UserToken;
import sinnet.models.Name;
import org.apache.commons.lang3.ArrayUtils;

@TestComponent
public class Api {

  private SyncBus sync;

  @Autowired
  public Api(EventBus eventBus) {
    this.sync = new SyncBus(eventBus);
  }

  public ProjectId createNewProject() {
    var address = CreateNewProject.Command.ADDRESS;
    var randomPart = UUID.randomUUID().toString();
    var projectName = "Project [" + randomPart + "]";
    var request = CreateNewProject.Command.builder().name(projectName).build();
    var reqClass = CreateNewProject.Command.class;
    var resClass = ProjectId.class;
    var result = sync.ask(address, request, reqClass, resClass);
    return result;
  }

  public EntityId defineCustomer(EntityId eid, CustomerValue value,
      ChangeCustomerData.Secret[] secrets,
      ChangeCustomerData.SecretEx[] secretsEx,
      ChangeCustomerData.Contact[] contacts) {

    var address = ChangeCustomerData.Command.ADDRESS;
    var randomPart = UUID.randomUUID().toString();
    var customerEmail = Email.of(randomPart + "@test.com");
    var request = ChangeCustomerData.Command.builder()
        .id(eid)
        .requestor(customerEmail)
        .value(value)
        .secrets(secrets)
        .secretsEx(secretsEx)
        .contacts(contacts)
        .build();
    var reqClass = ChangeCustomerData.Command.class;
    var resClass = EntityId.class;
    var result = sync.ask(address, request, reqClass, resClass);
    return result;
  }

  public EntityId defineCustomer(EntityId eid, CustomerValue value) {
    return defineCustomer(eid, value, ArrayUtils.toArray(), ArrayUtils.toArray(), ArrayUtils.toArray());
  }

  public EntityId defineCustomer(EntityId eid) {
    var validModel = CustomerValue.builder()
      .customerName(Name.of("some not-empty name"))
      .build();

    return defineCustomer(eid, validModel);
  }

  public void assignToProject(String emailOfServiceman, String customName, ProjectId projectId) {
    var address = IncludeServicemanInProject.Command.ADDRESS;
    var request = new IncludeServicemanInProject.Command(projectId, Email.of(emailOfServiceman), customName);
    sync.ask(address, request, IncludeServicemanInProject.Command.class, Void.class);
  }

  public ListServicemen.Model[] queryServicemen(UUID projectId) {
    var address = ListServicemen.Ask.ADDRESS;
    var request = new ListServicemen.Ask(projectId);
    return sync.ask(address, request, ListServicemen.Ask.class, ListServicemen.Response.class).getItems();
  }

  public FindCustomers.CustomerData[] queryCustomers(ProjectId projectId, UserToken invoker) {
    var address = FindCustomers.Ask.ADDRESS;
    var request = new FindCustomers.Ask(projectId.getId(), invoker);
    return sync.ask(address, request, FindCustomers.Ask.class, FindCustomers.Reply.class).getData();
  }
}

/** Helper class to invoke async methods in sync way to simplify tests. */
final class SyncBus {
  
  private EventBus eventBus;

  public SyncBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public <TREQ, TRES> TRES ask(String address, TREQ request, Class<TREQ> reqClass, Class<TRES> resClass) {
    var askTemplate = new AskTemplate<TREQ, TRES>(address, resClass, eventBus) {
      public final TRES request(TREQ request) {
        var asFuture = super.ask(request);
        return asFuture.toCompletionStage().toCompletableFuture().join();
      }
    };
    return askTemplate.request(request);
  }

}

