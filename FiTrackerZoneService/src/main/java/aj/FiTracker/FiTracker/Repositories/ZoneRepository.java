package aj.FiTracker.FiTracker.Repositories;

import aj.FiTracker.FiTracker.Documents.Zone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    Optional<Zone> findByIdAndDeletedAtIsNullAndMembers_UserId(String id, long memberId);

    List<Zone> findByMembers_UserId(long memberId);
}
