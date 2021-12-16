package uk.gov.mca.beacons.api.legacybeacon.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyBeaconRepository
  extends JpaRepository<LegacyBeacon, LegacyBeaconId> {}
