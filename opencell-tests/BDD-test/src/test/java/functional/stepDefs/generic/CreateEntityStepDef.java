package functional.stepDefs.generic;

import functional.driver.actions.generic.CreateEntityInline;
import functional.driver.actions.generic.CreateEntityWithFile;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;

public class CreateEntityStepDef {

    @Given("^I create entity \"([^\"]*)\" from dto \"([^\"]*)\"$")
    public void actorCreateEntityFromDto(String entity, String entityDto) {
        BasicConfig.getActor().attemptsTo(CreateEntityWithFile.called(entity, entityDto, BasicConfig.getFeaturePath()));
    }

    @Given("^I create entity from feature \"[^\"]*\"")
    public void actorCreateEntityFromFeature(String featurePath) {
    }

//    @Given("^I create \"([^\"]*)\"$")
    @Given("^I create ([^ \"]*)$")
    public void actorCreateEntityInline(String entity, DataTable dataTable) throws Exception {
        BasicConfig.getActor().attemptsTo(CreateEntityInline.called(entity, dataTable));
    }
}
