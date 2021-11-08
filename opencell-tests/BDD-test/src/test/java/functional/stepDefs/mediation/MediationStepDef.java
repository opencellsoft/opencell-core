package functional.stepDefs.mediation;

import functional.driver.actions.generic.CreateEntity;
import functional.driver.actions.mediation.ImportCDR;
import functional.driver.actions.subscription.ActivateService;
import functional.driver.actions.subscription.UpdateService;
import functional.driver.assertions.GetAmountWithTaxOfWalletOperation;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.core.util.EnvironmentVariables;

public class MediationStepDef {

    private static Actor actor;
    private EnvironmentVariables environmentVariables;
    static String restApiBaseUrl;

    @Before
    public void configureBaseUrl() {
        restApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                .orElse("https://reqres.in/api");

        actor = Actor.named("Thang the supervisor").whoCan(CallAnApi.at(restApiBaseUrl));
    }

    @Given("Thang creates or updates entity {string}")
    public void actorCreateOrUpdateEntity(String entityDto) {
        actor.attemptsTo(CreateEntity.called(entityDto));
    }

    @When("Thang activates services {string} on subscription")
    public void actorActivatesServiceOnSubscription(String entityDto) {
        actor.attemptsTo(ActivateService.called(entityDto));
    }

    @And("Thang imports CDR {string}")
    public void actorImportCDR(String entityDto) {
        actor.attemptsTo(ImportCDR.called(entityDto));
    }

    @And("Thang updates service {string} on subscription")
    public void actorUpdateServiceOnSubscription(String entityDto) {
        actor.attemptsTo(UpdateService.called(entityDto));
    }

    @Then("amount with tax of {int} wallet operation should be equal to {double} euros")
    public void amountWithTaxOfWalletOperationShouldBeEqualToEuros(int order, double amount) {
        actor.asksFor(GetAmountWithTaxOfWalletOperation.called(order, amount));
    }
}
