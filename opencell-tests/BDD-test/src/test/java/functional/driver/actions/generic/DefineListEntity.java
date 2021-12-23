package functional.driver.actions.generic;

import functional.driver.utils.JsonUtils;
import io.cucumber.datatable.DataTable;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.thucydides.core.annotations.Step;

import java.util.List;
import java.util.Map;

public class DefineListEntity implements Task {

    private final DataTable dataTable;

    public DefineListEntity(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public static DefineListEntity called(DataTable dataTable) {
        return Tasks.instrumented(DefineListEntity.class, dataTable);
    }

    @Override
    @Step("{0} define")
    public <T extends Actor> void performAs(T actor) {
        List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> anInstance : table) {
            JsonUtils.defineJson(anInstance, "", "", true);
        }
    }

}
