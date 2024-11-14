package org.pknu.weather.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.utils.TagUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagQueryResult;
import org.pknu.weather.dto.converter.TagResponseConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagQueryService {
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    /**
     * 간단 날씨 보기 기능에서 태그 정보를 불러온다.
     *
     * @param memberId
     * @return
     */
    public List<TagDto.SimpleTag> getMostSelectedTags(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();

        List<TagQueryResult> tagQueryResultList = tagRepository.rankingTags(location);
        List<EnumTag> tempAndHumidList = new ArrayList<>();
        List<String> result = new ArrayList<>();

        for (int i = 0; i < tagQueryResultList.size(); i++) {
            EnumTag tag = tagQueryResultList.get(i).getTag();
            if (TagUtils.isTempTagOrHumdiTag(tag)) {
                tempAndHumidList.add(tag);
            } else {
                result.add(TagUtils.tag2Text(tag));
            }
        }

        String text = TagUtils.temperatureAndHumidityTag2Text(tempAndHumidList);
        result.add(0, text);
        result.remove(result.size() - 1);

        return result.stream()
                .map(TagResponseConverter::toSimpleTag)
                .toList();

    }
}
