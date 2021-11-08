package Utils;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;

public class RestApiUtils {

    /**
     *
     * @param body
     * @return
     */
    public static ValidatableResponse post(String uri, String body) {
        RestAssured.defaultParser = Parser.JSON;

        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .post(System.getProperty( Constants.URL_OC ) + uri )
                .then();
    }

    /**
     *
     * @param body
     * @return
     */
    public static ValidatableResponse get(String uri, String body) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .get( System.getProperty( Constants.URL_OC ) + uri )
                .then();
    }
    
    
    /**
    *
    * @param uri
    * @return
    */
    public static ValidatableResponse delete(String uri) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .when().delete(uri).then();
    }

    public static ValidatableResponse put(String uri, String body) {
        return RestAssured.given()
                .auth().oauth2(KeyCloakAuthenticationHook.getToken())
                .contentType("application/json")
                .body(body)
                .when()
                .put(uri).then();
    }
}
