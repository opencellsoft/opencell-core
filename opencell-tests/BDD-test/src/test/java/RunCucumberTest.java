//@RunWith(Cucumber.class)
//@CucumberOptions()
//public class RunCucumberTest {
//
//}

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(plugin = {"pretty"})
public class RunCucumberTest {

}
