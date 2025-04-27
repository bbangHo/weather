package org.pknu.weather.repository;


import java.util.List;
import org.pknu.weather.dto.AlarmMemberDTO;

public interface MemberCustomRepository {
    public List<AlarmMemberDTO> findMembersAndAlarmsByAlarmType();
}
