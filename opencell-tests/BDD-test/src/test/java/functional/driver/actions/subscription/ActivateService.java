package functional.driver.actions.subscription;

import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.Map;

public class ActivateService implements Task {

    private final DataTable dataTable;

    public ActivateService(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public static ActivateService called(DataTable dataTable) {
        return Tasks.instrumented(ActivateService.class, dataTable);
    }

    @Override
    @Step("{0} activate services on subscription")
    public <T extends Actor> void performAs(T actor) {
        final String url = "/billing/subscription/activateServices";

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
//        Class<?> dtoClass = ReflectionUtils.getDtoMapperClassByName(entity);
//        String jsonBody = ApiUtils.getJsonBody(dtoClass, dataTable);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        for (Map<String, String> columns : rows) {
//            ObjectNode rootNode = mapper.createObjectNode();
//
//            for (Map.Entry<String, String> entry : columns.entrySet()) {
//                JsonObjectGenerator.setJsonPointerValue(rootNode, JsonPointer.compile(entry.getKey()),
//                        new TextNode(entry.getValue()));
//            }
//        }
//
//        actor.attemptsTo(
//                Post.to(url)
//                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                        .with(request -> request.header(
//                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
//                                .body(rootNode)
//                        )
//        );

        actor.should(
                ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
        );
    }
}
