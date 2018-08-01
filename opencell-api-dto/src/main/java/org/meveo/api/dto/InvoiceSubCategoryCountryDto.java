package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceSubcategoryCountry;

/**
 * The Class InvoiceSubCategoryCountryDto.
 */
@XmlRootElement(name = "InvoiceSubCategoryCountry")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoryCountryDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7702371660532457108L;

    /** The invoice sub category. */
    @XmlElement(required = true)
    private String invoiceSubCategory;

    /** The selling country. */
    private String sellingCountry;

    /** The country. */
    private String country;

    /** The tax. */
    private String tax;

    /** The tax code EL. */
    private String taxCodeEL;

    /** The discount code. */
    private String discountCode;

    /** The filter EL. */
    private String filterEL;

    /** The start validity date. */
    private Date startValidityDate;
    
    /** The end validity date. */
    private Date endValidityDate;
    
    /** The priority. */
    private int priority;

    /**
     * Instantiates a new invoice sub category country dto.
     */
    public InvoiceSubCategoryCountryDto() {

    }

    /**
     * Instantiates a new invoice sub category country dto.
     *
     * @param invoiceSubcategoryCountry the invoice subcategory country
     */
    public InvoiceSubCategoryCountryDto(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
        super(invoiceSubcategoryCountry);
        invoiceSubCategory = invoiceSubcategoryCountry.getInvoiceSubCategory().getCode();
        sellingCountry = invoiceSubcategoryCountry.getSellingCountry() == null ? null : invoiceSubcategoryCountry.getSellingCountry().getCountryCode();
        country = invoiceSubcategoryCountry.getTradingCountry() == null ? null : invoiceSubcategoryCountry.getTradingCountry().getCountryCode();
        tax = invoiceSubcategoryCountry.getTax() == null ? null : invoiceSubcategoryCountry.getTax().getCode();
        taxCodeEL = invoiceSubcategoryCountry.getTaxCodeEL();
        filterEL = invoiceSubcategoryCountry.getFilterEL();
        startValidityDate = invoiceSubcategoryCountry.getStartValidityDate();
        endValidityDate = invoiceSubcategoryCountry.getEndValidityDate();
        priority = invoiceSubcategoryCountry.getPriority();
    }

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public String getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(String invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /**
     * Gets the selling country.
     *
     * @return the selling country
     */
    public String getSellingCountry() {
        return sellingCountry;
    }

    /**
     * Sets the selling country.
     *
     * @param sellingCountry the new selling country
     */
    public void setSellingCountry(String sellingCountry) {
        this.sellingCountry = sellingCountry;
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the tax.
     *
     * @return the tax
     */
    public String getTax() {
        return tax;
    }

    /**
     * Sets the tax.
     *
     * @param tax the new tax
     */
    public void setTax(String tax) {
        this.tax = tax;
    }

    /**
     * Gets the tax code EL.
     *
     * @return the tax code EL
     */
    public String getTaxCodeEL() {
        return taxCodeEL;
    }

    /**
     * Sets the tax code EL.
     *
     * @param taxCodeEL the new tax code EL
     */
    public void setTaxCodeEL(String taxCodeEL) {
        this.taxCodeEL = taxCodeEL;
    }

    /**
     * Gets the discount code.
     *
     * @return the discount code
     */
    public String getDiscountCode() {
        return discountCode;
    }

    /**
     * Sets the discount code.
     *
     * @param discountCode the new discount code
     */
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    /**
     * Gets the filter EL.
     *
     * @return the filter EL
     */
    public String getFilterEL() {
        return filterEL;
    }

    /**
     * Sets the filter EL.
     *
     * @param filterEL the new filter EL
     */
    public void setFilterEL(String filterEL) {
        this.filterEL = filterEL;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceSubCategoryCountryDto [invoiceSubCategory=" + invoiceSubCategory + ", selling country=" + sellingCountry + ", country=" + country + ", tax=" + tax
                + ", taxCodeEL=" + taxCodeEL + ", discountCode=" + discountCode + ",filterEL=" + filterEL + "]";
    }

    /**
     * Gets the start validity date.
     *
     * @return the start validity date
     */
    public Date getStartValidityDate() {
        return startValidityDate;
    }

    /**
     * Sets the start validity date.
     *
     * @param startValidityDate the new start validity date
     */
    public void setStartValidityDate(Date startValidityDate) {
        this.startValidityDate = startValidityDate;
    }

    /**
     * Gets the end validity date.
     *
     * @return the end validity date
     */
    public Date getEndValidityDate() {
        return endValidityDate;
    }

    /**
     * Sets the end validity date.
     *
     * @param endValidityDate the new end validity date
     */
    public void setEndValidityDate(Date endValidityDate) {
        this.endValidityDate = endValidityDate;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

}
