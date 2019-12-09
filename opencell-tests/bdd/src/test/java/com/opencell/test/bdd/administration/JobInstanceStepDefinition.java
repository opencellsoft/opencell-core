package com.opencell.test.bdd.administration;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class JobInstanceStepDefinition implements En {

    public JobInstanceStepDefinition(BaseHook base) {
        Then("^The jobInstance is created$", () -> {

            base.getCode().ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/jobInstance?jobInstanceCode=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    JobInstanceResponseDto actualEntity = response.extract().body().as(JobInstanceResponseDto.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getJobInstanceDto());
                    assertEquals(code, actualEntity.getJobInstanceDto().getCode());
                }
            });


        });
    }
}
