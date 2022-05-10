package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

@ApplicationScoped
public class ProjectsRepository implements PanacheRepositoryBase<ProjectsDbo, UUID> {
}
