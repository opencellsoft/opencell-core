package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSubcategoryCountry;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InvoiceSubCategoryCountry")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryCountryDto extends BaseDto {

	private static final long serialVersionUID = 7702371660532457108L;

	@XmlAttribute(required = true)
	private String invoiceSubCategory;

	@XmlAttribute(required = true)
	private String country;

	@XmlAttribute(required = true)
	private String tax;

	private String discountCode;

	public InvoiceSubCategoryCountryDto() {

	}

	public InvoiceSubCategoryCountryDto(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		invoiceSubCategory = invoiceSubcategoryCountry.getInvoiceSubCategory().getCode();
		country = invoiceSubcategoryCountry.getTradingCountry().getCountryCode();
		tax = invoiceSubcategoryCountry.getTax().getCode();
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

	@Override
	public String toString() {
		return "InvoiceSubCategoryCountryDto [invoiceSubCategory=" + invoiceSubCategory + ", country=" + country
				+ ", tax=" + tax + ", discountCode=" + discountCode + "]";
	}

}
