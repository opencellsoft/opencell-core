package org.meveo.api.dto.custom;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents data in custom table - custom entity data stored in a separate table
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomTableRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTableRecordDto implements Serializable {

    private static final long serialVersionUID = -1209601309024979418L;

    private LinkedHashMap<String, Object> values;

    public CustomTableRecordDto() {

    }

    public CustomTableRecordDto(Map<String, Object> values) {
        this.values = (LinkedHashMap<String, Object>) values;
    }

    /**
     * @return A list of values with field name as map's key and field value as map's value
     */
    public LinkedHashMap<String, Object> getValues() {
        return values;
    }

    /**
     * @param values A list of values with field name as map's key and field value as map's value
     */
    public void setValues(LinkedHashMap<String, Object> values) {
        this.values = values;
    }
}