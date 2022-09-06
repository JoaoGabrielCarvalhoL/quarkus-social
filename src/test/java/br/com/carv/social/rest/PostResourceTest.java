package br.com.carv.social.rest;

import br.com.carv.social.dto.CreatePostRequest;
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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;

    Long userNotFollowerId;

    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup() {
        User user = new User("Joao Gabriel Carvalho", 26);
        userRepository.persist(user);
        userId = user.getId();

        User nonFollower = new User("Lais Mansano", 24);
        userRepository.persist(nonFollower);
        userNotFollowerId = nonFollower.getId();

        User userFollower = new User("Test", 100);
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    public void should_create_post() {

        CreatePostRequest post = new CreatePostRequest();
        post.setText("Post test");

        RestAssured.given().contentType(ContentType.JSON)
                .body(post).pathParam("userId", userId)
                .when().post().then().statusCode(201);
    }

    @Test
    public void should_return_error_when_create_post_on_user_not_found() {
        CreatePostRequest post = new CreatePostRequest();
        post.setText("Test");
        RestAssured.given().contentType(ContentType.JSON)
                .body(post).pathParam("userId", 100)
                .when().post().then().statusCode(404);
    }

    @Test
    public void should_return_error_when_user_not_found() {
        Long nonexistentUserId = 999L;

        RestAssured.given()
                .pathParam("userId", nonexistentUserId)
                .when().get().then().statusCode(404);
    }

    @Test
    public void should_return_error_when_follower_header_not_send() {

        RestAssured.given().pathParam("userId", userId)
                .when().get().then().statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    public void should_return_error_when_follower_not_found() {

        Long nonexistentFollowerId = 999L;

        RestAssured.given().pathParam("userId", userId).header("followerId", nonexistentFollowerId)
                .when().get().then().statusCode(400)
                .body(Matchers.is("Nonexistent followerId"));
    }

    @Test
    public void should_return_error_when_not_a_follower() {

        RestAssured.given().pathParam("userId", userId).header("followerId", userNotFollowerId)
                .when().get().then().statusCode(403)
                .body(Matchers.is("You can't see these posts."));
    }

    @Test
    public void should_get_posts() {
        RestAssured.given().pathParam("userId", userId).header("followerId", userFollowerId)
                .when().get().then().statusCode(200)
                .body("size()", Matchers.is(0));
    }
}