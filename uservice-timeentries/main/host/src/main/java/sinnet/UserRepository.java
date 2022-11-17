package sinnet;

import java.util.UUID;

import javax.persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserRepository.UserDbo, UUID> {
  
  @Entity
  class UserDbo {

  }
}
