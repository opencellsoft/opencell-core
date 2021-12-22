package functional.stepDefs.generic;

import functional.driver.actions.generic.DefineEntity;
import functional.driver.actions.generic.DefineListEntity;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class DefineEntityStepDef {

    @Given("I define")
    public void actorDefineEntity(DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(DefineEntity.called(dataTable));
    }

    @Given("I define following list")
    public void actorDefineListEntity(DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(DefineListEntity.called(dataTable));
    }

}
