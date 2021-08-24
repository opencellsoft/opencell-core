package StepDefs.CRUD.DeleteSeller;

import Utils.Constants;
import Utils.RestApiUtils;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Delete_seller_stepDefs {

    // These fields are required in the generation process
    private Scenario scenario;
    private String env;
    private String id;
    private String status;

    @Given("A seller on {string}")
    public void aSellerOn(String arg0) {
        env = arg0;
    }

    @When("Field id filled by following id {string}")
    public void fieldIdFilledByFollowingId(String arg0) {
        id = arg0;
    }

    @Then("The status is now {string}")
    public void theStatusIsNow(String arg0) {
        status = arg0;

        String url = env + Constants.PREFIX_DELETE_API + id;

        // This line is to update the entity and to execute the assertion
        RestApiUtils.delete(url).assertThat().statusCode( Integer.valueOf( status ) );

    }
}
