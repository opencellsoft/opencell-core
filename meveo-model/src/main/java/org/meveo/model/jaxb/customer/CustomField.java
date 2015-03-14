package org.meveo.model.jaxb.customer;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldInstance;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customField")
public class CustomField {
    
	@XmlAttribute(required=true)
    protected String code;
    
    @XmlAttribute
    protected String description;

    @XmlAttribute
    protected String stringValue;

    @XmlAttribute
    protected Date dateValue;

    @XmlAttribute
    protected Long longValue;

    @XmlAttribute
    protected Double doubleValue;

    public CustomField(){}
    
	public CustomField(CustomFieldInstance cfi) {
		if(cfi!=null){
			code=cfi.getCode();
			description=cfi.getDescription();
			stringValue=cfi.getStringValue();
			dateValue=cfi.getDateValue();
			longValue=cfi.getLongValue();
			doubleValue=cfi.getDoubleValue();
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

}
