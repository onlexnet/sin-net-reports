package net.onlex.support;

import java.util.Base64;
import java.util.List;

import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import io.smallrye.jwt.build.Jwt;
import io.vavr.collection.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.onlex.AppApi;
import net.onlex.AppApi.ProjectEntity;

public final class Sessions {

  /** Context of the user, wh lastly did some external operation  */
  @Getter
  private UserContext activeUser = null;

  private HashMap<UserEmail, UserContext> users = HashMap.empty();

  public UserContext active;

  public void given(UserEmail userEmail) {
    var secret = "my super secret key to sign my dev JWT token";
    var keyBytes = new String(Base64.getEncoder().encode(secret.getBytes()));
    var builder = Jwt.claims();
    var token = builder
        .issuer("https://issuer.org")
        .claim("emails", List.of(userEmail.getEmail()))
        .signWithSecret(keyBytes);
    var bearer = String.format("Bearer %s", token);
    var appApi = TypesafeGraphQLClientBuilder.newBuilder()
        .header("Authorization", bearer)
        .build(AppApi.class);
    var active = new UserContext(appApi, null, 0);
    users = users.put(userEmail, active);
    activeUser = active;
  }

  public UserContext tryGet(UserEmail userEmail) {
    var active = users.get(userEmail).get();
    activeUser = active;
    return activeUser;
  }

  /**
   * Gets API context created with JWT token for given user, or create it if not exists.
   * @param userEmail
   * @return
   */
  public UserContext getOrCreate(UserEmail userEmail) {
    var active = users.get(userEmail).getOrElse(() -> {
      given(userEmail);
      return users.get(userEmail).get();
    });
    activeUser = active;
    return activeUser;
  }

  @Data
  @AllArgsConstructor
  public static class UserContext {
    public AppApi appApi;
    public ProjectEntity lastProject;
    public Integer knownNumberOfProjects;
  }

}
