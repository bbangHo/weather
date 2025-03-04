package org.pknu.weather.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DataJpaTestConfig.class)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    void member를_조회하면_location은_즉시_로딩_테스트() {
        // given
        Member member = TestDataCreator.getBusanMember();
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member res = memberRepository.safeFindById(member.getId());

        // then
        assertThat(Hibernate.isInitialized(res.getLocation()))
                .isTrue();
    }

    @Test
    @Transactional
    void member를_조회하면_location은_즉시_로딩_테스트2() {
        // given
        Member member = TestDataCreator.getBusanMember();
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member res = memberRepository.safeFindByEmail(member.getEmail());

        // then
        assertThat(Hibernate.isInitialized(res.getLocation()))
                .isTrue();
    }

}