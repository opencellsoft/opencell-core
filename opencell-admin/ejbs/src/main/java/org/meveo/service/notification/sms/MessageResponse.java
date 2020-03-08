package org.meveo.service.notification.sms;

public class MessageResponse {

    private String body;
    private String sentTo;
    private String status;
    private String price;
    private String uri;
    private String sid;
    private Integer errorCode;
    private String errorMessage;

    public MessageResponse(Builder builder) {
        this.uri = builder.uri;
        this.sentTo = builder.sentTo;
        this.body = builder.body;
        this.price = builder.price;
        this.sid = builder.sid;
        this.status = builder.status;
        this.errorCode = builder.errorCode;
        this.errorMessage = builder.errorMessage;
    }

    public String getBody() {
        return body;
    }

    public String getSentTo() {
        return sentTo;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getUri() {
        return uri;
    }

    public String getSid() {
        return sid;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class Builder {

        private String body;
        private String sentTo;
        private String status;
        private String price;
        private String uri;
        private String sid;
        private Integer errorCode;
        private String errorMessage;

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder withSentTo(String sentTo) {
            this.sentTo = sentTo;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withSid(String sid) {
            this.sid = sid;
            return this;
        }

        public Builder withErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public MessageResponse build() {
            return new MessageResponse(this);
        }
    }
}