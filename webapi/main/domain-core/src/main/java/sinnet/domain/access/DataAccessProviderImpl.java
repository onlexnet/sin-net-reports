package sinnet.domain.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.domain.access.DataAccess.AccessOptions;
import sinnet.models.UserToken;
import sinnet.read.ProjectProjector;

@Component
@RequiredArgsConstructor
public class DataAccessProviderImpl implements DataAccess.Provider {

    private final ProjectProjector.Provider projectAssignmentRepository;

    /** {@inheritDoc} */
    @Override
    public <T> AccessOptions<T> with(UserToken invoker) {
        // var role = invoker.getRole();

        // if (Role.DATA_OPS.equals(role)) {
        //     return DataAccess.EffectiveAccess.<T>builder().build();
        // }

        var userEmail = invoker.getEmail();
        var assignments = projectAssignmentRepository
            .findByServiceman(userEmail)
            .map(items -> items.map(item -> item.getId()));
        return null;

        // if (Role.ADVISOR.equals(role) || Role.UNDERWRITER.equals(role)) {
        //     return DataAccess.EffectiveAccess.<T>builder().limitedToProjects(projectIds).build();
        // }

        // var maybeUser = userRepository.findByEmail(userEmail);
        // var user = maybeUser.orElseThrow(() -> new AccessDeniedError());
        // var brokerId = user.getBroker().getEntityId();
        // return DataAccess.EffectiveAccess.<T>builder().limitedToBroker(brokerId)
        //     .limitedToProjects(projectIds).build();
    }
}
