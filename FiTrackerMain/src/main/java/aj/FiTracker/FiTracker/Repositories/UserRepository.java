package aj.FiTracker.FiTracker.Repositories;

import aj.FiTracker.FiTracker.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByLogin(String login);

    @Query
    Optional<User> findOneById(long id);

    @Query(value = "SELECT * FROM app_core.app_user WHERE email ~ :regexpEmail ORDER BY email ASC LIMIT 5", nativeQuery = true)
    List<User> findUsersByEmail(@Param("regexpEmail") String email);

    @Query(value = "SELECT * FROM app_core.app_user WHERE id IN :args", nativeQuery = true)
    List<User> findUsersByIds(@Param("args") List<Long> ids);


}
