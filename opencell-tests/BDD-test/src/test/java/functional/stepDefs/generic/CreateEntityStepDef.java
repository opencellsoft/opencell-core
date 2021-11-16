package functional.stepDefs.generic;

import functional.driver.actions.generic.CreateEntityWithDto;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.core.util.EnvironmentVariables;

import java.net.URISyntaxException;
import java.util.List;

public class CreateEntityStepDef {

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

    @Given("^I create entity \"([^\"]*)\" from dto \"([^\"]*)\"$")
    public void actorCreateEntityFromDto(String entity, String entityDto) {
        actor.attemptsTo(CreateEntityWithDto.called(entity, entityDto, featurePath));
    }

    @Given("^I create entity from feature \"[^\"]*\"")
    public void actorCreateEntityFromFeature(String featurePath) {
    }

    @Given("^I create entity \"([^\"]*)\", with field and value (([^\":,]* : [^\":,]*)*)$")
    public void actorCreateEntity(String entity, List<String> entries) {
        System.out.println("entity : " + entity);
        for (String arg : entries) {
            // do smth with arg.
            System.out.println("arg DAY NE : " + arg);
        }
    }

    @And("^with field \"[^\"]*\" and value \"[^\"]*\"")
    public void withFieldAndValue(String field, String value) {

    }
}
