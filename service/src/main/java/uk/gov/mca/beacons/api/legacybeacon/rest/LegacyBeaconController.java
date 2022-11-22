package uk.gov.mca.beacons.api.legacybeacon.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.mca.beacons.api.dto.WrapperDTO;
import uk.gov.mca.beacons.api.exceptions.InvalidBeaconDeleteException;
import uk.gov.mca.beacons.api.exceptions.ResourceNotFoundException;
import uk.gov.mca.beacons.api.legacybeacon.application.LegacyBeaconService;
import uk.gov.mca.beacons.api.legacybeacon.domain.LegacyBeaconId;
import uk.gov.mca.beacons.api.legacybeacon.mappers.LegacyBeaconMapper;
import uk.gov.mca.beacons.api.legacybeacon.rest.dto.LegacyBeaconDTO;
import uk.gov.mca.beacons.api.registration.application.RegistrationService;
import uk.gov.mca.beacons.api.registration.rest.DeleteBeaconDTO;

@RestController
@RequestMapping("/spring-api/legacy-beacon")
@Tag(name = "Legacy Beacon Controller")
public class LegacyBeaconController {

  private final RegistrationService registrationService;
  private final LegacyBeaconService legacyBeaconService;
  private final LegacyBeaconMapper legacyBeaconMapper;

  @Autowired
  public LegacyBeaconController(
    RegistrationService registrationService,
    LegacyBeaconService legacyBeaconService,
    LegacyBeaconMapper legacyBeaconMapper
  ) {
    this.registrationService = registrationService;
    this.legacyBeaconService = legacyBeaconService;
    this.legacyBeaconMapper = legacyBeaconMapper;
  }

  @GetMapping(value = "/{uuid}")
  public WrapperDTO<LegacyBeaconDTO> findById(@PathVariable("uuid") UUID id) {
    final var legacyBeacon = legacyBeaconService
      .findById(new LegacyBeaconId(id))
      .orElseThrow(ResourceNotFoundException::new);

    return legacyBeaconMapper.toWrapperDTO(legacyBeacon);
  }

  @PatchMapping(value = "backoffice/{uuid}/delete")
  public ResponseEntity<Void> delete(
    @PathVariable("uuid") UUID beaconId,
    @RequestBody @Valid DeleteBeaconDTO dto
  ) {
    if (
      !beaconId.equals(dto.getBeaconId())
    ) throw new InvalidBeaconDeleteException();

    // yuck
    registrationService.deleteLegacyBeacon(dto);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
