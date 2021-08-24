package StepDefs.CRUD.UpdateEntity;

import Driver.ApiInfo;
import Driver.WhenManagingEntities;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UpdateEntity {

    // These fields are required in the generation process of the request Update an entity
    private String apiVer;
    private String businessDomainPath;
    private String entity;
    private String codeOrId;
    private String body;
    private int status;

    @Given("Actor wants to test update operation")
    public void actorWantsToTestOnApiVersionWithDomainPath() {
    }

    @When("API version {string}")
    public void apiVersion(String arg0) {
        apiVer = arg0;
    }

    @And("Business domain {string}")
    public void businessDomain(String arg0) {
        businessDomainPath = arg0;
    }

    @And("Entity {string} with code or id {string}")
    public void entityWithCodeOrId(String arg0, String arg1) {
        entity = arg0;
        codeOrId = arg1;
    }

    @And("Body request given by {string}")
    public void bodyRequestGivenBy(String arg0) {
        body = WhenManagingEntities.read_json_file(entity, arg0);
    }

    @Then("The test is {string}")
    public void theTestIs(String arg0) {
        ApiInfo apiInfo = new ApiInfo(apiVer, businessDomainPath, entity, codeOrId, body);
        WhenManagingEntities.update_an_entity(apiInfo);
    }
}
