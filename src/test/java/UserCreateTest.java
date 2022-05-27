import api.UserApi;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pogo.UserData;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class UserCreateTest {

    private UserData userData;
    private UserApi userApi;
    private ValidatableResponse response;

    public String name;
    public String password;
    public String email;
    public String token;
    public int statusCode;
    public String message;

    @Before
    public void setUp() {
        Faker faker = new Faker();
        name = faker.name().firstName();
        email = name + "@yandex.ru";
        password = "Qwerty1234!";
        userData = new UserData();
        userApi = new UserApi();
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.delete(token);
        }
    }

    @Test
    @DisplayName("Тест создание уникального пользователя")
    public void testCreateUniqueUser() {
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        response = userApi.register(userData);
        token = response.extract().path("accessToken");
        message = response.extract().path("message");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(message, SC_OK, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    @Test
    @DisplayName("Тест создание пользователя, который уже зарегистрирован")
    public void testCreateNotUniqueUser() {
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        response = userApi.register(userData);
        token = response.extract().path("accessToken");
        ValidatableResponse testResponse = userApi.register(userData);
        message = testResponse.extract().path("message");
        statusCode = testResponse.extract().statusCode();
        Assert.assertEquals(message, SC_FORBIDDEN, statusCode);
        Assert.assertEquals("User already exists", message);
    }

    @Test
    @DisplayName("Тест создание пользователя, без заполнения обязательного поля(email)")
    public void testCreateUniqueUserWithInvalidRequest() {
        userData.setName(name);
        userData.setEmail(null);
        userData.setPassword(password);
        response = userApi.register(userData);
        token = response.extract().path("accessToken");
        message = response.extract().path("message");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(message, SC_FORBIDDEN, statusCode);
        Assert.assertEquals("Email, password and name are required fields", message);
    }
}