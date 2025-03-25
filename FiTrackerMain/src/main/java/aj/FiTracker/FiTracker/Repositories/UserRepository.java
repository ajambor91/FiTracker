package aj.FiTracker.FiTracker.Repositories;
import java.util.Optional;

import aj.FiTracker.FiTracker.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByLogin(String login);
}
