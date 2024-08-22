package org.pknu.weather.repository;

import jakarta.transaction.Transactional;
import org.pknu.weather.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(@Param("email") String email);

}

