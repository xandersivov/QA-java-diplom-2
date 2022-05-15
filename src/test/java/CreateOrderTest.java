import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class CreateOrderTest {

    private Order order;
    private UserApi userApi;
    private OrderApi orderApi;
    private Ingredients ingredients;

    public String body;
    public int statusCode;
    public String message;
    public String token;

    @Before
    public void setup() {
        orderApi = new OrderApi();
        userApi = new UserApi();
        order = new Order();
        ingredients = new Ingredients();

        token = userApi.registerAndAuthorizeValidUser().extract().path("accessToken");
        token = token.replaceAll("Bearer", "").trim();

    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.delete(token);
        }
    }


    @Test
    @DisplayName("Тест на создание заказа и с авторизацией")
    public void testCreateOrderAuthorized() {
        order.setIngredients(ingredients.getRandomList());
        body = new Gson().toJson(order);
        ValidatableResponse response = orderApi.createOrder(token, body);
        statusCode = response.extract().statusCode();
        message = response.extract().path("message");
        Assert.assertEquals(message, SC_OK, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }


    @Test
    @DisplayName("Тест на создание заказа и без авторизацией")
    public void testCreateOrderUnAuthorized() {
        order.setIngredients(ingredients.getRandomList());
        body = new Gson().toJson(order);
        ValidatableResponse response = orderApi.createOrder("", body);
        statusCode = response.extract().statusCode();
        message = response.extract().path("message");
        Assert.assertEquals(message, SC_OK, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    @DisplayName("Тест на создание заказа без ингридентов")
    @Test
    public void testCreateOrderWithNoIngredients() {
        body = new Gson().toJson(order.getIngredients());
        ValidatableResponse response = orderApi.createOrder(token, body);
        statusCode = response.extract().statusCode();
        Assert.assertEquals(SC_BAD_REQUEST, statusCode);
    }

    @DisplayName("Тест на создание заказа и не правильным хэшем")
    @Test
    public void testCreateOrderWithInvalidHash() {
        order.setIngredients(ingredients.getRandomInvalidList());
        body = new Gson().toJson(order);
        ValidatableResponse response = orderApi.createOrder(token, body);
        statusCode = response.extract().statusCode();
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}