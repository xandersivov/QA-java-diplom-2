import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class UserUpdateTest {

    private UserData userData;
    private UserApi userApi;
    private ValidatableResponse updatedResponse;

    public String token;
    public String userEmail;
    public String userName;
    public String newEmail;
    public String newName;
    public String body;
    public String updatedEmail;
    public String updatedName;
    public int statusCode;
    public String message;

    @Before
    public void setup() {
        userApi = new UserApi();
        Faker faker = new Faker();
        ValidatableResponse response = userApi.registerAndAuthorizeValidUser();
        token = response.extract().path("accessToken");
        token = token.replaceAll("Bearer", "").trim();
        userName = response.extract().path("user.name");
        userEmail = response.extract().path("user.email");
        newName = faker.name().firstName().toLowerCase();
        newEmail = newName + "@yandex.ru";
        userData = new UserData();
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.delete(token);
        }
    }

    @Test
    @DisplayName("Тест на измение данных пользователя (email) с авторизацией")
    public void testUpdateEmailAuthorized() {
        userData.setEmail(newEmail);
        body = new Gson().toJson(userData);
        updatedResponse = userApi.update(token, body);
        updatedEmail = updatedResponse.extract().path("user.email");
        statusCode = updatedResponse.extract().statusCode();
        message = updatedResponse.extract().path("message");
        Assert.assertEquals(message, SC_OK, statusCode);
        Assert.assertEquals(message, newEmail, updatedEmail);
    }

    @Test
    @DisplayName("Тест на измение данных пользователя (name) с авторизацией")
    public void testUpdateUserAuthorized() {
        userData.setName(newName);
        body = new Gson().toJson(userData);
        updatedResponse = userApi.update(token, body);
        updatedName = updatedResponse.extract().path("user.name");
        statusCode = updatedResponse.extract().statusCode();
        message = updatedResponse.extract().path("message");
        Assert.assertEquals(message, SC_OK, statusCode);
        Assert.assertEquals(message, newName, updatedName);
    }

    @Test
    @DisplayName("Тест на измение данных пользователя (email) без авторизации")
    public void testNotUpdateEmailUnAuthorized() {
        userData.setEmail(newEmail);
        body = new Gson().toJson(userData);
        updatedResponse = userApi.update("", body);
        updatedEmail = updatedResponse.extract().path("user.email");
        statusCode = updatedResponse.extract().statusCode();
        message = updatedResponse.extract().path("message");
        Assert.assertEquals("Can edit user data unauthorized", SC_UNAUTHORIZED, statusCode);
        Assert.assertEquals("response body is different", "You should be authorised", message);
    }

    @Test
    @DisplayName("Тест на измение данных пользователя (name) без авторизации")
    public void testNotUpdateUserUnAuthorized() {
        userData.setName(newName);
        body = new Gson().toJson(userData);
        updatedResponse = userApi.update("", body);
        updatedName = updatedResponse.extract().path("user.name");
        statusCode = updatedResponse.extract().statusCode();
        message = updatedResponse.extract().path("message");
        Assert.assertEquals("Can edit user data unauthorized", SC_UNAUTHORIZED, statusCode);
        Assert.assertEquals("response body is different", "You should be authorised", message);
    }
}
