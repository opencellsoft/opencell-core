package functional.driver.actions.mediation;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class ChargeCDR implements Task {

    private final String entityDto;

    public ChargeCDR(String entityDto) {
        this.entityDto = entityDto;
    }

    public static ChargeCDR called(String entityDto) {
        return Tasks.instrumented(ChargeCDR.class, entityDto);
    }

    @Override
    @Step("{0} charge a CDR and transform to EDR")
    public <T extends Actor> void performAs(T actor) {
        final String url = "/billing/mediation/chargeCdr";
        actor.attemptsTo(
                Post.to(url)
                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                        .with(request -> request.header(
                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                .body(entityDto)
                        )
        );

        if (SerenityRest.lastResponse().statusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR){
            assertThat(SerenityRest.lastResponse().jsonPath().get("status").toString()).isEqualTo("FAIL");
        }
    }
}
