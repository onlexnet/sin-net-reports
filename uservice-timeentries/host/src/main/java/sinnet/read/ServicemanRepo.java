package sinnet.read;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.vavr.collection.Seq;

/**
 * TBD.
 */
@Repository
public interface ServicemanRepo extends JpaRepository<ServicemanDbo, UUID> {

  Seq<ServicemanDbo> findByProjectId(UUID projectId);
  
}
