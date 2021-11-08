package functional.driver.actions.subscription;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class ActivateService implements Task {

    private final String entityDto;

    public ActivateService(String entityDto) {
        this.entityDto = entityDto;
    }

    public static ActivateService called(String entityDto) {
        return Tasks.instrumented(ActivateService.class, entityDto);
    }

    @Override
    @Step("{0} activate services on subscription")
    public <T extends Actor> void performAs(T actor) {
        final String url = "/billing/subscription/activateServices";

        actor.attemptsTo(
                Post.to(url)
                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                        .with(request -> request.header(
                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                .body(entityDto)
                        )
        );

    }
}
