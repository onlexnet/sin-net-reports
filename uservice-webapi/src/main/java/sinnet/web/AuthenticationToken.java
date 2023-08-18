package sinnet.web;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;

/** Fixme. */
public final class AuthenticationToken extends AbstractAuthenticationToken {

  @Getter
  private String name;

  @Getter 
  private String id;

  /**
   * Fixme.
   *
   * @param accountId fixme
   * @param email fixme
   */
  AuthenticationToken(String accountId, String name) {
    super(Collections.emptyList());
    this.name = name;
    this.id = accountId;
    this.setAuthenticated(true);
  }

  private static final long serialVersionUID = 3717651672219649325L;

  @Override
  public Object getCredentials() {
    return null;
  }

  /** Contains primary email of the user. */
  @Override
  public String getPrincipal() {
    return name;
  }
}
