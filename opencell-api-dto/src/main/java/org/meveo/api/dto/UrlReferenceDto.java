package org.meveo.api.dto;

import org.meveo.model.crm.custom.UrlReferenceWrapper;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class UrlReferenceDto {

    @XmlAttribute(required = true)
    protected String url;

    @XmlAttribute
    protected String regexp;


    @XmlAttribute
    protected String label;

    @XmlAttribute
    protected Integer length;

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

    @Override
    public String toString() {
        return "UrlReferenceDto[" +
                "url='" + url + '\'' +
                ", regexp='" + regexp + '\'' +
                ", label='" + label + '\'' +
                ", length=" + length +
                ']';
    }

    public boolean isEmpty() {
        return url== null || url.isEmpty();
    }

    /**
     * to Wrapper.
     *
     * @return the url reference wrapper
     */
    public UrlReferenceWrapper toWrapper() {
        return new UrlReferenceWrapper(url, regexp, label, length);
    }
}
