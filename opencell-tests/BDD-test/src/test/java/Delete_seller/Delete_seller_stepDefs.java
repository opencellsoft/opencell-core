package Delete_seller;

import Utils.Constants;
import Utils.KeyCloakAuthenticationHook;
import Utils.RestApiUtils;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;

public class Delete_seller_stepDefs {

    // These fields are required in the generation process
    private Scenario scenario;
    private String env;
    private String id;
    private String status;

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("A seller on {string}")
    public void aSellerOn(String arg0) {
        env = arg0;
    }

    @When("Field id filled by following id {string}")
    public void fieldIdFilledByFollowingId(String arg0) {
        id = arg0;
    }

    @Then("The status is now {string}")
    public void theStatusIsNow(String arg0) throws IOException {
        status = arg0;

        //--------------------------------------------------------------------
        // This piece of code tests creates a new Http client with credentials
        KeyCloakAuthenticationHook keyCloak = new KeyCloakAuthenticationHook();
        keyCloak.setProperties();
        keyCloak.authenticateAsAdmin();

        String url = env + Constants.PREFIX_DELETE_API + id;
System.out.println( "url Delete Here : " + url );

        // This line is to update the entity and to execute the assertion
        RestApiUtils.delete(url).assertThat().statusCode( Integer.valueOf( status ) );

    }
}
