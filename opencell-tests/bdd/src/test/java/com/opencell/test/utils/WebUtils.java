package com.opencell.test.utils;

import com.opencell.test.KeyCloakAuthenticationHook;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

public class WebUtils {
    private static String uri = System.getProperty("opencell.url");
    
    public static ValidatableResponse get(String url) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .when()
                .get(uri+url).then();
    }
}
