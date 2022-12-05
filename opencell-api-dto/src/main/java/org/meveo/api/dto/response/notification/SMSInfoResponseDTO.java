package org.meveo.api.dto.response.notification;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SMSInfoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMSInfoResponseDTO {

    private String body;
    private String sentTo;
    private String status;
    private String price;
    private String uri;
    private Integer errorCode;
    private String errorMessage;

    public String getStatus() {
        return status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getBody() {
        return body;
    }

    public String getUri() {
        return uri;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSentTo() {
        return sentTo;
    }

    public String getPrice() {
        return price;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}