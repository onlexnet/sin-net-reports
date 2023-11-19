package sinnet.dbo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Repository
@Transactional(TxType.REQUIRED)
interface ProjectRepository extends JpaRepository<ProjectDbo, UUID> {

  @Query("""
      select m from ProjectDbo m
      left join fetch m.operators
      where m.emailOfOwner = :emailOfOwner
      """)
  List<ProjectDbo> findByEmailOfOwner(String emailOfOwner);

  long countByEmailOfOwner(String emailOfOwner);
}
