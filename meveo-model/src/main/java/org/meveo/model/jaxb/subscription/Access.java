package org.meveo.model.jaxb.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meveo.model.shared.DateUtils;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}starDate"/>
 *         &lt;element ref="{}endDate"/>
 *         &lt;element ref="{}accessUserId"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "startDate", "endDate", "accessUserId" })
@XmlRootElement(name = "access")
public class Access {

	@XmlElement(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NMTOKEN")
	protected String startDate;
	@XmlElement(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NMTOKEN")
	protected String endDate;
	@XmlElement(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NCName")
	protected String accessUserId;

	public Access(){}
	
	public Access(org.meveo.model.mediation.Access access,String dateFormat) {
		startDate=access.getStartDate()==null?null:
    		DateUtils.formatDateWithPattern(access.getStartDate(), dateFormat);
		endDate=access.getEndDate()==null?null:
    		DateUtils.formatDateWithPattern(access.getEndDate(), dateFormat);
		accessUserId=access.getAccessUserId();
	}

	/**
	 * Gets the value of the startDate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Sets the value of the startDate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStartDate(String value) {
		this.startDate = value;
	}

	/**
	 * Gets the value of the endDate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Sets the value of the endDate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEndDate(String value) {
		this.endDate = value;
	}

	/**
	 * Gets the value of the accessUserId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAccessUserId() {
		return accessUserId;
	}

	/**
	 * Sets the value of the accessUserId property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAccessUserId(String value) {
		this.accessUserId = value;
	}

}
