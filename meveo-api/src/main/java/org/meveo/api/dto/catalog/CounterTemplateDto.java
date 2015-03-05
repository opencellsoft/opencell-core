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

	private String unity;
	private int type;
	private BigDecimal level;
	private boolean disabled;
	private String calendar;

	public CounterTemplateDto() {

	}

	public CounterTemplateDto(CounterTemplate e) {
		code = e.getCode();
		description = e.getDescription();
		unity = e.getUnityDescription();
		type = e.getCounterType().getId();
		level = e.getLevel();
		disabled = e.isDisabled();
		calendar = e.getCalendar().getCode();
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BigDecimal getLevel() {
		return level;
	}

	public void setLevel(BigDecimal level) {
		this.level = level;
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

	@Override
	public String toString() {
		return "CounterTemplateDto [code=" + code + ", description=" + description + ", unity=" + unity + ", type="
				+ type + ", level=" + level + ", disabled=" + disabled + ", calendar=" + calendar + "]";
	}

}
