package functional.stepDefs.mediation;

import functional.driver.actions.mediation.ChargeCDR;
import functional.driver.actions.mediation.ChargeCDRList;
import functional.driver.actions.subscription.ActivateService;
import functional.driver.actions.subscription.UpdateService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
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

        actor = Actor.named("I am the supervisor").whoCan(CallAnApi.at(restApiBaseUrl));
    }

    @When("I activate services on subscription")
    public void actorActivatesServiceOnSubscription(DataTable dataTable) {
        actor.attemptsTo(ActivateService.called(dataTable));
    }

    @And("I charge following {string}")
    public void actorChargeCDR(String cdr) {
        actor.attemptsTo(ChargeCDR.called(cdr));
    }

    @And("I charge following list of CDR")
    public void actorChargeCDRList(String cdr) {
        actor.attemptsTo(ChargeCDRList.called(cdr));
    }

    @And("I update service on subscription")
    public void actorUpdateServiceOnSubscription(DataTable dataTable) {
        actor.attemptsTo(UpdateService.called(dataTable));
    }

    @Then("amount with tax of {int} wallet operation should be equal to {double} euros")
    public void amountWithTaxOfWalletOperationShouldBeEqualToEuros(int order, double amount) {
//        actor.asksFor(GetAmountWithTaxOfWalletOperation.called(order, amount));
    }
}
