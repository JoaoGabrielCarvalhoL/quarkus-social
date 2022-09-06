package br.com.carv.social.dto;

import java.util.List;

public class FollowersPerUserResponse {

    private Integer count;
    private List<FollowerResponse> content;

    public FollowersPerUserResponse() {

    }
    public FollowersPerUserResponse(Integer count, List<FollowerResponse> content) {
        this.count  = count;
        this.content = content;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<FollowerResponse> getContent() {
        return content;
    }

    public void setContent(List<FollowerResponse> content) {
        this.content = content;
    }
}
