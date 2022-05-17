package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;

public class Api {
    public static final String API = "https://stellarburgers.nomoreparties.site/api/";

    public static RequestSpecification getApi() {
        return new RequestSpecBuilder()
                .setContentType(JSON)
                .setBaseUri(API)
                .build();
    }
}
