package org.meveo.api.dto;

import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * The Class CustomFieldFormattedValueDto.
 * 
 * @author Abdellatif BARI
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldFormattedValueDto extends BusinessEntityDto {

    /**
     * The auto generated serial no
     */
    private static final long serialVersionUID = 4706817670486265862L;

    /** The long, double or date value. */
    @XmlElement
    protected String singleValue;

    /** The list value. */
    @XmlElement
    protected List<String> listValue;

    /** The map value. */
    @XmlElement
    protected LinkedHashMap<String, String> mapValue;

    /**
     * Instantiates a new custom field dto.
     */
    public CustomFieldFormattedValueDto() {
    }

    /**
     * @return the singleValue
     */
    public String getSingleValue() {
        return singleValue;
    }

    /**
     * @param singleValue the singleValue to set
     */
    public void setSingleValue(String singleValue) {
        this.singleValue = singleValue;
    }

    /**
     * @return the listValue
     */
    public List<String> getListValue() {
        return listValue;
    }

    /**
     * @param listValue the listValue to set
     */
    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
    }

    /**
     * @return the mapValue
     */
    public LinkedHashMap<String, String> getMapValue() {
        return mapValue;
    }

    /**
     * @param mapValue the mapValue to set
     */
    public void setMapValue(LinkedHashMap<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomFieldFormattedValueDto [singleValue=" + singleValue + ", listValue=" + listValue + ", mapValue=" + mapValue + "]";
    }

}