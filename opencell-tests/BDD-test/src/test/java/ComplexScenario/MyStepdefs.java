package ComplexScenario;

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
        scenarioNames = new ArrayList<>(Arrays.asList(arg0.split(" ")));
    }

    @Then("execute a complex scenario")
    public void executeAComplexScenario() throws IOException {
        System.out.println( "test executeAComplexScenario HERE" );

//        Process process = Runtime.getRuntime()
//                .exec("mvn.cmd test -Dcucumber.filter.tags=@UpdateSeller");

//        Process process = Runtime.getRuntime()
//                .exec("mvn.cmd test -Dcucumber.filter.tags=@UpdateSeller && mvn.cmd test -Dcucumber.filter.tags=@DeleteSeller");

        Process process = Runtime.getRuntime()
                .exec("mvn.cmd test -Dcucumber.filter.tags=@UpdateSeller && mvn.cmd test -Dcucumber.filter.tags=@DeleteSeller");

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println("line : " + line);
        }
    }
}
