package functional.driver.actions.generic;

import functional.SQLite.SQLiteManagement;
import functional.driver.utils.ApiUtils;
import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class GetEntity implements Task {

    private final String entity;
    private final DataTable dataTable;

    public GetEntity(String entity, DataTable dataTable) {
        this.entity = entity;
        this.dataTable = dataTable;
    }

    public static GetEntity called(String entity, DataTable dataTable) {
        return Tasks.instrumented(GetEntity.class, entity, dataTable);
    }

    @Override
    @Step("{0} gets entity")
    public <T extends Actor> void performAs(T actor) {
        String url = SQLiteManagement.selectTableEntityAndRs(entity);
System.out.println( "url : " + url );
        String completeUrl = ApiUtils.getCompleteUrl(entity, dataTable);
System.out.println( "completeUrl : " + completeUrl );

        actor.attemptsTo(
                Get.resource(url)
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
