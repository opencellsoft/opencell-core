package functional.driver.actions.generic.crud;

import functional.SQLite.SQLiteManagement;
import functional.driver.utils.Constants;
import functional.driver.utils.JsonUtils;
import functional.driver.utils.KeyCloakAuthenticationHook;
import functional.driver.utils.ReflectionUtils;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

public class UpdateEntityBasedOnDatatable implements Task {

    private final String entityName;
    private final DataTable dataTable;

    public UpdateEntityBasedOnDatatable(String entityName, DataTable dataTable) {
        this.entityName = entityName;
        this.dataTable = dataTable;
    }

    public static UpdateEntityBasedOnDatatable called(String entity, DataTable dataTable) {
        return Tasks.instrumented(UpdateEntityBasedOnDatatable.class, entity, dataTable);
    }

    @Override
    @Step("{0} gets entity")
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = SQLiteManagement.selectTableEntityAndRs(entityName);
        Class<?> dtoClass = ReflectionUtils.getDtoClassByName(entityName);

        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> anInstance : table) {
            Object jsonBody = JsonUtils.getJsonBody(dtoClass, anInstance);
System.out.println("jsonBody : " + jsonBody);
            actor.attemptsTo(
                    Put.to(baseUrl)
                            .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                            .with(request -> request.header(
                                    HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                    .body(jsonBody)
                            )
            );

            // Always add this piece of code in each task to verify whether or not the request is successfully executed (i.e. status code 200)
            actor.should(
                    ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
            );
        }
    }

}
