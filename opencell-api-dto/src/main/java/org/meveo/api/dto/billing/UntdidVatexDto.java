package org.meveo.api.dto.billing;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;


/**
 * DTO for {@link UntdidVatex}.
 * 
 * 
 **/
@XmlRootElement(name = "UntdidVatex")
@XmlAccessorType(XmlAccessType.FIELD)
public class UntdidVatexDto extends BusinessEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5184602572648722134L;

	private String codeName;

	private String remark;

	public UntdidVatexDto(String codeName, String remark) {
		super();
		this.codeName = codeName;
		this.remark = remark;
	}

	/**
	 * Instantiates a new UntdidVatex dto.
	 */
	public UntdidVatexDto() {

	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
