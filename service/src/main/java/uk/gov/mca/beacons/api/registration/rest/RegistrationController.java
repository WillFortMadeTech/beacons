package uk.gov.mca.beacons.api.registration.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.mca.beacons.api.accountholder.domain.AccountHolderId;
import uk.gov.mca.beacons.api.beacon.domain.Beacon;
import uk.gov.mca.beacons.api.beacon.domain.BeaconId;
import uk.gov.mca.beacons.api.beacon.rest.BeaconDTO;
import uk.gov.mca.beacons.api.dto.WrapperDTO;
import uk.gov.mca.beacons.api.exceptions.InvalidBeaconDeleteException;
import uk.gov.mca.beacons.api.registration.application.RegistrationService;
import uk.gov.mca.beacons.api.registration.domain.Registration;
import uk.gov.mca.beacons.api.registration.mappers.RegistrationMapper;

@RestController
@RequestMapping("/spring-api/registrations")
@Tag(name = "Registration Controller")
public class RegistrationController {

  private final RegistrationService registrationService;
  private final RegistrationMapper registrationMapper;

  @Autowired
  public RegistrationController(
    RegistrationService createRegistrationService,
    RegistrationMapper registrationMapper
  ) {
    this.registrationService = createRegistrationService;
    this.registrationMapper = registrationMapper;
  }

  @PostMapping(value = "/register")
  public ResponseEntity<RegistrationDTO> register(
    @Valid @RequestBody CreateRegistrationDTO createRegistrationDTO
  ) {
    Registration registration = registrationMapper.fromDTO(
      createRegistrationDTO
    );
    Registration savedRegistration = registrationService.register(registration);
    return new ResponseEntity<>(
      registrationMapper.toDTO(savedRegistration),
      HttpStatus.CREATED
    );
  }

  @PatchMapping(value = "/register/{uuid}")
  public ResponseEntity<RegistrationDTO> update(
    @Valid @RequestBody CreateRegistrationDTO dto,
    @PathVariable("uuid") UUID rawBeaconId
  ) {
    BeaconId beaconId = new BeaconId(rawBeaconId);
    Registration registration = registrationMapper.fromDTO(dto);
    Registration updatedRegistration = registrationService.update(
      beaconId,
      registration
    );
    RegistrationDTO updateDTO = registrationMapper.toDTO(updatedRegistration);
    return ResponseEntity.ok(updateDTO);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<RegistrationDTO> getRegistrationByBeaconId(
    @PathVariable("uuid") UUID rawBeaconId
  ) {
    BeaconId beaconId = new BeaconId(rawBeaconId);
    Registration registration = registrationService.getByBeaconId(beaconId);
    return ResponseEntity.ok(registrationMapper.toDTO(registration));
  }

  /**
   * @implNote This is the wrong way to enforce ownership of the registration (i.e. account holder can get a
   * registration by beacon id, but only their registrations). But given the fact that no "account holder session" is
   * sent in the request headers (for example) this is the only way to enforce ownership currently (trusted client model
   * of the webapp is creating these issues).
   */
  @GetMapping(value = "/{uuid}", params = "accountHolderId")
  public ResponseEntity<RegistrationDTO> getRegistrationByBeaconId(
    @PathVariable("uuid") UUID rawBeaconId,
    @RequestParam("accountHolderId") UUID rawAccountHolderId
  ) {
    BeaconId beaconId = new BeaconId(rawBeaconId);
    AccountHolderId accountHolderId = new AccountHolderId(rawAccountHolderId);
    Registration registration = registrationService.getByBeaconIdAndAccountHolderId(
      beaconId,
      accountHolderId
    );

    return ResponseEntity.ok(registrationMapper.toDTO(registration));
  }

  /**
   * @param accountHolderId Account holder's id
   * @return List of registrations where beacon status is new
   */
  @GetMapping
  public ResponseEntity<List<RegistrationDTO>> getRegistrationsByAccountHolderId(
    @RequestParam UUID accountHolderId
  ) {
    List<Registration> registrations = registrationService.getByAccountHolderId(
      new AccountHolderId(accountHolderId)
    );

    return ResponseEntity.ok(
      registrations
        .stream()
        .map(registrationMapper::toDTO)
        .collect(Collectors.toList())
    );
  }

  @PatchMapping(value = "/{uuid}/delete")
  public ResponseEntity<Void> softDeleteRegistration(
    @PathVariable("uuid") UUID beaconId,
    @RequestBody @Valid DeleteRegistrationDTO dto
  ) {
    if (
      !beaconId.equals(dto.getBeaconId())
    ) throw new InvalidBeaconDeleteException();

    registrationService.softDelete(dto);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{uuid}")
  //  @PreAuthorize("hasAuthority('APPROLE_UPDATE_RECORDS')")
  public ResponseEntity<Void> permanentlyDeleteRegistration(
    @PathVariable("uuid") UUID id
  ) {
    registrationService.permanentDelete(new BeaconId(id));
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
