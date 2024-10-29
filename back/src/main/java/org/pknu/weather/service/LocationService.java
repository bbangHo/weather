package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.utils.AddressFinder;
import org.pknu.weather.common.utils.SgisLocationUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.LocationDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.pknu.weather.dto.converter.LocationConverter.toLocation;
import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final SgisLocationUtils sgisLocationUtils;
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final AddressFinder addressFinder;

    public LocationDTO saveLocation(LocationDTO locationDTO, String email, double x, double y) {

        log.debug("LocationService - save Location with coordinate service start......");

        sgisLocationUtils.getAddressInfo(locationDTO, x, y);

        Optional<Location> locationByFullAddress = locationRepository.findLocationByFullAddress(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());

        Location savedLocation = locationByFullAddress.orElseGet(() -> locationRepository.save(toLocation(locationDTO)));

        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        member.changeLocation(savedLocation);
        memberRepository.save(member);

        log.debug("LocationService - saveLocation method end......");

        return toLocationDTO(savedLocation);
    }

    public LocationDTO saveLocation(LocationDTO locationDTO) {

        Optional<Location> locationByFullAddress = locationRepository.findLocationByFullAddress(locationDTO.getProvince(), locationDTO.getCity(), locationDTO.getStreet());
        Location savedLocation = null;

        if(locationByFullAddress.isEmpty()){
            sgisLocationUtils.getAddressInfo(locationDTO);
            savedLocation = locationByFullAddress.orElseGet(() -> locationRepository.save(toLocation(locationDTO)));
        }

        if(locationByFullAddress.isPresent())
            savedLocation = locationByFullAddress.get();

        return toLocationDTO(savedLocation);
    }

    public LocationDTO findDefaultLocation(String email) {

        log.debug("LocationService - findDefaultLocation  start......");

        Member member = memberRepository.findMemberByEmail(email).orElseThrow();

        Location location = member.getLocation();

        if (location == null) {
            throw new GeneralException(ErrorStatus._MEMBER_NOT_FOUND_LOCATION);
        }

        log.debug("LocationService - findDefaultLocation  method end......");

        return toLocationDTO(location);

    }

    public List<String> getLocation(String province, String city) {

        if (province == null || province.isEmpty())
            return addressFinder.getLocation();
        else if (city == null || city.isEmpty())
            return addressFinder.getLocation(province);
        else
            return addressFinder.getLocation(province, city);

    }
}
