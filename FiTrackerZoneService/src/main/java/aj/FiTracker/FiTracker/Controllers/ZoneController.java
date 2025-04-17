package aj.FiTracker.FiTracker.Controllers;

import aj.FiTracker.FiTracker.DTO.REST.*;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Services.ZoneService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/zones")
public class ZoneController {
    private final Logger logger = LoggerFactory.getLogger(ZoneController.class);
    private final ZoneService zoneService;

    @Autowired
    public ZoneController(ZoneService zoneService) {
        logger.info("Initializing ZoneController");
        this.zoneService = zoneService;
    }

    @PostMapping("/zone")
    public ResponseEntity<NewZoneResponse> createZone(Authentication authentication, @RequestBody @Valid NewZoneRequest newZoneRequest) {
        logger.info("Received request to create a new zone. User: {}", authentication.getName());
        logger.debug("Create zone request details: {}", newZoneRequest);
        Zone zone = this.zoneService.addNewZone(newZoneRequest, authentication);
        logger.info("Successfully created new zone with ID: {}", zone.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new NewZoneResponse(zone));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<GetZoneResponse> getZone(Authentication authentication, @PathVariable @NotBlank String zoneId) {
        logger.info("Received request to get zone with ID: {}. User: {}", zoneId, authentication.getName());
        Zone zone = this.zoneService.getExistingZoneById(zoneId, authentication);
        logger.info("Successfully retrieved zone with ID: {}", zone.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new GetZoneResponse(zone));
    }

    @DeleteMapping("/zone/{zoneId}")
    public ResponseEntity<DeletedZoneResponse> deleteZone(Authentication authentication, @PathVariable @NotBlank String zoneId) {
        logger.info("Received request to delete zone with ID: {}. User: {}", zoneId, authentication.getName());
        Zone zone = this.zoneService.removeZoneById(zoneId, authentication);
        logger.info("Successfully deleted zone with ID: {}", zone.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new DeletedZoneResponse(zone));
    }

    @PatchMapping("/zone/{zoneId}/member")
    public ResponseEntity<UpdateZoneResponse> deleteZoneMember(Authentication authentication, @RequestBody @Valid RemoveZoneMemberRequest removeZoneMemberRequest, @PathVariable @NotBlank String zoneId) {
        logger.info("Received request to remove a member from zone with ID: {}. User: {}", zoneId, authentication.getName());
        logger.debug("Remove member request details: {}", removeZoneMemberRequest);
        Zone zone = this.zoneService.removeZoneMember(zoneId, removeZoneMemberRequest, authentication);
        logger.info("Successfully removed member from zone with ID: {}", zone.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateZoneResponse(zone));
    }

    @PatchMapping("/zone/{zoneId}")
    public ResponseEntity<UpdateZoneResponse> updateZone(Authentication authentication, @RequestBody @Valid UpdateZoneRequest updateZoneRequest, @PathVariable @NotBlank String zoneId) {
        logger.info("Received request to update zone with ID: {}. User: {}", zoneId, authentication.getName());
        logger.debug("Update zone request details: {}", updateZoneRequest);
        Zone zone = this.zoneService.updateZone(zoneId, authentication, updateZoneRequest);
        logger.info("Successfully updated zone with ID: {}", zone.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateZoneResponse(zone));
    }

    @GetMapping("/")
    public ResponseEntity<ZonesResponse> getUserZones(Authentication authentication, @RequestParam(name = "all", required = false) Boolean all) {
        logger.info("Received request to get user's zones. User: {}", authentication.getName());
        List<Zone> zone = new ArrayList<>();
        if (all == null || !all) {
            logger.info("Fetching the last four zones for user: {}", authentication.getName());
            zone = this.zoneService.getLastFourZones(authentication);
            logger.info("Successfully retrieved the last four zones.");
        } else {
            logger.info("Fetching all zones for user: {}", authentication.getName());
            zone = this.zoneService.getAllZones(authentication);
            logger.info("Successfully retrieved all zones.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ZonesResponse(zone));
    }
}