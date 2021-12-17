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
import java.util.List;
import java.util.Map;

public class GetEntity implements Task {

    private final String entityName;
    private final DataTable dataTable;

    public GetEntity(String entityName, DataTable dataTable) {
        this.entityName = entityName;
        this.dataTable = dataTable;
    }

    public static GetEntity called(String entity, DataTable dataTable) {
        return Tasks.instrumented(GetEntity.class, entity, dataTable);
    }

    @Override
    @Step("{0} gets entity")
    public <T extends Actor> void performAs(T actor) {
        String baseUrl = SQLiteManagement.selectTableEntityAndRs(entityName);

        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> anInstance : table) {

            String urlForGetRequest = ApiUtils.getUrlForGet(entityName, baseUrl, anInstance);

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
