package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeatherQueryService {
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;

    public void getNearbyLocationWithTagsAndWeathers(Long memberId){
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        List<Location> nearbyLocationList = locationRepository.getNearbyLocationWithTagsAndWeathers(location);

    }

}
