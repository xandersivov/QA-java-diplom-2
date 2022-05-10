import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

    //логин под существующим пользователем
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
        Assert.assertEquals(message, 200, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    //логин с неверным логином и паролем
    @Test
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
        Assert.assertEquals(message, 401, statusCode);
        Assert.assertEquals("email or password are incorrect", message);
    }
}