package functional.driver.actions.generic;

import functional.driver.utils.ApiUtils;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.Map;

public class DefineEntity implements Task {

    private final DataTable dataTable;

    public DefineEntity(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public static DefineEntity called(DataTable dataTable) {
        return Tasks.instrumented(DefineEntity.class, dataTable);
    }

    @Override
    @Step("{0} define")
    public <T extends Actor> void performAs(T actor) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> anInstance : table) {
            ApiUtils.createJson(anInstance, false);
        }
    }

}
