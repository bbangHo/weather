package org.pknu.weather.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Slf4j
class PostCustomRepositoryImplTest {

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;
    @Test
    @Transactional
    void test1() {
        Location location = locationRepository.findLocationByFullAddress("전북특별자치도", "부안군", "상서면").get();

        long current = System.currentTimeMillis();
        List<Post> test = postRepository.test(1L, 10L, location, PostType.WEATHER);
        long end = System.currentTimeMillis();
        long time = end - current;

        log.info(time + "ms");
    }

    @Test
    @Transactional
    void test2() {
        Location location1 = locationRepository.findLocationByFullAddress("전북특별자치도", "부안군", "상서면").get();

        long current1 = System.currentTimeMillis();
        List<Post> test1 = postRepository.findAllWithinDistance(1L, 10L, location1, PostType.WEATHER);
        long end1 = System.currentTimeMillis();
        long time1 = end1 - current1;

        log.info(time1 + "ms");
    }
}