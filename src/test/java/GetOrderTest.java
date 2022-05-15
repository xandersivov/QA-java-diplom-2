import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class GetOrderTest {

    private UserApi userApi;
    private OrderApi orderApi;

    public int statusCode;
    public String token;

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        userApi = new UserApi();
        Order order = new Order();
        Ingredients ingredients = new Ingredients();

        token = userApi.registerAndAuthorizeValidUser().extract().path("accessToken");
        token = token.replaceAll("Bearer", "").trim();

        order.setIngredients(ingredients.getRandomList());
        orderApi.createOrder(token, order);
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.delete(token);
        }
    }

    @Test
    @DisplayName("Тест на получение заказа с авторизацией")
    public void testGetOrderAuthorized() {
        ValidatableResponse response = orderApi.getUserOrderList(token);
        statusCode = response.extract().statusCode();
        Assert.assertEquals(SC_OK, statusCode);
        Assert.assertNotNull(response.extract().path("orders"));
    }

    @Test
    @DisplayName("Тест на получение заказа без авторизацией")
    public void testGetOrderUnAuthorized() {
        ValidatableResponse response = orderApi.getUserOrderList("");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(SC_UNAUTHORIZED, statusCode);
        Assert.assertEquals("unexpected response message", "You should be authorised", response.extract().path("message"));
    }
}