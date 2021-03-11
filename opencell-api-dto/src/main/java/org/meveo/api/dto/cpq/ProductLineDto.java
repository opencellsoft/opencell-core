package org.meveo.api.dto.cpq;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.ProductLine;

/**
 * 
 * @author Tarik F.
 * @version 10.0
 */
@XmlRootElement(name = "ProductLineDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductLineDto extends BusinessEntityDto{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7359760632077227886L;
	private String sellerCode;
	private String longDescription;
	private String parentLineCode;
    private CustomFieldsDto customFields;
	
	
	public ProductLineDto(String codeProductLine, String description, String sellerCode, String longDescription,
			String parentLineCode) {
		this.code = codeProductLine;
		this.description = description;
		this.sellerCode = sellerCode;
		this.longDescription = longDescription;
		this.parentLineCode = parentLineCode;
	}
	
	public ProductLineDto(ProductLine p) {
		if(p!=null) {
			this.code = p.getCode();
			this.description = p.getDescription();
			this.sellerCode = p.getSeller() != null ? p.getSeller().getCode() : null;
			this.longDescription = p.getLongDescription();
			this.parentLineCode = p.getParentLine() != null ? p.getParentLine().getCode() : null;
		}
	
	}
	public ProductLineDto() {
	}
 
	
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	 

	/**
	 * @return the sellerCode
	 */
	public String getSellerCode() {
		return sellerCode;
	}

	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}
	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	
	/**
	 * @return the parentLineCode
	 */
	public String getParentLineCode() {
		return parentLineCode;
	}

	/**
	 * @param parentLineCode the parentLineCode to set
	 */
	public void setParentLineCode(String parentLineCode) {
		this.parentLineCode = parentLineCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, sellerCode, parentLineCode, description, longDescription);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductLineDto other = (ProductLineDto) obj;
		return Objects.equals(code, other.code) && Objects.equals(sellerCode, other.sellerCode)
				&& Objects.equals(parentLineCode, other.parentLineCode) && Objects.equals(description, other.description)
				&& Objects.equals(longDescription, other.longDescription);
	}
 
	@Override
	public String toString() {
		return "ProductLineDto [code=" + code + ", label=" + description + ", sellerCode=" + sellerCode + ", longDescription="
				+ longDescription + ", parentLineCode=" + parentLineCode + "]";
	}

	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	
	
	
	
	
}
