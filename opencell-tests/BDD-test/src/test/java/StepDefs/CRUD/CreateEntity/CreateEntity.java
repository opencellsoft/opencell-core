package StepDefs.CRUD.CreateEntity;

import Driver.ApiInfo;
import Driver.WhenManagingEntities;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CreateEntity {

    // These fields are required in the generation process of the request Update an entity
    private String apiVer;
    private String businessDomainPath;
    private String entity;
    private String codeOrId;
    private String body;
    private int status;

    @Given("Actor wants to test create operation")
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

    @And("Entity {string}")
    public void entity(String arg0) {
        entity = arg0;
    }

    @And("Body request given by {string}")
    public void bodyRequestGivenBy(String arg0) {
        body = WhenManagingEntities.read_json_file(entity, arg0);
    }

    @Then("The test is {string}")
    public void theTestIs(String arg0) {
        ApiInfo apiInfo = new ApiInfo(apiVer, businessDomainPath, entity, codeOrId, body);
        WhenManagingEntities.add_an_entity(apiInfo);
    }
}
