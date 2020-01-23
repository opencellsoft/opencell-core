package com.opencell.test.utils;

import com.opencell.test.KeyCloakAuthenticationHook;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;

public class RestApiUtils {

    private static String uri = System.getProperty("opencell.url")+"/api/rest";
    /**
     *
     * @param api
     * @param body
     * @return
     */
    public static ValidatableResponse post(String api, String body) {
        RestAssured.defaultParser = Parser.JSON;
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .post(uri+api).then();
    }

    /**
     *
     * @param api
     * @param body
     * @return
     */
    public static ValidatableResponse get(String api, String body) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .get(uri+api).then();
    }
    
    
    /**
    *
    * @param api
    * @param body
    * @return
    */
    public static ValidatableResponse delete(String api, String body) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .delete(uri+api).then();
    }

    public static ValidatableResponse put(String api, String body) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .put(uri+api).then();
    }
}
