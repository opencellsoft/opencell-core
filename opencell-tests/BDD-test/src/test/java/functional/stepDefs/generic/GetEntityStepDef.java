package functional.stepDefs.generic;

import functional.driver.actions.generic.crud.GetEntityBasedOnCode;
import functional.driver.actions.generic.crud.GetEntityBasedOnDatatable;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class GetEntityStepDef {

    @Given("^([^ \"]*)$")
    public void actorGetEntityBasedOnDataTable(String entityName, DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(GetEntityBasedOnDatatable.called(entityName, dataTable));
    }



    @Given("^([^ \"]*) ([^ \"]*)$")
    public void actorUpdateEntityBasedOnCode(String entityName, String entityCodes) throws Exception {
        BasicConfig.getActor().attemptsTo(GetEntityBasedOnCode.called(entityName, entityCodes));
    }



}
