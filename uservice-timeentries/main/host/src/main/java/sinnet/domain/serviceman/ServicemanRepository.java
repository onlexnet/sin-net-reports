package sinnet.domain.serviceman;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicemanRepository extends JpaRepository<ServicemanDbo, UUID> {
}
