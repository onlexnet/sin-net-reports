package sinnet.dbo;

import javax.enterprise.context.ApplicationScoped;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
@ApplicationScoped
public final class DboFacade {

  @Delegate
  private final DboRemove dboRemove;

  @Delegate
  private final DboOwned dboOwned;

  @Delegate
  private final DboUpdate dboUpdate;

  @Delegate
  private final DboCreate dboCreate;
}
