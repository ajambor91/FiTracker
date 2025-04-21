package aj.FiTracker.FiTrackerExpenses.Repositories;

import aj.FiTracker.FiTrackerExpenses.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdAndZoneId(long userId, String zoneId);

    @Modifying
    @Query(value = "DELETE FROM app_data.app_user WHERE app_data.app_user.app_user_id = :userId AND app_data.app_user.app_zone_id = :zoneId",
            nativeQuery = true)
    int removeMember(@Param("userId") long userId, @Param("zoneId") String zoneId);
}
