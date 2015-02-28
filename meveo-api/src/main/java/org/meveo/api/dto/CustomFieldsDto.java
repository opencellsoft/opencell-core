package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomFields")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldsDto implements Serializable {

	private static final long serialVersionUID = 7751924530575980282L;

	private List<CustomFieldDto> customField;

	public List<CustomFieldDto> getCustomField() {
		if (customField == null) {
			customField = new ArrayList<CustomFieldDto>();
		}

		return customField;
	}

	public void setCustomField(List<CustomFieldDto> customField) {
		this.customField = customField;
	}

	@Override
	public String toString() {
		return "CustomFieldsDto [customField=" + customField + "]";
	}

}
