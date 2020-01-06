package com.opencell.test.bdd.commons;

import org.meveo.api.dto.ActionStatus;

import io.restassured.path.json.JsonPath;


public class ApiResponse {

    private ActionStatus actionStatus ;
    private int httpStatusCode ;
    private JsonPath jsonResponse;

    public ApiResponse() {
    }


    public ApiResponse(int httpStatusCode, ActionStatus actionStatus) {
        this.httpStatusCode = httpStatusCode;
        this.actionStatus = actionStatus;
    }
    

    public ApiResponse(int httpStatusCode, ActionStatus actionStatus, JsonPath jsonResponse) {
        this.httpStatusCode = httpStatusCode;
        this.actionStatus = actionStatus;
        this.jsonResponse = jsonResponse;
    }
    

    public ActionStatus getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }


    public JsonPath getJsonResponse() {
        return jsonResponse;
    }


    public void setJsonResponse(JsonPath jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    
}
