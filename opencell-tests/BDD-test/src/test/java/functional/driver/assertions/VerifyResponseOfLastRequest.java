package functional.driver.assertions;

import io.restassured.path.json.JsonPath;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import static org.assertj.core.api.Assertions.assertThat;

public class VerifyResponseOfLastRequest implements Question<Object> {

    private final String field;
    private final String expectedValue;

    public VerifyResponseOfLastRequest(String field, String expectedValue) {
        this.field = field;
        this.expectedValue = expectedValue;
    }

    public static Question<Object> called(String field, String expectedValue) {
        return new VerifyResponseOfLastRequest(field, expectedValue);
    }

    @Override
    public Object answeredBy(Actor actor) {

        JsonPath aResponse = SerenityRest.lastResponse().jsonPath();

        assertThat(aResponse.get(field).toString()).isEqualTo(expectedValue);

        return null;
    }
}
