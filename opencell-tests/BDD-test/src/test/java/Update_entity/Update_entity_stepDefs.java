package Update_entity;

import Utils.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import org.apache.http.HttpStatus;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Update_entity_stepDefs {

    // These fields are required in the generation process of the request UPDATE
    private String entity;
    private String id;
    private String payload;
    private String url;
    private int status;

    // These fields are not required in the generation process of the request UPDATE
    private String description;

    @Given("Update {string} with {string}")
    public void updateWith(String arg0, String arg1) throws ParseException, IOException {
        entity = arg0;
        id = arg1;

        //--------------------------------------------------------------------
        // This piece of code tests creates a new instance of Keycloak token
        KeyCloakAuthenticationHook single_instance = KeyCloakAuthenticationHook.getInstance();
        single_instance.setProperties();
        single_instance.authenticateAsAdmin();

        url = Constants.PREFIX_PUT_API + entity + Constants.SEPARATOR_SLASH + id;

        ExtractableResponse aResponse =
                RestApiUtils.post( url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE ).extract();

        // A request POST tests existence of entity based on id
        assertEquals( aResponse.statusCode(), HttpStatus.SC_OK );

        // Create the new payload from the response of the request POST
        payload = Payload.generatePayload( aResponse.asString() );

//System.out.println( "payload : " + payload );
    }

    @When("All fields tested")
    public void allFieldsTested() {

    }

    @When("Fields filled by {string}")
    public void fieldsFilledBy(String arg0) throws ParseException {
        description = arg0;
        Map<String, String> updatedFields = new HashMap<>();

        updatedFields.put( "description", description );
        payload = Payload.updatePayload( payload, updatedFields );

        // Read payload from json file indicated in parameter
//        payloadPath = arg0;
//
//        StringBuilder contentBuilder = new StringBuilder();
//
//        try (Stream<String> stream =
//                     Files.lines( Paths.get(payloadPath), StandardCharsets.UTF_8))
//        {
//            stream.forEach(s -> contentBuilder.append(s).append("\n"));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        payload = contentBuilder.toString();
    }

    @Then("The status is {int}")
    public void theStatusIs(Integer arg0) throws ParseException {
        status = arg0;

        // This line is used to update the entity and to execute the assertion
        RestApiUtils.put( url, payload ).assertThat().statusCode( status );
        // This piece of code is used to verify if the request has updated the entity
        String aResult = RestApiUtils.post( url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE )
                .extract().asString();
        Payload.comparePayloadToResult( payload, aResult, entity );
    }
}
