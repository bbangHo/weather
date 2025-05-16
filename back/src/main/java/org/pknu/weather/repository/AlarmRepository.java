package org.pknu.weather.repository;


import java.util.Optional;
import org.pknu.weather.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByFcmToken(String fcmToken);
}
