package aj.FiTracker.FiTracker.Repositories;

import aj.FiTracker.FiTracker.Documents.Zone;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    Optional<Zone> findByIdAndDeletedAtIsNullAndMembersList_UserId(String id, long memberId);

    Optional<Zone> findByIdAndOwnerIdAndDeletedAtIsNull(String zoneId, long ownerId);

    List<Zone> findByDeletedAtIsNullAndMembersList_UserId(long memberId);

    List<Zone> findByDeletedAtIsNullAndMembersList_UserId(long memberId, Pageable pageable);

    @Query("{ 'deletedAt' : null, 'membersList.userId' : ?0 }")
    @Update("{ '$pull' : { 'membersList' : { 'userId' : ?0 } } }")
    void pullMemberFromAllZones(long userId);

    @Query(value = "{ 'deletedAt' : null, 'membersList' : { $size: 1 } }", delete = true)
    void deleteZonesWithOneMember();
}
