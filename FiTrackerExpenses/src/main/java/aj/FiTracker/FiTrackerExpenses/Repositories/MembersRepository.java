package aj.FiTracker.FiTrackerExpenses.Repositories;

import aj.FiTracker.FiTrackerExpenses.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdAndZoneId(long userId, String zoneId);

    @Modifying
    int deleteByUserIdAndZoneId(long userId, String zoneId);

    @Modifying
    int deleteByUserId(long userId);
}
