package API.stepDefs.CRUD.UpdateSeller;

import API.driver.ApiInfo;
import API.driver.WhenManagingEntities;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.simple.JSONObject;

public class UpdateSeller {

    private String apiVer;
    private String businessDomainPath;
    private String entity;
    private String codeOrId;
    private String field;
    private String data;
    private String status;

    @Given("Actor wants to test on API version {string} with business domain {string}")
    public void actorWantsToTestOnApiVersionWithDomainPath(String arg0, String arg1) {
        apiVer = arg0;
        businessDomainPath = arg1;
    }

    @When("Entity {string} with code or id {string}")
    public void entitySellerWithCodeOrId(String arg0, String arg1) {
        entity = arg0;
        codeOrId = arg1;
    }

    @And("Field {string} filled by {string}")
    public void fieldFilledBy(String arg0, String arg1) {
        field = arg0;
        data = arg1;
    }

    @Then("The test is {string}")
    public void theTestIs(String arg0) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(field, data);
        String body = jsonObject.toString();

        ApiInfo apiInfo = new ApiInfo(apiVer, businessDomainPath, entity, codeOrId, body);
        WhenManagingEntities.update_an_entity(apiInfo);
    }
}
