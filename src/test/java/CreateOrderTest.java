import com.google.gson.Gson;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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

    //с авторизацией
    @Test
    public void testCreateOrderAuthorized() {
        order.setIngredients(ingredients.getRandomList());
        body = new Gson().toJson(order);
        ValidatableResponse response = orderApi.create(token, body);
        statusCode = response.extract().statusCode();
        message = response.extract().path("message");
        Assert.assertEquals(message, 200, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    //без авторизации + без ингредиентов
    @Test
    public void testCreateOrderUnAuthorized() {
        order.setIngredients(ingredients.getRandomList());
        body = new Gson().toJson(order);
        ValidatableResponse response = orderApi.create("", body);
        statusCode = response.extract().statusCode();
        message = response.extract().path("message");
        Assert.assertEquals(message, 200, statusCode);
        Assert.assertTrue(message, response.extract().path("success"));
    }

    //без ингредиентов
    @Test
    public void testCreateOrderWithNoIngredients() {
        body = new Gson().toJson(order.getIngredients());
        ValidatableResponse response = orderApi.create(token, body);
        statusCode = response.extract().statusCode();
        Assert.assertEquals(400, statusCode);
    }

    //с неверным хешем ингредиентов
    @Test
    public void testCreateOrderWithInvalidHash() {
        order.setIngredients(ingredients.getRandomInvalidList());
        body = new Gson().toJson(order);
        ValidatableResponse response = orderApi.create(token, body);
        statusCode = response.extract().statusCode();
        Assert.assertEquals(500, statusCode);
    }
}