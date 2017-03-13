package org.meveo.api.dto.response.utilities;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "FieldsNotImportedStringCollection")
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldsNotImportedStringCollectionDto {

	private Collection<String> fieldsNotImported;

	public FieldsNotImportedStringCollectionDto() {

	}

	public FieldsNotImportedStringCollectionDto(Collection<String> fieldsNotImported) {
		this.fieldsNotImported = fieldsNotImported;
	}

	public Collection<String> getFieldsNotImported() {
		return fieldsNotImported;
	}

	public void setFieldsNotImported(Collection<String> fieldsNotImported) {
		this.fieldsNotImported = fieldsNotImported;
	}

}