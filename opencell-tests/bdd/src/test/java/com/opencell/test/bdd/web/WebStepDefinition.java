package com.opencell.test.bdd.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.opencell.test.bdd.commons.ApiResponse;
import com.opencell.test.bdd.commons.BaseHook;
import com.opencell.test.bdd.commons.WebResponse;
import com.opencell.test.utils.WebUtils;

import cucumber.api.java8.En;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.ValidatableResponse;

public class WebStepDefinition implements En {

    private BaseHook base;
    
    public WebStepDefinition(BaseHook base) {
        this.base = base;
        When("^I go to this \"([^\"]*)\"$", (String url) -> {
            ValidatableResponse response = WebUtils.get(url);
            base.setWebResponse(new WebResponse(response.extract().statusCode(), response.extract().htmlPath()));
            base.setResponse(new ApiResponse(base.getWebResponse().getHttpStatusCode(), null));
            assertNotNull(base.getResponse());
            assertNotNull(base.getResponse().getHttpStatusCode());
        });
        Then("^I should be on \"([^\"]*)\"$", (String pageName) -> {
            assertEquals(pageName, base.getWebResponse().getBody().get("html.head.title").toString());
        });
    }

    public BaseHook getBase() {
        return base;
    }

    public void setBase(BaseHook base) {
        this.base = base;
    }
    
    
}
