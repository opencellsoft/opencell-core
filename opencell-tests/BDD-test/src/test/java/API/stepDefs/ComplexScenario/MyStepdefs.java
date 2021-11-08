package API.stepDefs.ComplexScenario;

import functional.driver.utils.Constants;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyStepdefs {
    private List<String> scenarioNames;

    @Given("complex scenario composed of {string}")
    public void complexScenarioComposedOf(String arg0) {
        scenarioNames = new ArrayList<>(Arrays.asList(arg0.split(Constants.AND_LOGIC)));
    }

    @Then("execute a complex scenario")
    public void executeAComplexScenario() throws IOException {

//        Process process = Runtime.getRuntime()
//                .exec("mvn.cmd test -Dcucumber.filter.tags=@UpdateSeller && mvn.cmd test -Dcucumber.filter.tags=@DeleteSeller");

        StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= scenarioNames.size() - 1; i++) {
            finalCommand.append(Constants.BASE_COMMAND + scenarioNames.get(i));
            if (i < scenarioNames.size() - 1)
                finalCommand.append(Constants.AND_CMD);
        }
System.out.println( "finalCommand : " + finalCommand.toString() );
        Process process = Runtime.getRuntime().exec(finalCommand.toString());

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
