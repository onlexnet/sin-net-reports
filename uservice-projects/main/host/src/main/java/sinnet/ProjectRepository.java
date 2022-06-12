package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

@ApplicationScoped
public class ProjectRepository implements PanacheRepositoryBase<ProjectDbo, UUID> {
}
