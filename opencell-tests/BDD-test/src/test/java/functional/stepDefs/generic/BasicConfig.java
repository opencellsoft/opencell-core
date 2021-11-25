package functional.stepDefs.generic;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.core.util.EnvironmentVariables;

import java.net.URISyntaxException;

public class BasicConfig {

    private static Actor actor;
    private static String featurePath;
    private EnvironmentVariables environmentVariables;

    @Before
    public void configureBaseUrl() {
        String restApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                .orElse("https://reqres.in/api");

        actor = Actor.named("I am the supervisor").whoCan(CallAnApi.at(restApiBaseUrl));
    }

    @Before
    public void setUpFeaturePath(Scenario scenario) throws URISyntaxException {
        featurePath = scenario.getUri().getRawSchemeSpecificPart();
    }

    public static Actor getActor() {
        return actor;
    }

    public static String getFeaturePath() {
        return featurePath;
    }
}
