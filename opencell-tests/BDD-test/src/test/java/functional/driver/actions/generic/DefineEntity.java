package functional.driver.actions.generic;

import functional.driver.utils.JsonUtils;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.Map;

public class DefineEntity implements Task {

    private final String dataFields;

    private final String purpose;

    private final DataTable dataTable;

    public DefineEntity(String dataFields, String purpose, DataTable dataTable) {
        this.dataFields = dataFields;
        this.purpose = purpose;
        this.dataTable = dataTable;
    }

    public static DefineEntity called(String dataField, String intention, DataTable dataTable) {
        return Tasks.instrumented(DefineEntity.class, dataField, intention, dataTable);
    }

    @Override
    @Step("{0} define")
    public <T extends Actor> void performAs(T actor) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> anInstance : table) {
            JsonUtils.defineJson(anInstance, dataFields, purpose, false);
        }
    }

}
