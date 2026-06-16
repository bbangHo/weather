package org.pknu.weather.weather.service;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.alarm.repository.AlarmRepository;
import org.pknu.weather.weather.repository.WeatherRedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActiveWeatherLocationService {

    private final WeatherRedisRepository weatherRedisRepository;
    private final AlarmRepository alarmRepository;

    @Value("${weather.update.recent-request-window-hours:24}")
    private long recentRequestWindowHours;

    /**
     * 조회 API가 접근한 지역을 Redis ZSet에 기록해 다음 스케줄 갱신의 최우선 후보로 만든다.
     */
    public void markRequestedLocation(Long locationId) {
        weatherRedisRepository.markRequestedLocation(locationId);
    }

    /**
     * 스케줄러가 갱신할 활성 지역을 최근 요청 지역, 알림 대상 지역, 캐시 보유 지역 순서로 합친다.
     * LinkedHashSet으로 우선순위를 유지하면서 중복 locationId를 제거한다.
     */
    @Transactional(readOnly = true)
    public List<Long> getActiveLocationIds(Integer limitSize) {
        int limit = limitSize != null ? limitSize : 0;
        LinkedHashSet<Long> locationIds = new LinkedHashSet<>();

        addUntilLimit(locationIds, getRecentlyRequestedLocationIds(limit), limit);
        addUntilLimit(locationIds, alarmRepository.findAlarmTargetLocationIds(), limit);
        if (!isFull(locationIds, limit)) {
            addUntilLimit(locationIds, weatherRedisRepository.getCachedLocationIds(remainingLimit(locationIds, limit)), limit);
        }

        return locationIds.stream().toList();
    }

    /**
     * 설정된 시간 window 안에서 실제 사용자 조회가 있었던 지역만 가져온다.
     */
    private List<Long> getRecentlyRequestedLocationIds(int limit) {
        Duration window = Duration.ofHours(recentRequestWindowHours);
        return weatherRedisRepository.getRecentlyRequestedLocationIds(window, limit);
    }

    private void addUntilLimit(LinkedHashSet<Long> target, List<Long> source, int limit) {
        if (source == null || source.isEmpty() || isFull(target, limit)) {
            return;
        }

        for (Long locationId : source) {
            if (locationId != null) {
                target.add(locationId);
            }

            if (isFull(target, limit)) {
                return;
            }
        }
    }

    private int remainingLimit(LinkedHashSet<Long> locationIds, int limit) {
        if (limit <= 0) {
            return 0;
        }
        return Math.max(limit - locationIds.size(), 0);
    }

    private boolean isFull(LinkedHashSet<Long> locationIds, int limit) {
        return limit > 0 && locationIds.size() >= limit;
    }
}
