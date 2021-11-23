package functional.driver.actions.subscription;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import functional.driver.utils.Constants;
import functional.driver.utils.JsonObjectGenerator;
import functional.driver.utils.KeyCloakAuthenticationHook;
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

public class UpdateService implements Task {

    private final DataTable dataTable;

    public UpdateService(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public static UpdateService called(DataTable dataTable) {
        return Tasks.instrumented(UpdateService.class, dataTable);
    }

    @Override
    @Step("{0} update services on subscription")
    public <T extends Actor> void performAs(T actor) {
        final String url = "/billing/subscription/updateServices";

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        ObjectMapper mapper = new ObjectMapper();

        for (Map<String, String> columns : rows) {
            ObjectNode rootNode = mapper.createObjectNode();

            for (Map.Entry<String, String> entry : columns.entrySet()) {
                JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(entry.getKey()),
                        new TextNode(entry.getValue()));
            }

            actor.attemptsTo(
                    Put.to(url)
                            .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                            .with(request -> request.header(
                                    HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                    .body(rootNode)
                            )
            );

            actor.should(
                    ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
            );
        }
    }
}
