package org.pknu.weather.repository;

import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.Member;
import org.pknu.weather.exception.GeneralException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"location"})
    default Member safeFindById(Long id) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    Member findByNickname(String name);

    Optional<Member> findMemberByEmail(@Param("email") String email);

    Member findByEmail(String email);

    @EntityGraph(attributePaths = {"location"})
    default Member safeFindByEmail(String email) {
        Member member = findByEmail(email);

        if(member == null) {
            throw new GeneralException(ErrorStatus._MEMBER_NOT_FOUND);
        }

        return member;
    }
}

