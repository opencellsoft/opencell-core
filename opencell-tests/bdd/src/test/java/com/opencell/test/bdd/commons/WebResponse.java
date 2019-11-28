package com.opencell.test.bdd.commons;

import io.restassured.path.xml.XmlPath;

public class WebResponse {
    private XmlPath body ;
    private int httpStatusCode ;


    public WebResponse() {
    }


    public WebResponse(int httpStatusCode, XmlPath xmlPath) {
        this.httpStatusCode = httpStatusCode;
        this.body = xmlPath;
    }

    

    public WebResponse(int statusCode) {
        this.httpStatusCode = statusCode;
    }


    public XmlPath getBody() {
        return body;
    }


    public void setBody(XmlPath body) {
        this.body = body;
    }


    public int getHttpStatusCode() {
        return httpStatusCode;
    }


    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
    
    
}
