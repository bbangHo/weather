package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.ApiResponse;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.utils.LocalUploaderUtils;
import org.pknu.weather.common.utils.S3UploaderUtils;
import org.pknu.weather.domain.Member;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.MemberResponseDTO;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    public MemberResponseDTO saveMemberInfo(String email, MemberJoinDTO memberJoinDTO){

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MultipartFile profileImg = memberJoinDTO.getProfileImg();

        if (profileImg != null && profileImg.getContentType().startsWith("image")){
            uploadProfileImageToS3(memberJoinDTO, profileImg);
            removeExProfileImage(member);
        }

        member.setMemberInfo(memberJoinDTO);
        Member savedMember = memberRepository.save(member);

        return toMemberResponseDTO(savedMember);
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
