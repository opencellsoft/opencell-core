package functional.driver.actions.subscription;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class UpdateService implements Task {

    private final String entityDto;

    public UpdateService(String entityDto) {
        this.entityDto = entityDto;
    }

    public static UpdateService called(String entityDto) {
        return Tasks.instrumented(UpdateService.class, entityDto);
    }

    @Override
    @Step("{0} update services on subscription")
    public <T extends Actor> void performAs(T actor) {
        final String url = "/billing/subscription/updateServices";

        actor.attemptsTo(
                Put.to(url)
                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                        .with(request -> request.header(
                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                .body(entityDto)
                        )
        );

        actor.should(
                ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
        );

    }
}
