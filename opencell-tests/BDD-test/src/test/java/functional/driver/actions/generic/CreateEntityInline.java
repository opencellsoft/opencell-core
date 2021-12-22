package functional.driver.actions.generic;

import functional.SQLite.SQLiteManagement;
import functional.driver.utils.ApiUtils;
import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import functional.driver.utils.ReflectionUtils;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateEntityInline implements Task {

    private final String entity;
    private final DataTable dataTable;

    public CreateEntityInline(String entity, DataTable dataTable) {
        this.entity = entity;
        this.dataTable = dataTable;
    }

    public static CreateEntityInline called(String entity, DataTable dataTable) {
        return Tasks.instrumented(CreateEntityInline.class, entity, dataTable);
    }

    @Override
    @Step("{0} creates entity")
    public <T extends Actor> void performAs(T actor) {
        String url = SQLiteManagement.selectTableEntityAndRs(entity);
        Class<?> dtoClass = ReflectionUtils.getDtoClassByName(entity);
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> anInstance : table) {
            Object jsonBody = ApiUtils.getJsonBody(dtoClass, anInstance);
            actor.attemptsTo(
                    Post.to(url)
                            .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                            .with(request -> request.header(
                                    HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                    .body(jsonBody)
                            )
            );

            // Always add this piece of code in each task to verify whether or not the request is successfully executed (i.e. status code 200)
            if (SerenityRest.lastResponse().statusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR ||
                SerenityRest.lastResponse().statusCode() == HttpStatus.SC_CONFLICT){
                assertThat(SerenityRest.lastResponse().jsonPath().get("status").toString()).isEqualTo("FAIL");
            }
        }
    }

}
