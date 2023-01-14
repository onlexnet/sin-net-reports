package sinnet.dbo;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.vavr.collection.Seq;

@Repository
interface InputRepository extends CrudRepository<ProjectInputDbo, UUID> {

  /**
   * Returns all instances of the type.
   *
   * @return all entities
   */
  Seq<ProjectInputDbo> findAll();
}
