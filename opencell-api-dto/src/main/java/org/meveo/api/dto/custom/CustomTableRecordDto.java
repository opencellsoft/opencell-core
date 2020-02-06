package org.meveo.api.dto.custom;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;

/**
 * Represents data in custom table - custom entity data stored in a separate table
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomTableRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTableRecordDto implements Serializable {

    private static final long serialVersionUID = -1209601309024979418L;
    private static final String ID_KEY = "id";
    
    private Long id;

    private LinkedHashMap<String, Object> values;
    
    private String tableName;
    
    private String display;

    public CustomTableRecordDto() {

    }
    
    public CustomTableRecordDto(Map<String, Object> values, String tableName) {
    	this(values);
    	this.tableName=tableName;
    }
    
    public CustomTableRecordDto(String display, Map<String, Object> values) {
    	this(values);
    	this.display=display;
    }

    public CustomTableRecordDto(Map<String, Object> values) {
        this.values = new LinkedHashMap<>(values);
        this.id = Long.valueOf(values.getOrDefault(ID_KEY, 0L).toString());
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
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
	public String display() {
		if(display!=null) {
			return display;
		}
		String tName = StringUtils.isBlank(tableName) ? "" : tableName + " ";
		return tName + values.toString();
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}