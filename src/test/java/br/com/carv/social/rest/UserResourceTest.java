package br.com.carv.social.rest;

import br.com.carv.social.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @Test
    @Order(1)
    public void should_create_user() {
        CreateUserRequest user = new CreateUserRequest();
        user.setName("Joao Gabriel Carvalho");
        user.setAge(26);

        Response response = RestAssured.given().contentType(ContentType.JSON).body(user)
                .when().post("/users")
                .then().extract().response();
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @Order(2)
    public void should_throw_validation_error_when_user_not_valid() {
        CreateUserRequest user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);
        Response response = RestAssured.given().contentType(ContentType.JSON).body(user)
                .when().post("/users")
                .then().extract().response();
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertNotNull("Validation Error", response.jsonPath().getString("message"));

    }

    @Test
    @Order(3)
    public void should_return_all_users(){
        RestAssured.given().contentType(ContentType.JSON).when().get("/users").then()
                .statusCode(200).body("size()", Matchers.is(1));
    }

}