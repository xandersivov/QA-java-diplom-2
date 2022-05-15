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

public class UserLoginTest {

    private UserData userData;
    private UserApi userApi;
    private ValidatableResponse response;

    public String name;
    public String password;
    public String email;
    public String token;
    public int statusCode;
    public String body;
    public String message;

    @Before
    public void setup() {
        Faker faker = new Faker();
        name = faker.name().firstName();
        email = name + "@yandex.ru";
        password = "Qwerty1234!";
        userData = new UserData();
        userApi = new UserApi();
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        body = new Gson().toJson(userData);
        response = userApi.register(body);
        token = response.extract().path("accessToken");
    }

    @After
    public void tearDowm() {
        if (token != null) {
            userApi.delete(token);
        }
    }

    @DisplayName("Тест на логин под существующим пользователем")
    @Test
    public void testLoginWithValidUser() {
        userData = new UserData();
        userData.setEmail(email);
        userData.setPassword(password);
        body = new Gson().toJson(userData);
        response = userApi.login(body);
        token = response.extract().path("accessToken");
        message = response.extract().path("message");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(message, SC_OK, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    @Test
    @DisplayName("Тест на логин с неверным логином и паролем")
    public void testLoginWithInvalidUser() {
        userData = new UserData();
        userData.setEmail("Failed@com");
        userData.setPassword("123456");
        body = new Gson().toJson(userData);
        response = userApi.login(body);
        token = response.extract().path("accessToken");
        message = response.extract().path("message");
        statusCode = response.extract().statusCode();
        //Assert
        Assert.assertEquals(message, SC_UNAUTHORIZED, statusCode);
        Assert.assertEquals("email or password are incorrect", message);
    }
}