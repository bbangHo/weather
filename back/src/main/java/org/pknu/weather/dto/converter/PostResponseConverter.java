package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.dto.PostResponse;

import java.util.List;

public class PostResponseConverter {

    public static PostResponse.Posts toPosts(Member member, List<Post> postList, boolean hasNext) {
        List<PostResponse.Post> list = postList.stream()
                .map(p -> toPost(member, p))
                .toList();

        if(hasNext) {
            list.remove(list.size() - 1);
        }

        return PostResponse.Posts.builder()
                .posts(list)
                .listSize(postList.size() - 1)
                .hasNext(hasNext)  // size + 1개를 더 조회해서 확인함
                .build();
    }

    public static PostResponse.Post toPost(Member member, Post post) {
        return PostResponse.Post.builder()
                .postId(post.getId())
                .profileImageUrl(member.getProfileImage())
                .memberName(member.getNickname())
                .sensitivity(member.getSensitivity())
                .city(member.getLocation().getCity())
                .street(member.getLocation().getStreet())
                .createdAt("n분전")       // TODO: 나중에 관련 클래스 구현
                .content(post.getContent())
                .build();
    }
}
