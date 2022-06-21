package sinnet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;

import io.smallrye.mutiny.Uni;
import lombok.val;
import sinnet.grpc.projects.UserStatsReply;
import sinnet.grpc.projects.UserStatsRequest;

interface ProjectUserStats {
  Uni<UserStatsReply> userStats(UserStatsRequest request);
}

@ApplicationScoped
class ProjectUserStatsImpl implements ProjectUserStats {

  @Inject
  Mutiny.SessionFactory factory;

  @Override
  public Uni<UserStatsReply> userStats(UserStatsRequest request) {
    var userEmail = request.getEmailOfRequestor();

    val query = "SELECT count(*) from ProjectDbo T where T.emailOfOwner=:emailOfOwner";
    return factory.withSession(
      session -> session.createQuery(query, Long.class)
        .setParameter("emailOfOwner", userEmail)
        .getSingleResult()
        .map(Long::intValue)
        .map(it -> UserStatsReply.newBuilder().setNumberOfProjects((int) it).build()));
  }

}
