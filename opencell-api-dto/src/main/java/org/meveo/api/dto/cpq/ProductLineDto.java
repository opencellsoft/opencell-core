package org.meveo.api.dto.cpq;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.ProductLine;

/**
 * 
 * @author Tarik F.
 * @version 10.0
 */
@XmlRootElement(name = "ProductLineDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductLineDto extends BaseEntityDto{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7359760632077227886L;
	private Long id;
	private String code;
	private String label;
	private String sellerCode;
	private String longDescription;
	private String parentLineCode;
	
	
	public ProductLineDto(String codeProductLine, String label, String sellerCode, String longDescription,
			String parentLineCode) {
		this.code = codeProductLine;
		this.label = label;
		this.sellerCode = sellerCode;
		this.longDescription = longDescription;
		this.parentLineCode = parentLineCode;
	}
	
	public ProductLineDto(ProductLine p) {
		this.id = p.getId();
		this.code = p.getCode();
		this.label = p.getDescription();
		this.sellerCode = p.getSeller() != null ? p.getSeller().getCode() : null;
		this.longDescription = p.getLongDescription();
		this.parentLineCode = p.getParentLine() != null ? p.getParentLine().getCode() : null;
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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
		return Objects.hash(code, sellerCode, parentLineCode, label, longDescription);
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
				&& Objects.equals(parentLineCode, other.parentLineCode) && Objects.equals(label, other.label)
				&& Objects.equals(longDescription, other.longDescription);
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	
}
