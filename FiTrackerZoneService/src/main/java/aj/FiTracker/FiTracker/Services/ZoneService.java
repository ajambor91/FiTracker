package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.DTO.REST.NewZoneRequest;
import aj.FiTracker.FiTracker.DTO.REST.RemoveZoneMemberRequest;
import aj.FiTracker.FiTracker.DTO.REST.UpdateZoneRequest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.Exceptions.InternalServerException;
import aj.FiTracker.FiTracker.Exceptions.ZoneAlreadyExistsException;
import aj.FiTracker.FiTracker.Exceptions.ZoneDoesntExistException;
import aj.FiTracker.FiTracker.Factories.MembersFactory;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.Repositories.ZoneRepository;
import aj.FiTracker.FiTracker.Security.JWTClaimsUtil;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ZoneService {
    private final Logger logger = LoggerFactory.getLogger(ZoneService.class);
    private final ZoneRepository zoneRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ZoneService(ZoneRepository zoneRepository, KafkaProducerService kafkaProducerService) {
        logger.info("Initializing ZoneService.");
        this.zoneRepository = zoneRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public Zone addNewZone(NewZoneRequest newZoneRequest, Authentication authentication) {
        logger.info("Adding a new zone. User: {}", authentication.getName());
        logger.debug("New zone request details: {}", newZoneRequest);
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            Zone zone = new Zone(newZoneRequest, claims.userId());
            zone.addMember(new Zone.Member(claims.userId(), MemberRole.ADMIN, claims.name()));
            zone = this.zoneRepository.save(zone);
            logger.info("Successfully saved new zone with ID: {}", zone.getId());
            MemberTemplate memberTemplate = MembersFactory.createMemberTemplate(zone);
            this.kafkaProducerService.sendNewMembers(memberTemplate);
            logger.info("Sent new members event to Kafka for zone ID: {}", zone.getId());
            return zone;
        } catch (DuplicateKeyException exception) {
            logger.warn("Attempted to create a zone that already exists.", exception);
            throw new ZoneAlreadyExistsException(exception);
        } catch (Exception e) {
            logger.error("An internal server error occurred while adding a new zone.", e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public Zone getExistingZoneById(String id, Authentication authentication) {
        logger.info("Getting zone with ID: {}. User: {}", id, authentication.getName());
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndDeletedAtIsNullAndMembersList_UserId(id, claims.userId());
            if (zone.isEmpty()) {
                logger.warn("Zone with ID: {} not found for user: {}.", id, authentication.getName());
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            logger.info("Successfully retrieved zone with ID: {}.", zone.get().getId());
            return zone.get();
        } catch (ZoneDoesntExistException e) {
            logger.warn("Requested zone does not exist: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while retrieving zone with ID: {}.", id, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional()
    public Zone removeZoneById(String id, Authentication authentication) {
        logger.info("Removing zone with ID: {}. User: {}", id, authentication.getName());
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndDeletedAtIsNullAndMembersList_UserId(id, claims.userId());
            if (zone.isEmpty()) {
                logger.warn("Cannot delete zone with ID: {}. Zone not found for user: {}.", id, authentication.getName());
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            Zone zoneDocument = zone.get();
            zoneDocument.setDeletedAt(LocalDateTime.now());
            this.zoneRepository.save(zoneDocument);
            logger.info("Successfully marked zone with ID: {} as deleted.", zoneDocument.getId());
            return zoneDocument;

        } catch (ZoneDoesntExistException e) {
            logger.warn("Attempted to delete a non-existent zone: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while removing zone with ID: {}.", id, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional()
    public Zone removeZoneMember(String id, RemoveZoneMemberRequest removeZoneMemberRequest, Authentication authentication) {
        logger.info("Removing member(s) from zone with ID: {}. User: {}", id, authentication.getName());
        logger.debug("Remove member request details: {}", removeZoneMemberRequest);
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            Optional<Zone> zone = this.zoneRepository.findByIdAndOwnerIdAndDeletedAtIsNull(id, claims.userId());
            if (zone.isEmpty()) {
                logger.warn("Cannot remove member(s) from zone with ID: {}. Zone not found or user is not the owner.", id, authentication.getName());
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            Zone zoneDocument = zone.get();
            Zone updatedZone = this.removeMembers(zoneDocument, removeZoneMemberRequest);
            this.zoneRepository.save(updatedZone);
            logger.info("Successfully removed member(s) from zone with ID: {}.", zoneDocument.getId());
            MemberTemplate memberTemplate = MembersFactory.createMemberTemplate(removeZoneMemberRequest);
            this.kafkaProducerService.sendDeletedMembers(memberTemplate);
            logger.info("Sent deleted members event to Kafka for zone ID: {}.", zoneDocument.getId());
            return zoneDocument;

        } catch (ZoneDoesntExistException e) {
            logger.warn("Attempted to remove member(s) from a non-existent zone: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while removing member(s) from zone with ID: {}.", id, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional
    public Zone updateZone(String id, Authentication authentication, UpdateZoneRequest updateZoneRequest) {
        logger.info("Updating zone with ID: {}. User: {}", id, authentication.getName());
        logger.debug("Update zone request details: {}", updateZoneRequest);
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            MemberTemplate memberTemplateToAdd = new MemberTemplate(id);
            MemberTemplate memberTemplateToRemove = new MemberTemplate(id);
            Optional<Zone> zone = this.zoneRepository.findByIdAndDeletedAtIsNullAndMembersList_UserId(id, claims.userId());
            if (zone.isEmpty()) {
                logger.warn("Cannot update zone with ID: {}. Zone not found for user: {}.", id, authentication.getName());
                throw new ZoneDoesntExistException("Zone does not exist");
            }
            List<Zone.Member> membersToAdd = new ArrayList<>();
            List<Zone.Member> membersToRemove = new ArrayList<>();
            Zone zoneDocument = zone.get();
            if (!updateZoneRequest.getZoneName().isEmpty()) {
                logger.info("Updating zone name for ID: {} to: {}", id, updateZoneRequest.getZoneName());
                zoneDocument.setName(updateZoneRequest.getZoneName());
            }
            if (!updateZoneRequest.getZoneDescription().isEmpty()) {
                logger.info("Updating zone description for ID: {}", id);
                zoneDocument.setDescription(updateZoneRequest.getZoneDescription());
            }
            if (updateZoneRequest.getMembersList() != null && !updateZoneRequest.getMembersList().isEmpty()) {
                logger.info("Updating members list for zone ID: {}.", id);
                zoneDocument = this.updateMembers(zoneDocument, updateZoneRequest, membersToAdd, membersToRemove);
                logger.debug("Members to add: {}", membersToAdd);
            }
            this.zoneRepository.save(zoneDocument);
            logger.info("Successfully updated zone with ID: {}.", zoneDocument.getId());
            if (!membersToAdd.isEmpty()) {
                memberTemplateToAdd.addMembers(membersToAdd);

                this.kafkaProducerService.sendNewMembers(memberTemplateToAdd);
                logger.info("Sent new members event to Kafka for zone ID: {}.", zoneDocument.getId());
            }
            if (!membersToRemove.isEmpty()) {
                memberTemplateToRemove.addMembers(membersToRemove);
                this.kafkaProducerService.sendDeletedMembers(memberTemplateToRemove);
                logger.info("Sent members to remove event to Kafka for zone ID: {}.", zoneDocument.getId());

            }
            return zoneDocument;

        } catch (ZoneDoesntExistException e) {
            logger.warn("Attempted to update a non-existent zone: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An internal server error occurred while updating zone with ID: {}.", id, e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Zone> getAllZones(Authentication authentication) {
        logger.info("Getting all zones for user: {}.", authentication.getName());
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);

            List<Zone> zones = this.zoneRepository.findByDeletedAtIsNullAndMembersList_UserId(claims.userId());
            logger.info("Successfully retrieved {} zones for user: {}.", zones.size(), authentication.getName());
            return zones;
        } catch (Exception e) {
            logger.error("An internal server error occurred while retrieving all zones for user: {}.", authentication.getName(), e);
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Zone> getLastFourZones(Authentication authentication) {
        logger.info("Getting the last four zones for user: {}.", authentication.getName());
        try {
            JWTClaimsUtil.JWTClaims claims = JWTClaimsUtil.getUsernameFromClaims(authentication);
            Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdAt"));

            List<Zone> zones = this.zoneRepository.findByDeletedAtIsNullAndMembersList_UserId(claims.userId(), pageable);
            logger.info("Successfully retrieved the last {} zones for user: {}.", zones.size(), authentication.getName());
            return zones;
        } catch (Exception e) {
            logger.error("An internal server error occurred while retrieving the last four zones for user: {}.", authentication.getName(), e);
            throw new InternalServerException(e);
        }
    }

    private Zone removeMembers(Zone existingZone, RemoveZoneMemberRequest removeZoneMemberRequest) {
        logger.info("Removing members from zone ID: {}. Number of members to remove: {}", existingZone.getId(), removeZoneMemberRequest.getMembersList().size());
        for (Zone.Member memberToRemove : removeZoneMemberRequest.getMembersList()) {
            existingZone.getMembersList().removeIf(existingMember -> memberToRemove.getUserId() == existingMember.getUserId());
            logger.debug("Removed member with user ID: {} from zone ID: {}", memberToRemove.getUserId(), existingZone.getId());
        }
        return existingZone;
    }

    private Zone updateMembers(Zone existingZone, UpdateZoneRequest updateZone, List<Zone.Member> membersToAdd, List<Zone.Member> membersToRemove) {
        logger.info("Updating members for zone ID: {}. Number of members to add: {}", existingZone.getId(), updateZone.getMembersList().size());
        logger.debug("Members to add to zone ID {}: {}", existingZone.getId(), membersToAdd);
        List<Zone.Member> currentMembers = new ArrayList<>(existingZone.getMembersList());
        for (Zone.Member existingMember : currentMembers) {
            boolean found = false;
            for (Zone.Member memberToAdd : updateZone.getMembersList()) {
                if (existingMember.getUserId() == memberToAdd.getUserId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                membersToRemove.add(existingMember);
                logger.debug("Member with user ID: {} will be removed from zone ID: {}", existingMember.getUserId(), existingZone.getId());
            }
        }

        for (Zone.Member updateMember : updateZone.getMembersList()) {
            boolean found = false;
            for (Zone.Member currentMember: existingZone.getMembersList()) {
                if (currentMember.getUserId() == updateMember.getUserId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                membersToAdd.add(updateMember);
                logger.debug("Member with user ID: {} will be added to zone ID: {}", updateMember.getUserId(), existingZone.getId());

            }
        }
        existingZone.setMembersList(updateZone.getMembersList());
        return existingZone;
    }
}