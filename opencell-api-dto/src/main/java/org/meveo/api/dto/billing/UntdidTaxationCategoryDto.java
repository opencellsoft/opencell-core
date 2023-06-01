package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;


/**
 * DTO for {@link UntdidTaxationCategory}.
 * 
 * 
 **/
@XmlRootElement(name = "UntdidTaxationCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class UntdidTaxationCategoryDto extends BusinessEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5184602572648722134L;

	private String name;
	private String semanticModel;
	
	  
	public UntdidTaxationCategoryDto() {
		super();
	}
	public UntdidTaxationCategoryDto(String name, String semanticModel) {
		super();
		this.name = name;
		this.semanticModel = semanticModel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSemanticModel() {
		return semanticModel;
	}
	public void setSemanticModel(String semanticModel) {
		this.semanticModel = semanticModel;
	}

	
}
