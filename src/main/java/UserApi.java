import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserApi {

    private Faker faker;
    private UserData userData;
    private String name;
    private String password;
    private String email;
    private ValidatableResponse response;

    @Step("Create valid user registration request body")
    public String createValidRegistrationRequestBody() {
        userData = new UserData();
        faker = new Faker();
        name = faker.name().firstName();
        email = name + "@yandex.ru";
        password = name + "1234!";
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        return new Gson().toJson(userData);
    }

    @Step("Create valid user authorization request body")
    public String createValidAuthRequestBody(String userEmail, String userPassword) {
        userData = new UserData();
        userData.setEmail(userEmail);
        userData.setPassword(userPassword);
        return new Gson().toJson(userData);
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
    public ValidatableResponse register(String body) {
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
    public ValidatableResponse login(String body) {
        return given()
                .spec(Api.getApi())
                .body(body)
                .when()
                .post("auth/login")
                .then();
    }

    @Step("user data edition request")
    public ValidatableResponse update(String token, String body) {
        return given()
                .spec(Api.getApi())
                .auth()
                .oauth2(token)
                .and()
                .body(body)
                .when()
                .patch("auth/user")
                .then();
    }
}