package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.dto.MemberJoinDTO;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NONE'")
    private Sensitivity sensitivity;

    @Column(unique = true)
    private String nickname;

    @ColumnDefault("'https://weather-pknu-bucket.s3.ap-northeast-2.amazonaws.com/basic.png'")
    private String profileImage;

    @ColumnDefault("'basic.png'")
    private String profileImageName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Recommendation> recommendationList = new ArrayList<>();

    public void changeLocation(Location location){
        this.location = location;
    }
    public void setMemberInfo(MemberJoinDTO memberJoinDTO){
        if (memberJoinDTO.getNickname() != null && !memberJoinDTO.getNickname().isEmpty())
            this.nickname = memberJoinDTO.getNickname();

        if (memberJoinDTO.getSensitivity() != null)
            this.sensitivity = memberJoinDTO.getSensitivity();

        if (memberJoinDTO.getProfileImg() != null && !memberJoinDTO.getProfileImg().isEmpty()) {
            this.profileImage = memberJoinDTO.getImgPath();
            this.profileImageName = memberJoinDTO.getImgName();
        }
    }
}
