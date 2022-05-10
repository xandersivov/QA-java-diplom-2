import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderApi {


    //запрос на создание заказа
    public ValidatableResponse create(String token, String body) {
        return given()
                .spec(Api.getApi())
                .auth().oauth2(token)
                .body(body)
                .when()
                .post("orders")
                .then();
    }

    //получение списка заказов
    public ValidatableResponse getUserOrderList(String token) {
        return given()
                .spec(Api.getApi())
                .auth()
                .oauth2(token)
                .get("orders")
                .then();
    }
}