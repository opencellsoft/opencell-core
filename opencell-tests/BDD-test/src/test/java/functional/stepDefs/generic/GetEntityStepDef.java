package functional.stepDefs.generic;

import functional.driver.actions.generic.GetEntity;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class GetEntityStepDef {

    @Given("([^ \"]*) with (<[a-zA-Z]*>)*$")
    public void actorGetEntity(String entity, DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(GetEntity.called(entity, dataTable));
    }

}
