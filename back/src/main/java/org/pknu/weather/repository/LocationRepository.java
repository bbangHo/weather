package org.pknu.weather.repository;

import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.pknu.weather.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

    default Location safeFindById(Long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._LOCATION_NOT_FOUND));
    }
}
