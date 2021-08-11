package ComplexScenario;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyStepdefs {
    private List<String> scenarioNames;

    @Before
    public void setUp(Scenario scenario) throws Exception {

        List<String> tags = (ArrayList<String>) scenario.getSourceTagNames();
        String name = scenario.getName();
        System.out.println("name : " + name);
        System.out.println("At Hooks : " + scenario.getId());
        for (String aTag : tags)
            System.out.println( "aTag : " + aTag );

//        Iterator ite = tags.iterator();
//
//        while (ite.hasNext()) {
//
//            String buffer = ite.next().toString();
//            if (buffer.startsWith("<tagOfATestCase>")) {
//
//                Field f = scenario.getClass().getDeclaredField("testCase");
//                f.setAccessible(true);
//                TestCase r = (TestCase) f.get(scenario);
//
//                List<PickleStepTestStep> testSteps = r.getTestSteps().stream().filter(x -> x instanceof PickleStepTestStep)
//                        .map(x -> (PickleStepTestStep) x).collect(Collectors.toList());
//
//                for (PickleStepTestStep ts : testSteps) {
//
//                    System.out.println(ts.getStepText());//will print your test case steps
//
//                }
//
//            }
//
//        }
    }

    @Given("complex scenario composed of {string}")
    public void complexScenarioComposedOf(String arg0) {
        scenarioNames = new ArrayList<>(Arrays.asList(arg0.split(" ")));
        System.out.println( "scenarioNames.size() : " + scenarioNames.size() );
    }

    @Then("execute a complex scenario")
    public void executeAComplexScenario() {
        System.out.println( "test executeAComplexScenario HERE" );
    }
}
