package org.pknu.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Terms;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.common.TermsType;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.TermsDto;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.MemberTermsRepository;
import org.pknu.weather.repository.TermsRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TermsRepository termsRepository;

    @Autowired
    MemberTermsRepository memberTermsRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    ObjectMapper objectMapper;


//    @Test
//    @Transactional
//    void saveMemberInfo() throws Exception {
//        // given
//        init();
//
//        // MemberJoinDTO 객체 생성
//        MemberJoinDTO memberJoinDTO = getMemberJoinDTO();
//        String memberJoinDtoJson = objectMapper.writeValueAsString(memberJoinDTO);
//
//        // TermsDto.Agreed 객체 생성
//        TermsDto.Agreed termsDtoAgreed = getTermsDto(true);
//        String termsDtoJson = objectMapper.writeValueAsString(termsDtoAgreed);
//
//        // Member 객체 및 JWT 생성
//        Member member = memberRepository.findMemberByEmail(TestDataCreator.getMember().getEmail()).get();
//        String jwt = generateJwtToken(member.getId(), member.getEmail());
//
//        em.flush();
//        em.clear();
//
//        // mockMvc를 사용하여 실제 컨트롤러 호출
//        mockMvc.perform(post("/api/v1/member/info")
//                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
//                        .content(memberJoinDtoJson)  // MemberJoinDTO 객체
//                        .content(termsDtoJson)
//                        .header("Authorization", jwt))  // Authorization 헤더
//                .andExpect(status().isOk())  // 응답 상태 코드 200 OK 확인
//                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()))  // 응답 바디 검증
//                .andExpect(jsonPath("$.data.email").value(member.getEmail()));  // 응답 바디 검증
//    }

    @Test
    @Transactional
    void saveMemberInfo() throws Exception {
        // given
        init();

        // MemberJoinDTO 객체 생성 (MockMultipartFile은 JSON에 포함되지 않음)
        MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .nickname("nickname")
                .sensitivity(Sensitivity.NONE)
                .build();

        // MockMultipartFile 생성 (profileImg로 사용)
        MockMultipartFile profileImgFile = new MockMultipartFile(
                "profileImg",  // 컨트롤러에서 바인딩될 필드 이름
                "test.jpg",
                "image/jpeg",
                "This is a test image.".getBytes()
        );

        // TermsDto.Agreed 객체 생성
        TermsDto termsDtoAgreed = getTermsDto(true);
        String termsDtoJson = objectMapper.writeValueAsString(termsDtoAgreed);

        // Member 객체 및 JWT 생성
        Member member = memberRepository.findMemberByEmail(TestDataCreator.getMember().getEmail()).get();
        String jwt = generateJwtToken(member.getId(), member.getEmail());

        em.flush();
        em.clear();

        // JSON 데이터를 MockMultipartFile로 변환
        MockMultipartFile memberJoinFile = new MockMultipartFile(
                "memberJoinDTO",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(memberJoinDTO)  // profileImg 제외
        );

        MockMultipartFile termsAgreed = new MockMultipartFile(
                "termsDto",
                "",
                "application/json",
                termsDtoJson.getBytes()
        );

        // mockMvc를 사용하여 실제 컨트롤러 호출
        mockMvc.perform(multipart("/api/v1/member/info")
                        .file(profileImgFile)  // 파일 업로드
                        .file(memberJoinFile)  // MemberJoinDTO 데이터
                        .file(termsAgreed)    // TermsDto 데이터
                        .header("Authorization", jwt))  // Authorization 헤더
                .andExpect(status().isOk())  // 응답 상태 코드 200 OK 확인
                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()))  // 응답 바디 검증
                .andExpect(jsonPath("$.data.email").value(member.getEmail()));  // 응답 바디 검증
    }

    MemberJoinDTO getMemberJoinDTO() {
        return MemberJoinDTO.builder()
                .nickname("nickname")
                .profileImg(new MockMultipartFile("file", "test.jpg", "image/jpeg", "This is a test image.".getBytes()))
                .sensitivity(Sensitivity.NONE)
                .build();
    }

    TermsDto getTermsDto(Boolean isRequired) {
        return TermsDto.builder()
                .isLocationServiceTermsAgreed(isRequired)
                .isServiceTermsAgreed(isRequired)
                .isPrivacyPolicyAgreed(isRequired)
                .isPushNotificationAgreed(isRequired)
                .build();
    }

    @Transactional
    void init() {
        termsRepository.save(Terms.builder()
                .isRequired(true)
                .termsType(TermsType.SERVICE_TERMS)
                .termsVersion(1)
                .build());

        termsRepository.save(Terms.builder()
                .isRequired(true)
                .termsType(TermsType.LOCATION_SERVICE_TERMS)
                .termsVersion(1)
                .build());

        termsRepository.save(Terms.builder()
                .isRequired(true)
                .termsType(TermsType.PRIVACY_POLICY)
                .termsVersion(1)
                .build());

        termsRepository.save(Terms.builder()
                .isRequired(false)
                .termsType(TermsType.PUSH_NOTIFICATION)
                .termsVersion(1)
                .build());

        memberRepository.save(TestDataCreator.getMember());
    }

    private String generateJwtToken(Long id, String email) {
        // JWT 토큰을 생성하는 메서드
        Map<String, Object> claims = Map.of("id", id,"email", email);
        return "Bearer " + jwtUtil.generateToken(claims ,1);
    }
}
