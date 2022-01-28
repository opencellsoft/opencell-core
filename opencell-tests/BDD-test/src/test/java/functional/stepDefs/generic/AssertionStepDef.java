package functional.stepDefs.generic;

import functional.driver.assertions.GetAmountWithTaxOfWalletOperation;
import functional.driver.assertions.VerifyResponseOfLastRequest;
import functional.driver.assertions.VerifySuccessfulCreation;
import functional.driver.assertions.VerifySuccessfulUpdate;
import io.cucumber.java.en.Then;

public class AssertionStepDef {

    @Then("{string} {string} has {string} of value {string} euros")
    public void actorVerifyValueOfField(String order, String entity, String field, String expectedValue) {
        BasicConfig.getActor().asksFor(GetAmountWithTaxOfWalletOperation.called(order, entity, field, expectedValue));
    }

    @Then("^([^ \"]*) has a value of ([^ \"]*)")
    public void actorVerifyResponseOfLastRequest(String field, String expectedValue) {
        BasicConfig.getActor().asksFor(VerifyResponseOfLastRequest.called(field, expectedValue));
    }

    @Then("^([^ \"]*) is successfully created")
    public void actorVerifySuccessfulCreation(String entity) {
        BasicConfig.getActor().asksFor(VerifySuccessfulCreation.called(entity));
    }

    @Then("^these services are ([^ \"]*)")
    public void actorVerifyServices(String entity) {
        BasicConfig.getActor().asksFor(VerifySuccessfulCreation.called(entity));
    }

    @Then("^([^ \"]*) should be updated")
    public void actorVerifyUpdate(String entity) {
        BasicConfig.getActor().asksFor(VerifySuccessfulUpdate.called(entity));
    }

}
