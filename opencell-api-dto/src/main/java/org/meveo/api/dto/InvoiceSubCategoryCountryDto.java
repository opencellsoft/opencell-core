package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSubcategoryCountry;

@XmlRootElement(name = "InvoiceSubCategoryCountry")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryCountryDto extends BaseDto {

	private static final long serialVersionUID = 7702371660532457108L;

	@XmlElement(required = true)
	private String invoiceSubCategory;

	private String sellingCountry;
	
	private String country;

	private String tax;

	private String taxCodeEL;
	
	private String discountCode;
	
	private String filterEL;
	
	private Date startValidityDate;
	private Date endValidityDate;
	private int priority;

	public InvoiceSubCategoryCountryDto() {

	}

	public InvoiceSubCategoryCountryDto(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		invoiceSubCategory = invoiceSubcategoryCountry.getInvoiceSubCategory().getCode();
		sellingCountry = invoiceSubcategoryCountry.getSellingCountry()==null?null:invoiceSubcategoryCountry.getSellingCountry().getCountryCode();
		country = invoiceSubcategoryCountry.getTradingCountry()==null?null:invoiceSubcategoryCountry.getTradingCountry().getCountryCode();
		tax = invoiceSubcategoryCountry.getTax()==null?null:invoiceSubcategoryCountry.getTax().getCode();
		taxCodeEL = invoiceSubcategoryCountry.getTaxCodeEL();
		filterEL=invoiceSubcategoryCountry.getFilterEL();
		startValidityDate = invoiceSubcategoryCountry.getStartValidityDate();
		endValidityDate = invoiceSubcategoryCountry.getEndValidityDate();
		priority = invoiceSubcategoryCountry.getPriority();
	}

	public String getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(String invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public String getSellingCountry() {
		return sellingCountry;
	}

	public void setSellingCountry(String sellingCountry) {
		this.sellingCountry = sellingCountry;
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

	public String getTaxCodeEL() {
		return taxCodeEL;
	}

	public void setTaxCodeEL(String taxCodeEL) {
		this.taxCodeEL = taxCodeEL;
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
		return "InvoiceSubCategoryCountryDto [invoiceSubCategory=" + invoiceSubCategory 
				+ ", selling country=" + sellingCountry + ", country=" + country
				+ ", tax=" + tax + ", taxCodeEL=" + taxCodeEL + ", discountCode=" + discountCode +",filterEL="+filterEL+ "]";
	}

	public Date getStartValidityDate() {
		return startValidityDate;
	}

	public void setStartValidityDate(Date startValidityDate) {
		this.startValidityDate = startValidityDate;
	}

	public Date getEndValidityDate() {
		return endValidityDate;
	}

	public void setEndValidityDate(Date endValidityDate) {
		this.endValidityDate = endValidityDate;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
