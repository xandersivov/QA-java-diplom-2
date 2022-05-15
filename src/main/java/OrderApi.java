import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderApi {


    @Step ("Request to create an order")
    public ValidatableResponse createOrder(String token, String body) {
        return given()
                .spec(Api.getApi())
                .auth().oauth2(token)
                .body(body)
                .when()
                .post("orders")
                .then();
    }

    @Step("Getting a list of orders")
    public ValidatableResponse getUserOrderList(String token) {
        return given()
                .spec(Api.getApi())
                .auth()
                .oauth2(token)
                .get("orders")
                .then();
    }
}