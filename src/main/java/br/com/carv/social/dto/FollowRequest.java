package br.com.carv.social.dto;

public class FollowRequest {

    private Long followerId;

    public FollowRequest(){

    }

    public FollowRequest(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }
}
