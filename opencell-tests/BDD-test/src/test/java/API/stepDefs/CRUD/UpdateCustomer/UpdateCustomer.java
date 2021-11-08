package API.stepDefs.CRUD.UpdateCustomer;

import API.driver.ApiInfo;
import API.driver.WhenManagingEntities;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.simple.JSONObject;

public class UpdateCustomer {

    private String apiVer;
    private String businessDomainPath;
    private String entity;
    private String codeOrId;
    private String field1;
    private String data1;
    private String field2;
    private String data2;
    private String status;

    @Given("Actor wants to test on API version {string} with business domain {string}")
    public void actorWantsToTestOnApiVersionWithDomainPath(String arg0, String arg1) {
        apiVer = arg0;
        businessDomainPath = arg1;
    }

    @When("Entity {string} with code or id {string}")
    public void entityWithCodeOrId(String arg0, String arg1) {
        entity = arg0;
        codeOrId = arg1;
    }

    @And("Field 1 {string} filled by {string}")
    public void field1FilledBy(String arg0, String arg1) {
        field1 = arg0;
        data1 = arg1;
    }

    @And("Field 2 {string} filled by {string}")
    public void field2FilledBy(String arg0, String arg1) {
        field2 = arg0;
        data2 = arg1;
    }

    @Then("The test is {string}")
    public void theTestIs(String arg0) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(field1, data1);
        jsonObject.put(field2, data2);
        String body = jsonObject.toString();

        ApiInfo apiInfo = new ApiInfo(apiVer, businessDomainPath, entity, codeOrId, body);
        WhenManagingEntities.update_an_entity(apiInfo);
    }
}
