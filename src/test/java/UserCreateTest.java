import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public String body;

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

    //создать уникального пользователя
    @Test
    public void testCreateUniqueUser() {
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        body = new Gson().toJson(userData);
        response = userApi.register(body);
        token = response.extract().path("accessToken");
        message = response.extract().path("message");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(message, 200, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    //создать пользователя, который уже зарегистрирован
    @Test
    public void testCreateNotUniqueUser() {
        userData.setName(name);
        userData.setEmail(email);
        userData.setPassword(password);
        body = new Gson().toJson(userData);
        response = userApi.register(body);
        token = response.extract().path("accessToken");
        ValidatableResponse testResponse = userApi.register(body);
        message = testResponse.extract().path("message");
        statusCode = testResponse.extract().statusCode();
        Assert.assertEquals(message, 403, statusCode);
        Assert.assertEquals("User already exists", message);
    }

    //создать пользователя и не заполнить одно из обязательных полей(email)
    @Test
    public void testCreateUniqueUserWithInvalidRequest() {
        userData.setName(name);
        userData.setEmail(null);
        userData.setPassword(password);
        body = new Gson().toJson(userData);
        response = userApi.register(body);
        token = response.extract().path("accessToken");
        message = response.extract().path("message");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(message, 403, statusCode);
        Assert.assertEquals("Email, password and name are required fields", message);
    }
}