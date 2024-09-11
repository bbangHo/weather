package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.utils.LocalUploaderUtils;
import org.pknu.weather.common.utils.S3UploaderUtils;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.MemberResponseDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

import static org.pknu.weather.dto.converter.MemberResponseConverter.toMemberResponseDTO;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LocalUploaderUtils localUploaderUtils;
    private final S3UploaderUtils s3UploaderUtils;

    public Member saveMember(Member member){
        return memberRepository.save(member);
    }

    public Optional<Member> findMemberByEmail(String email){
        return memberRepository.findMemberByEmail(email);
    }

    @Transactional
    public MemberResponseDTO checkNicknameAndSave(String email, MemberJoinDTO memberJoinDTO){

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MultipartFile profileImg = memberJoinDTO.getProfileImg();

        if (profileImg != null && profileImg.getContentType().startsWith("image")){
            uploadProfileImageToS3(memberJoinDTO, profileImg);
            removeExProfileImage(member);
        }

        member.setMemberInfo(memberJoinDTO);

        Member savedMember = checkNicknameAndSave(member);

        return toMemberResponseDTO(savedMember);
    }

    private Member checkNicknameAndSave(Member member) {
        Member savedMember = null;
        try {
            savedMember = memberRepository.saveAndFlush(member);
        } catch (DataIntegrityViolationException e) {

            Throwable cause = e.getCause();

            if (cause instanceof ConstraintViolationException constraintViolationException) {

                SQLException sqlException = (SQLException) constraintViolationException.getCause();
                int errorCode = sqlException.getErrorCode();

                if (errorCode == 1062) {
                    // 유니크 제약 조건 위반일 때만 예외 처리
                    throw new GeneralException(ErrorStatus._DUPILICATED_NICKNAME);
                }
            } else {
                throw e;
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
