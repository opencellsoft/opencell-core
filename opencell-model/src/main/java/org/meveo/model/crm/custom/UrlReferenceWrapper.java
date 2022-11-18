package org.meveo.model.crm.custom;

import java.io.Serializable;
import java.net.URL;

public class UrlReferenceWrapper implements Serializable {

    private String url;

    private String regexp;


    private String label;

    private Integer length;

    public UrlReferenceWrapper(String url) {
        this.url = url;
    }

    public UrlReferenceWrapper() {
    }

    public UrlReferenceWrapper(String url, String regexp, String label, Integer length) {
        this.url = url;
        this.regexp = regexp;
        this.label = label;
        this.length = length;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public  boolean containsValidURL() {
    try {
        new URL(url);
        return true;
    } catch (Exception e) {
        return false;
    }
}

    public boolean isEmpty() {
        return url == null || url.isEmpty();
    }
}
