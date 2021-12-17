package functional.driver.actions.mediation;

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

public class ChargeCDRList implements Task {

    private final String entityDto;

    public ChargeCDRList(String entityDto) {
        this.entityDto = entityDto;
    }

    public static ChargeCDRList called(String entityDto) {
        return Tasks.instrumented(ChargeCDRList.class, entityDto);
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

        actor.should(
                ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
        );
    }
}
