package functional.driver.actions.generic;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import functional.SQLite.SQLiteManagement;
import functional.driver.utils.Constants;
import functional.driver.utils.JsonObjectGenerator;
import functional.driver.utils.KeyCloakAuthenticationHook;
import io.cucumber.datatable.DataTable;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

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

    @SneakyThrows
    @Override
    @Step("{0} creates entity")
    public <T extends Actor> void performAs(T actor) {
        String url = SQLiteManagement.selectTableEntityAndRs(entity);

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        ObjectMapper mapper = new ObjectMapper();

        for (Map<String, String> columns : rows) {
            ObjectNode rootNode = mapper.createObjectNode();

            for (Map.Entry<String, String> entry : columns.entrySet()) {
                JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(entry.getKey()),
                        new TextNode(entry.getValue()));
            }

            actor.attemptsTo(
                    Post.to(url)
                            .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                            .with(request -> request.header(
                                    HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                    .body(rootNode)
                            )
            );

            // Always add this piece of code in each task to verify whether or not the request is successfully executed (i.e. status code 200)
            actor.should(
                    ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
            );
        }
    }

}
