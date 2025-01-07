package org.pknu.weather.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.TestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DataJpaTest
@Import(TestConfig.class)
class PostCustomRepositoryTest {
    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("최신순으로 게시글을 가져오는 메서드 테스트")
    void getLatestPostListTest() {
        // given
        Location seoulLocation = locationRepository.save(TestDataCreator.getSeoulLocation());

        Member member = memberRepository.save(TestDataCreator.getMember());
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));

        em.flush();
        em.clear();

        // when
        List<Post> latestPostList = postRepository.getLatestPostList(seoulLocation);

        // then
        Assertions.assertThat(latestPostList.get(0).getCreatedAt()).isAfter(latestPostList.get(1).getCreatedAt());
        Assertions.assertThat(latestPostList.get(1).getCreatedAt()).isAfter(latestPostList.get(2).getCreatedAt());
        Assertions.assertThat(latestPostList.get(2).getCreatedAt()).isAfter(latestPostList.get(3).getCreatedAt());
        Assertions.assertThat(latestPostList.get(3).getCreatedAt()).isAfter(latestPostList.get(4).getCreatedAt());
        Assertions.assertThat(latestPostList.size()).isEqualTo(5);

    }
}

 