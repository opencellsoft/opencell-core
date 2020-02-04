package com.opencell.test.bdd.settings;

import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.utils.RestApiUtils;
import cucumber.api.java8.En;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.meveo.api.dto.response.GetRoleResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RoleStepDefinition implements En {
    public RoleStepDefinition(BaseHook base) {
        Then("^The role is created$", () -> {
            base.getField("name").ifPresent( code ->{
                if(base.getResponse().getHttpStatusCode() == HttpStatus.SC_OK) {
                    ValidatableResponse response = RestApiUtils.get("/role?roleName=" + code, "");
                    response.assertThat().statusCode(HttpStatus.SC_OK);
                    GetRoleResponse actualEntity = response.extract().body().as(GetRoleResponse.class);
                    assertNotNull(actualEntity);
                    assertNotNull(actualEntity.getRoleDto());
                    assertEquals(code, actualEntity.getRoleDto().getName());
                }
            });
        });
    }
}
