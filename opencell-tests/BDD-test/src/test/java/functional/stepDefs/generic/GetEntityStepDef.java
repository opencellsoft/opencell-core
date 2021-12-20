package functional.stepDefs.generic;

import functional.driver.actions.generic.GetEntity;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class GetEntityStepDef {

    @Given("^([^ \"]*)$")
    public void actorGetEntity(String entityName, DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(GetEntity.called(entityName, dataTable));
    }

}
