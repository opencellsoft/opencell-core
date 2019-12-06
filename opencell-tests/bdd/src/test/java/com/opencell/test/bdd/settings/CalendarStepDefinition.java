package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetCalendarResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CalendarStepDefinition implements En {
    public CalendarStepDefinition(BaseHook base) {
        Then("^The calendar is created$", () -> {
            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/calendar?calendarCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetCalendarResponse actualEntity = response.extract().body().as(GetCalendarResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getCalendar());
                    assertEquals(code, actualEntity.getCalendar().getCode());
                }
            });
        });
    }
}
