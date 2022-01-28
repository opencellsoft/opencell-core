package functional.driver.assertions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.apache.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class VerifySuccessfulUpdate implements Question<Object> {

    private final String entity;

    public VerifySuccessfulUpdate(String entity) {
        this.entity = entity;
    }

    public static Question<Object> called(String entity) {
        return new VerifySuccessfulUpdate(entity);
    }

    @Override
    public Object answeredBy(Actor actor) {

        assertThat(SerenityRest.lastResponse().statusCode()).isEqualTo(HttpStatus.SC_OK);

        return null;
    }
}
