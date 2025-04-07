package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import aj.FiTracker.FiTracker.Repositories.ZoneRepository;
import aj.FiTracker.FiTracker.Security.JWTClaimsUtil;
import aj.FiTracker.FiTracker.enums.MemberRole;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @Autowired
    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @Transactional
    public Zone addNewZone(NewZoneRequest newZoneRequest, Authentication authentication) {
        try {

            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            Zone zone = new Zone(newZoneRequest, claims.userId());
            zone.addMember(new Zone.Member(claims.userId(), MemberRole.ADMIN, claims.name()));
            this.zoneRepository.save(zone);
            return zone;
        } catch (DuplicateKeyException exception) {
            throw new ZoneAlreadyExistsException(exception);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public Zone getExistingZoneById(String id, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndDeletedAtIsNullAndMembers_UserId(id, claims.userId());
            if (zone.isEmpty()) {
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            return zone.get();
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional()
    public Zone removeZoneById(String id, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndDeletedAtIsNullAndMembers_UserId(id, claims.userId());
            if (zone.isEmpty()) {
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            Zone zoneDocument = zone.get();
            zoneDocument.setDeletedAt(LocalDateTime.now());
            this.zoneRepository.save(zoneDocument);
            return zoneDocument;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional
    public Zone updateZone(String id, Authentication authentication, UpdateZoneRequest updateZoneRequest) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndDeletedAtIsNullAndMembers_UserId(id, claims.userId());
            if (zone.isEmpty()) {
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            Zone zoneDocument = zone.get();
            if (!updateZoneRequest.getZoneName().isEmpty()) {
                zoneDocument.setName(updateZoneRequest.getZoneName());
            }
            if (!updateZoneRequest.getZoneDescription().isEmpty()) {
                zoneDocument.setDescription(updateZoneRequest.getZoneDescription());
            }
            if (!updateZoneRequest.getMembers().isEmpty()) {
                zoneDocument = this.updateMembers(zoneDocument, updateZoneRequest);
            }
            this.zoneRepository.save(zoneDocument);
            return zoneDocument;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Zone> getAllZones(Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            List<Zone> zones = this.zoneRepository.findByMembers_UserId(claims.userId());
            return zones;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    private Zone updateMembers(Zone existingZone, UpdateZoneRequest updateZone) {
        List<Zone.Member> members = new ArrayList<>();
        for (Zone.Member updateMember : updateZone.getMembers()) {
            Zone.Member memberToSave = null;
            for (Zone.Member existingMember : existingZone.getMembers()) {
                if (existingMember.getUserId() == updateMember.getUserId()) {
                    memberToSave = updateMember;
                    this.updateMember(existingMember, memberToSave);
                    break;
                }
            }
            if (memberToSave == null) {
                existingZone.addMember(updateMember);
            }
        }
        return existingZone;
    }

    private void updateMember(Zone.Member existingMember, Zone.Member updateMember) {
        if (updateMember.getRole() != null) {
            existingMember.setRole(updateMember.getRole());
        }
        if (updateMember.getName() != null) {
            existingMember.setName(updateMember.getName());
        }
    }
}
