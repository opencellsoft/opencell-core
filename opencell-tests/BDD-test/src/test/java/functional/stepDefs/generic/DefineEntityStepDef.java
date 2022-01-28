package functional.stepDefs.generic;

import functional.driver.actions.generic.DefineEntity;
import functional.driver.actions.generic.DefineListEntity;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class DefineEntityStepDef {

    @Given("^I define( ([^\"]*))* to ([^\"]*)$")
    public void actorDefineEntity(String dataFields, String purpose, DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(DefineEntity.called(dataFields, purpose, dataTable));
    }

    @Given("I define following list")
    public void actorDefineListEntity(DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(DefineListEntity.called(dataTable));
    }

}
