package sinnet.read;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicemanRepo extends JpaRepository<ServicemanDbo, UUID> {
}
