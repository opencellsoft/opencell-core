package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.CounterTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CounterTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterTemplateDto implements Serializable {

	private static final long serialVersionUID = 2587489734648000805L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;
    
	@XmlAttribute(required = true)
	private String calendar;
	
	private String unity;
	private String type;
	private BigDecimal ceiling;
	private boolean disabled;
	private String counterLevel;
	

	public CounterTemplateDto() {
	}

	public CounterTemplateDto(CounterTemplate e) {
		code = e.getCode();
		description = e.getDescription();
		unity = e.getUnityDescription();
		type = e.getCounterType().getLabel();
		ceiling = e.getCeiling();
		disabled = e.isDisabled();
		calendar = e.getCalendar().getCode();
		counterLevel=String.valueOf(e.getCounterLevel());
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

	public String getUnity() {
		return unity;
	}

	public void setUnity(String unity) {
		this.unity = unity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getCeiling() {
		return ceiling;
	}

	public void setCeiling(BigDecimal ceiling) {
		this.ceiling = ceiling;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
	
	public String getCounterLevel() {
		return counterLevel;
	}

	public void setCounterLevel(String counterLevel) {
		this.counterLevel = counterLevel;
	}

	@Override
	public String toString() {
		return "CounterTemplateDto [code=" + code + ", description=" + description + ", unity=" + unity + ", type="
				+ type + ", ceiling=" + ceiling + ", counterLevel=" + counterLevel + ", disabled=" + disabled + ", calendar=" + calendar + "]";
	}

}
