package org.meveo.api.dto.custom;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

@XmlRootElement(name = "CustomTableData")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitaryCustomTableDataDto extends BaseEntityDto {

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
    @XmlAttribute(name = "record", required = true)
    private CustomTableRecordDto value;

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
        return Optional.ofNullable(overrwrite).orElse(false);
    }

    /**
     * @param overrwrite Should data be overwritten (deleted all data first) instead of appended to existing values.
     */
    public void setOverrwrite(Boolean overrwrite) {
        this.overrwrite = overrwrite;
    }

    /**
     * @return value with field name as map's key and field value as map's value
     */
    public CustomTableRecordDto getValue() {
        return value;
    }

    public LinkedHashMap<String, Object> getRowValues() {
        return Optional.ofNullable(value).map(CustomTableRecordDto::getValues).orElse(new LinkedHashMap<>());
    }

    /**
     * @param value with field name as map's key and field value as map's value
     */
    public void setValue(CustomTableRecordDto value) {
        this.value = value;
    }

}
