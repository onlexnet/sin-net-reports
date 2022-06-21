package net.onlex;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import io.smallrye.jwt.build.Jwt;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppApiUtils {
  
  static AppApi create() {
    var secret = "my super secret key to sign my dev JWT token";
    var keyBytes = new String(Base64.getEncoder().encode(secret.getBytes()));
    var builder = Jwt.claims();
    var randomUsername = UUID.randomUUID().toString();
    var token = builder
        .issuer("https://issuer.org")
        .claim("emails", List.of(randomUsername + "@email.com"))
        .signWithSecret(keyBytes);
    var bearer2 = String.format("Bearer %s", token);
    return TypesafeGraphQLClientBuilder.newBuilder()
          .header("Authorization", bearer2)
          .build(AppApi.class);

  }
}
