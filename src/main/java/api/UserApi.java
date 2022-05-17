package api;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pogo.UserData;

import static io.restassured.RestAssured.given;

public class UserApi {

    private Faker faker;
    private UserData userData;
    private String name;
    private String password;
    private String email;
    private ValidatableResponse response;

    @Step("Create valid user registration request body")
    public UserData createValidRegistrationRequestBody() {
        userData = new UserData();
        faker = new Faker();
        name = faker.name().firstName();
        email = name + "@yandex.ru";
        password = name + "1234!";
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        return userData;
    }

    @Step("Create valid user authorization request body")
    public UserData createValidAuthRequestBody(String userEmail, String userPassword) {
        userData = new UserData();
        userData.setEmail(userEmail);
        userData.setPassword(userPassword);
        return userData;
    }

    @Step("Register valid user")
    public ValidatableResponse registerValidUser() {
        return register(createValidRegistrationRequestBody());
    }

    @Step("Register and authorization valid user")
    public ValidatableResponse registerAndAuthorizeValidUser() {
        response = registerValidUser();
        String userName = response.extract().path("user.name");
        String userEmail = response.extract().path("user.email");
        String userPassword = userName + "1234!";

        return login(createValidAuthRequestBody(userEmail, userPassword));
    }

    @Step("user registration request")
    public ValidatableResponse register(UserData body) {
        return given().spec(Api.getApi()).body(body).when().post("auth/register").then();
    }

    @Step("user delete request")
    public void delete(String token) {
        given()
                .spec(Api.getApi())
                .auth().oauth2(token.replaceAll("Bearer", "")
                        .trim())
                .when()
                .delete("auth/user")
                .then()
                .statusCode(202);
    }

    @Step("user authorization request")
    public ValidatableResponse login(UserData userData) {
        return given()
                .spec(Api.getApi())
                .body(userData)
                .when()
                .post("auth/login")
                .then();
    }

    @Step("user data edition request")
    public ValidatableResponse update(String token, UserData userData) {
        return given()
                .spec(Api.getApi())
                .auth()
                .oauth2(token)
                .and()
                .body(userData)
                .when()
                .patch("auth/user")
                .then();
    }
}