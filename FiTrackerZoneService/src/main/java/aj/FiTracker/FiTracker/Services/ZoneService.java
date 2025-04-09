package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.DTO.REST.RemoveZoneMemberRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import aj.FiTracker.FiTracker.Factories.MembersFactory;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.Repositories.ZoneRepository;
import aj.FiTracker.FiTracker.Security.JWTClaimsUtil;
import aj.FiTracker.FiTracker.Enums.MemberRole;
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
    private final KafkaProducerService kafkaProducerService;
    @Autowired
    public ZoneService(ZoneRepository zoneRepository, KafkaProducerService kafkaProducerService) {
        this.zoneRepository = zoneRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public Zone addNewZone(NewZoneRequest newZoneRequest, Authentication authentication) {
        try {

            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            Zone zone = new Zone(newZoneRequest, claims.userId());
            zone.addMember(new Zone.Member(claims.userId(), MemberRole.ADMIN, claims.name()));
            zone = this.zoneRepository.save(zone);
            MemberTemplate memberTemplate = MembersFactory.createMemberTemplate(zone);
            this.kafkaProducerService.sendNewMembers(memberTemplate);
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
        } catch (ZoneDoesntExistException e) {
            throw e;
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

        } catch (ZoneDoesntExistException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional()
    public Zone removeZoneMember(String id, RemoveZoneMemberRequest removeZoneMemberRequest, Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndOwnerIdAndDeletedAtIsNull(id, claims.userId());
            if (zone.isEmpty()) {
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            Zone zoneDocument = zone.get();
            Zone updatedZone = this.removeMembers(zoneDocument, removeZoneMemberRequest);
            this.zoneRepository.save(updatedZone);
            MemberTemplate memberTemplate = MembersFactory.createMemberTemplate(removeZoneMemberRequest);
            this.kafkaProducerService.sendDeletedMembers(memberTemplate);
            return zoneDocument;

        } catch (ZoneDoesntExistException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }
    @Transactional
    public Zone updateZone(String id, Authentication authentication, UpdateZoneRequest updateZoneRequest) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            MemberTemplate memberTemplate = new MemberTemplate(id);
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
            if (updateZoneRequest.getMembers() != null && !updateZoneRequest.getMembers().isEmpty()) {
                zoneDocument = this.updateMembers(zoneDocument, updateZoneRequest, memberTemplate);
            }
            this.zoneRepository.save(zoneDocument);
            if (!memberTemplate.getMemberList().isEmpty()) {
                this.kafkaProducerService.sendNewMembers(memberTemplate);
            }
            return zoneDocument;

        } catch (ZoneDoesntExistException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Zone> getAllZones(Authentication authentication) {
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            List<Zone> zones = this.zoneRepository.findByDeletedAtIsNullAndMembers_UserId(claims.userId());
            return zones;
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    private Zone removeMembers(Zone existingZone, RemoveZoneMemberRequest removeZoneMemberRequest) {
        for (Zone.Member memberToRemove : removeZoneMemberRequest.getMembers()) {
            existingZone.getMembers().removeIf(existingMember -> memberToRemove.getUserId() == existingMember.getUserId());
        }
        return existingZone;
    }

    private Zone updateMembers(Zone existingZone, UpdateZoneRequest updateZone, MemberTemplate memberTemplate) {
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
                memberTemplate.addMember(updateMember);
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
