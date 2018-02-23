package org.meveo.model.notification;

/**
 * @author Edward P. Legaspi
 * @version 5 Jan 2018
 **/
public enum HttpProtocol {
    HTTPS("HTTPS"), HTTP("HTTP");

    private String label;

    private HttpProtocol(String label) {
        this.label = label;
    }

    public String getLabel() {
        return "HttpProtocol." + label;
    }
}
