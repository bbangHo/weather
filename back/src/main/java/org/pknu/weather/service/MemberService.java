package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.security.util.logout.AppleUnlinker;
import org.pknu.weather.security.util.logout.KakaoUnlinker;
import org.pknu.weather.common.utils.LocalUploaderUtils;
import org.pknu.weather.common.utils.S3UploaderUtils;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.MemberTerms;
import org.pknu.weather.domain.Terms;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.MemberResponse;
import org.pknu.weather.dto.TermsDto;
import org.pknu.weather.dto.converter.TermsConverter;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.MemberTermsRepository;
import org.pknu.weather.repository.TermsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.pknu.weather.dto.converter.MemberResponseConverter.toMemberResponseDTO;
import static org.pknu.weather.dto.converter.MemberResponseConverter.toMemberResponseWithAddressDTO;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LocalUploaderUtils localUploaderUtils;
    private final S3UploaderUtils s3UploaderUtils;
    private final KakaoUnlinker kakaoUnlinker;
    private final AppleUnlinker appleUnlinker;
    private final TermsRepository termsRepository;
    private final MemberTermsRepository memberTermsRepository;


    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    public MemberResponse.MemberResponseWithAddressDTO findFullMemberInfoByEmail(String email) {
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        return toMemberResponseWithAddressDTO(member);
    }

    @Transactional
    public MemberResponse.MemberResponseDTO checkNicknameAndSave(String email, MemberJoinDTO memberJoinDTO, TermsDto agreed) {

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MultipartFile profileImg = memberJoinDTO.getProfileImg();

        if (!profileImg.isEmpty() && profileImg.getContentType().startsWith("image")) {
            uploadProfileImageToS3(memberJoinDTO, profileImg);
            removeExProfileImage(member);
        }

        member.setMemberInfo(memberJoinDTO);

        List<Terms> termsList = termsRepository.findAll();
        List<MemberTerms> memberTermsList = TermsConverter.toMemberTermsList(member, agreed, termsList);
        memberTermsRepository.saveAll(memberTermsList);

        Member savedMember = checkNicknameAndSave(member);

        return toMemberResponseDTO(savedMember);
    }

    @Transactional
    public void deleteMember(Map<String, Object> memberInfo){

        deleteMemberFromDB(memberInfo);

        String type = String.valueOf(memberInfo.get("type"));

        if (type.equals("kakao")) {
            kakaoUnlinker.unlinkUser(String.valueOf(memberInfo.get("kakaoId")));
        } else if (type.equals("apple")) {
            appleUnlinker.unlinkUser(String.valueOf(memberInfo.get("authenticationCode")));
        }

    }

    private void deleteMemberFromDB(Map<String, Object> memberInfo) {
        Member member = memberRepository.findMemberByEmail(String.valueOf(memberInfo.get("email")))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    private Member checkNicknameAndSave(Member member) {
        Member savedMember = null;

        try {
            savedMember = memberRepository.saveAndFlush(member);

        } catch (DataIntegrityViolationException e) {

            Throwable cause = e.getCause();

            if (cause instanceof ConstraintViolationException constraintViolationException) {

                Throwable sqlCause = constraintViolationException.getCause();

                if (sqlCause instanceof SQLException sqlException && sqlException.getErrorCode() == 1062) {
                    throw new GeneralException(ErrorStatus._DUPILICATED_NICKNAME);
                }
            }
        }
        return savedMember;
    }

    private void removeExProfileImage(Member member) {
        if (member.getProfileImageName() != null && !member.getProfileImageName().isEmpty() && !member.getProfileImageName().equals("basic.png")) {
            s3UploaderUtils.removeS3File(member.getProfileImageName());
        }
    }

    private void uploadProfileImageToS3(MemberJoinDTO memberJoinDTO, MultipartFile profileImg) {
        String uploadedFileLocalPath = localUploaderUtils.uploadLocal(profileImg);

        File targetFile = new File(uploadedFileLocalPath);
        memberJoinDTO.setImgName(targetFile.getName());

        String uploadedS3Path = s3UploaderUtils.upload(uploadedFileLocalPath);
        memberJoinDTO.setImgPath(uploadedS3Path);
    }
}
