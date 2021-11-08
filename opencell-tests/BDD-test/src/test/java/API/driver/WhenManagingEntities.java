package API.driver;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import Utils.URLFormat;
import io.cucumber.java.Before;
import io.cucumber.junit.CucumberSerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

@RunWith(CucumberSerenityRunner.class)
public class WhenManagingEntities {

    private static final String BASIC_AUTH = "Basic";
    private static final String OAUTH2 = "Bearer";

    private static final String BASIC_PATH = "src/test/resources/CRUD";

    private static Actor actor;
    private EnvironmentVariables environmentVariables;
    static String theRestApiBaseUrl;

    @Before
    public void configureBaseUrl() {
        theRestApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                .orElse("https://reqres.in/api");

        actor = Actor.named("Sam the supervisor").whoCan(CallAnApi.at(theRestApiBaseUrl));
    }

    public static String read_json_file(String directory, String filename) {
        String jsonPath = BASIC_PATH + Constants.SEPARATOR_SLASH + directory + Constants.SEPARATOR_SLASH + filename;

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(jsonPath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    // tag::find_an_individual_entity[]
    @Test
    public static void find_an_individual_entity() {

        actor.attemptsTo(
                Get.resource("/accountManagement/sellers/1")
        );

        actor.should(
                seeThatResponse( "Entity details should be correct",
                        response -> response.statusCode(200)
                                .body("data.first_name", equalTo("George"))
                                .body("data.last_name", equalTo("Bluth"))
                )
        );
    }
    // end::find_an_individual_entity[]

    @Test
    public static void add_an_entity(ApiInfo apiInfo) {
        final String url = URLFormat.formatCreateURL(apiInfo);
        // tag::add_a_new_entity[]
        actor.attemptsTo(
            Post.to(url)
                .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .with(request -> request.header(
                        HttpHeaders.AUTHORIZATION, OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                        .body(apiInfo.getRequestBody())
                )
        );

        actor.should(
                seeThatResponse("The entity should have been successfully added",
                        response -> response.statusCode(200))
        );
        // end::add_a_new_entity[]
    }

    // tag::update_an_entity[]
    @Test
    public static void update_an_entity(ApiInfo apiInfo) {
        final String url = URLFormat.formatUpdateURL(apiInfo);
        actor.attemptsTo(
            Put.to(url)
                .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .with(request -> request.header(
                        HttpHeaders.AUTHORIZATION, OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                        .body(apiInfo.getRequestBody())

//                        BASIC_AUTH + " " + "b3BlbmNlbGwuYWRtaW46b3BlbmNlbGwuYWRtaW4=")
//                        .body("{\"description\": \"" + apiInfo.getRequestBody() + "\"}")
                )
        );

        actor.should(
                seeThatResponse(response -> response.statusCode(200)
                        .body("updatedAt", not(isEmptyString())))
        );
    }
    // end::update_an_entity[]
}
