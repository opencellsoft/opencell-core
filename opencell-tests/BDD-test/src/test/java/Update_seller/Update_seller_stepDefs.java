package Update_seller;

import Utils.Constants;
import Utils.KeyCloakAuthenticationHook;
import Utils.RestApiUtils;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.simple.JSONObject;

public class Update_seller_stepDefs {

    // These fields are required in the generation process
    private Scenario scenario;
    private String env;
    private String id;
    private String status;

    private String code;
    private String description;

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("Update seller on {string}")
    public void updateSellerOn(String arg0) {
        env = arg0;
    }

    @When("Field id filled by {string}")
    public void fieldIdFilledBy(String arg0) {
        id = arg0;
    }

    @And("Field code filled by {string}")
    public void fieldCodeFilledBy(String arg0) {
        code = arg0;
    }

    @And("Field description filled by {string}")
    public void fieldDescriptionFilledBy(String arg0) {
        description = arg0;
    }

    @Then("The status is {string}")
    public void theStatusIs(String arg0) {
        status = arg0;

        //--------------------------------------------------------------------
        // This piece of code tests creates a new Http client with credentials
        KeyCloakAuthenticationHook keyCloak = new KeyCloakAuthenticationHook();
        keyCloak.setProperties();
        keyCloak.authenticateAsAdmin();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("description", description);
        String body = jsonObject.toString();

        String url = env + Constants.PREFIX_PUT_API + id;
//System.out.println( "url Update Here : " + url );
//        String body = "{\"code\":\"" + code + "\",\"description\":\"" + description + "\"}";

        // This line is to update the entity and to execute the assertion
        RestApiUtils.put( url, body ).assertThat().statusCode( Integer.valueOf( status ) );

    }
}
