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
        Zone zone = this.zoneService.addNewZone(newZoneRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new NewZoneResponse(zone));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<GetZoneResponse> getZone(Authentication authentication, @PathVariable @NotBlank String zoneId) {
        Zone zone = this.zoneService.getExistingZoneById(zoneId, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new GetZoneResponse(zone));
    }

    @DeleteMapping("/zone/{zoneId}")
    public ResponseEntity<DeletedZoneResponse> deleteZone(Authentication authentication, @PathVariable @NotBlank String zoneId) {
        Zone zone = this.zoneService.removeZoneById(zoneId, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new DeletedZoneResponse(zone));
    }

    @DeleteMapping("/zone/{zoneId}/member")
    public ResponseEntity<UpdateZoneResponse> deleteZoneMember(Authentication authentication, @RequestBody @Valid RemoveZoneMemberRequest removeZoneMemberRequest, @PathVariable @NotBlank String zoneId) {
        Zone zone = this.zoneService.removeZoneMember(zoneId,removeZoneMemberRequest, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateZoneResponse(zone));
    }

    @PatchMapping("/zone/{zoneId}")
    public ResponseEntity<UpdateZoneResponse> updateZone(Authentication authentication, @RequestBody @Valid UpdateZoneRequest updateZoneRequest, @PathVariable @NotBlank String zoneId) {
        Zone zone = this.zoneService.updateZone(zoneId, authentication, updateZoneRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateZoneResponse(zone));
    }

    @GetMapping("/")
    public ResponseEntity<ZonesResponse> getAllUserZones(Authentication authentication) {
        List<Zone> zone = this.zoneService.getAllZones(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(new ZonesResponse(zone));
    }
}