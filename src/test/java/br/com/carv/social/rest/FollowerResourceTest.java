package br.com.carv.social.rest;

import br.com.carv.social.dto.FollowRequest;
import br.com.carv.social.model.Follower;
import br.com.carv.social.model.User;
import br.com.carv.social.repository.FollowerRepository;
import br.com.carv.social.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    public void setup() {
        User user = new User("Joao Gabriel Carvalho", 26);
        userRepository.persist(user);
        userId = user.getId();

        User userFollower = new User("Lais Mansano", 24);
        userRepository.persist(userFollower);
        followerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setFollower(userFollower);
        follower.setUser(user);
        followerRepository.persist(follower);

    }

    @Test
    public void same_user_as_follower() {

        FollowRequest request = new FollowRequest();
        request.setFollowerId(userId);

        RestAssured.given().contentType(ContentType.JSON)
                .body(request).pathParam("userId", userId)
                .when().put().then().statusCode(409).body(Matchers.is("You can't follow your self."));
    }

    @Test
    public void user_not_found() {

        FollowRequest request = new FollowRequest();
        request.setFollowerId(userId);

        Long nonexistentUserId = 999L;

        RestAssured.given().contentType(ContentType.JSON)
                .body(request).pathParam("userId", nonexistentUserId)
                .when().put().then().statusCode(404);
    }

    @Test
    public void should_user_follower() {

        FollowRequest request = new FollowRequest();
        request.setFollowerId(followerId);

        RestAssured.given().contentType(ContentType.JSON)
                .body(request).pathParam("userId", userId)
                .when().put().then().statusCode(204 );
    }

    @Test
    public void should_return_user_not_found_when_unfollowing() {

        Long nonexistentUserId = 999L;

        RestAssured.given().contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .queryParam("followerId", followerId)
                .when().delete().then().statusCode(404);
    }

    @Test
    public void should_unfollowing() {

        RestAssured.given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when().delete().then().statusCode(204);
    }

}