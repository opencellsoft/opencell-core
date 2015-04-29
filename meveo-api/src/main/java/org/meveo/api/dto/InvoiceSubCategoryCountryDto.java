package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSubcategoryCountry;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InvoiceSubCategoryCountry")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryCountryDto extends BaseDto {

	private static final long serialVersionUID = 7702371660532457108L;

	@XmlElement(required = true)
	private String invoiceSubCategory;

	@XmlElement(required = true)
	private String country;

	@XmlElement(required = true)
	private String tax;

	private String discountCode;
	

	private String filterEL;

	public InvoiceSubCategoryCountryDto() {

	}

	public InvoiceSubCategoryCountryDto(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		invoiceSubCategory = invoiceSubcategoryCountry.getInvoiceSubCategory().getCode();
		country = invoiceSubcategoryCountry.getTradingCountry().getCountryCode();
		tax = invoiceSubcategoryCountry.getTax().getCode();
		filterEL=invoiceSubcategoryCountry.getFilterEL();
	}

	public String getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(String invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}
	
	

	public String getFilterEL() {
		return filterEL;
	}

	public void setFilterEL(String filterEL) {
		this.filterEL = filterEL;
	}

	@Override
	public String toString() {
		return "InvoiceSubCategoryCountryDto [invoiceSubCategory=" + invoiceSubCategory + ", country=" + country
				+ ", tax=" + tax + ", discountCode=" + discountCode +",filterEL="+filterEL+ "]";
	}

}
