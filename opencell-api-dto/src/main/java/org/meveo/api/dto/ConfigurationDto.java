package org.meveo.api.dto;

public class ConfigurationDto extends BaseEntityDto {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String property;
    private String value;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
