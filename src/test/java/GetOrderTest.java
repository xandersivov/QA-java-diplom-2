import com.google.gson.Gson;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class GetOrderTest {

    private UserApi userApi;
    private OrderApi orderApi;

    public String body;
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
        body = new Gson().toJson(order);
        orderApi.create(token, body);
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.delete(token);
        }
    }

    @Test
    public void testCreateOrderAuthorized() {
        ValidatableResponse response = orderApi.getUserOrderList(token);
        statusCode = response.extract().statusCode();
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(response.extract().path("orders"));
    }

    @Test
    public void testCreateOrderUnAuthorized() {
        ValidatableResponse response = orderApi.getUserOrderList("");
        statusCode = response.extract().statusCode();
        Assert.assertEquals(401, statusCode);
        Assert.assertEquals("unexpected response message", "You should be authorised", response.extract().path("message"));
    }
}