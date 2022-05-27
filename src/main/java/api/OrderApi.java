package api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pogo.Order;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private Order order;

    @Step("Request to create an order")
    public ValidatableResponse createOrder(String token, Order order) {
        return given()
                .spec(Api.getApi())
                .auth().oauth2(token)
                .body(order)
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