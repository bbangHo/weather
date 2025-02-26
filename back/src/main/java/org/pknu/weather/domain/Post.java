package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.pknu.weather.domain.common.PostType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Recommendation> recommendationList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'WEATHER'")
    @Builder.Default
    private PostType postType = PostType.WEATHER;

    public void addTag(Tag tag) {
        if(this.tag != null) {
            this.tag.addPost(this);
        }
        this.tag = tag;
        if(tag != null) {
            tag.addPost(this);
        }
    }
}
