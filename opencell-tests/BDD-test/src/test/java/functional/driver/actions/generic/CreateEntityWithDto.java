package functional.driver.actions.generic;

import functional.SQLite.SQLiteManagement;
import functional.driver.utils.Constants;
import functional.driver.utils.FileManagement;
import functional.driver.utils.KeyCloakAuthenticationHook;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.thucydides.core.annotations.Step;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class CreateEntityWithDto implements Task {

    private final String entity;
    private final String entityDto;
    private final String featurePath;

    public CreateEntityWithDto(String entity, String entityDto, String featurePath) {
        this.entity = entity;
        this.entityDto = entityDto;
        this.featurePath = featurePath;
    }

    public static CreateEntityWithDto called(String entity, String entityDto, String featurePath) {
        return Tasks.instrumented(CreateEntityWithDto.class, entity, entityDto, featurePath);
    }

    @SneakyThrows
    @Override
    @Step("{0} creates entity")
    public <T extends Actor> void performAs(T actor) {
        String url = SQLiteManagement.selectTableEntityAndRs(entity);
        String dto = FileManagement.readEntityDto(featurePath, entityDto);

        actor.attemptsTo(
                Post.to(url)
                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                        .with(request -> request.header(
                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                .body(dto)
                        )
        );

        // Always add this piece of code in each task to verify whether or not the request is successfully executed (i.e. status code 200)
        actor.should(
                ResponseConsequence.seeThatResponse(response -> response.statusCode(200))
        );
    }

}
