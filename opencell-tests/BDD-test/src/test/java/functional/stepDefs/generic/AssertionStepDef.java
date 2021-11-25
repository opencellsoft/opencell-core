package functional.stepDefs.generic;

import functional.driver.assertions.GetAmountWithTaxOfWalletOperation;
import io.cucumber.java.en.Then;

public class AssertionStepDef {

    @Then("{string} {string} has {string} of value {string} euros")
    public void actorVerifyValueOfField(String order, String entity, String field, String expectedValue) {
        BasicConfig.getActor().asksFor(GetAmountWithTaxOfWalletOperation.called(order, entity, field, expectedValue));
    }

}
