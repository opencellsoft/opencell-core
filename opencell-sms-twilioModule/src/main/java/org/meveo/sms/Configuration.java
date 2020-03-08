package org.meveo.sms;

public class Configuration {

    private String accountSid;
    private String from;
    private String token;

    public Configuration(String accountSid, String from, String token) {
        this.accountSid = accountSid;
        this.from = from;
        this.token = token;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getFrom() {
        return from;
    }

    public String getToken() {
        return token;
    }
}