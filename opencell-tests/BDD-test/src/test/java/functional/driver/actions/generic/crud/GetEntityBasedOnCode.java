package functional.driver.actions.generic.crud;

import functional.SQLite.SQLiteManagement;
import functional.driver.utils.ApiUtils;
import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class GetEntityBasedOnCode implements Task {

    private final String entityName;
    private final String entityCodes;

    public GetEntityBasedOnCode(String entityName, String entityCodes) {
        this.entityName = entityName;
        this.entityCodes = entityCodes;
    }

    public static GetEntityBasedOnCode called(String entityName, String entityCode) {
        return Tasks.instrumented(GetEntityBasedOnCode.class, entityName, entityCode);
    }

    @Override
    @Step("{0} gets entity")
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = SQLiteManagement.selectTableEntityAndRs(entityName);

        String[] codes = entityCodes.split(Constants.COMMA);

        for (String entityCode : codes){
            String urlForGetRequest = ApiUtils.getUrlForGetInLine(entityName, baseUrl, entityCode);

            actor.attemptsTo(
                    Get.resource(urlForGetRequest)
                            .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                            .with(request -> request.header(
                                    HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                            )
            );

            // Always add this piece of code in each task to verify whether or not the request is successfully executed (i.e. status code 200)
            actor.should(
                    ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
            );
        }

    }

}
