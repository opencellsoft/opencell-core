package functional.driver.actions.generic;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class CreateEntity implements Task {

    private final String entityDto;

    public CreateEntity(String entityDto) {
        this.entityDto = entityDto;
    }

    public static CreateEntity called(String entityDto) {
        return Tasks.instrumented(CreateEntity.class, entityDto);
    }

    @Override
    @Step("{0} creates entity")
    public <T extends Actor> void performAs(T actor) {
        final String url = "/billing/subscription/createOrUpdate";
        actor.attemptsTo(
                Post.to(url)
                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                        .with(request -> request.header(
                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                .body(entityDto)
                        )
        );

        // Always add this piece of code in each task to verify whether or not the request is successfully executed (i.e. status code 200)
        actor.should(
                ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
        );
    }

}
