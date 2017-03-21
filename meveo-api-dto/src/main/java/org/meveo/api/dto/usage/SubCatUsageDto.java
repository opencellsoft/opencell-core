package org.meveo.api.dto.usage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "SubCatUsage")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubCatUsageDto extends BaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String description;

	@XmlElementWrapper
    @XmlElement(name="usage")
	List<UsageDto> listUsage = new ArrayList<UsageDto>();

	public SubCatUsageDto() {
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the listUsage
	 */
	public List<UsageDto> getListUsage() {
		return listUsage;
	}

	/**
	 * @param listUsage
	 *            the listUsage to set
	 */
	public void setListUsage(List<UsageDto> listUsage) {
		this.listUsage = listUsage;
	}

	@Override
	public String toString() {
		return "SubCatUsageDto [code=" + code + ", description=" + description + ", listUsage=" + listUsage + "]";
	}

}
