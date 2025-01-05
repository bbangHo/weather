package org.pknu.weather.repository;

import org.pknu.weather.domain.Terms;
import org.pknu.weather.domain.common.TermsType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsRepository extends JpaRepository<Terms, Long> {

    Terms findByTermsType(TermsType termsType);
}
