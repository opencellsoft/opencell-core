package functional.stepDefs.generic;

import functional.driver.actions.generic.crud.UpdateEntityBasedOnDatatable;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class UpdateEntityStepDef {

    @Given("^I update ([^ \"]*)$")
    public void actorUpdateEntityInline(String entity, DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(UpdateEntityBasedOnDatatable.called(entity, dataTable));
    }

}
