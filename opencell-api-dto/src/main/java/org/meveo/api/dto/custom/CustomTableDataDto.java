package org.meveo.api.dto.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * Represents data in custom table - custom entity data stored in a separate table
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomTableData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTableDataDto extends BaseEntityDto {

    private static final long serialVersionUID = -1209601309024979414L;

    /**
     * Custom table/custom entity template code
     */
    @XmlAttribute(required = true)
    private String customTableCode;

    /**
     * Should data be overwritten (deleted all data first) instead of appended to existing values. Defaults to false if omitted.
     */
    @XmlAttribute
    private Boolean overrwrite;

    /**
     * A list of values with field name as map's key and field value as map's value
     */
    @XmlElementWrapper(name = "records")
    @XmlElement(name = "record")
    private List<CustomTableRecordDto> values;

    /**
     * @return Custom table/custom entity template code
     */
    public String getCustomTableCode() {
        return customTableCode;
    }

    /**
     * @param customTableCode Custom table/custom entity template code
     */
    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    /**
     * @return Should data be overwritten (deleted all data first) instead of appended to existing values. Defaults to false if null.
     */
    public Boolean getOverrwrite() {
        return overrwrite;
    }

    /**
     * @param overrwrite Should data be overwritten (deleted all data first) instead of appended to existing values.
     */
    public void setOverrwrite(Boolean overrwrite) {
        this.overrwrite = overrwrite;
    }

    /**
     * @return A list of values with field name as map's key and field value as map's value
     */
    public List<CustomTableRecordDto> getValues() {
        return values;
    }

    /**
     * @param values A list of values with field name as map's key and field value as map's value
     */
    public void setValues(List<CustomTableRecordDto> values) {
        this.values = values;
    }

    public void setValuesFromListofMap(List<Map<String, Object>> list) {

        if (list == null) {
            return;

        }
        values = new ArrayList<>();

        for (Map<String, Object> item : list) {
            values.add(new CustomTableRecordDto(item));
        }
    }
}