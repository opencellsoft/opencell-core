package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.crm.CustomerCategory;

/**
 * The Class CustomerCategoryDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CustomerCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCategoryDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8778571285967620018L;

    /** The exonerated from taxes. */
    private Boolean exoneratedFromTaxes;

    /** The exoneration tax el. */
    private String exonerationTaxEl;

    /** The exoneration tax el for Spark. */
    private String exonerationTaxElSpark;

    /** The exoneration reason. */
    private String exonerationReason;

    /**
     * Instantiates a new customer category dto.
     */
    public CustomerCategoryDto() {

    }

    /**
     * Instantiates a new customer category dto.
     *
     * @param customerCategory the customerCategory entity
     */
    public CustomerCategoryDto(CustomerCategory customerCategory) {
        super(customerCategory);
        exoneratedFromTaxes = customerCategory.getExoneratedFromTaxes();
        exonerationReason = customerCategory.getExonerationReason();
        exonerationTaxEl = customerCategory.getExonerationTaxEl();
        exonerationTaxElSpark = customerCategory.getExonerationTaxElSpark();
    }

    /**
     * @return True if account is exonerated from taxes
     */
    public Boolean isExoneratedFromTaxes() {
        return exoneratedFromTaxes;
    }

    /**
     * @param exoneratedFromTaxes True if account is exonerated from taxes
     */
    public void setExoneratedFromTaxes(Boolean exoneratedFromTaxes) {
        this.exoneratedFromTaxes = exoneratedFromTaxes;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes
     */
    public String getExonerationTaxEl() {
        return exonerationTaxEl;
    }

    /**
     * @param exonerationTaxEl Expression to determine if account is exonerated from taxes
     */
    public void setExonerationTaxEl(String exonerationTaxEl) {
        this.exonerationTaxEl = exonerationTaxEl;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes - for Spark
     */
    public String getExonerationTaxElSpark() {
        return exonerationTaxElSpark;
    }

    /**
     * @param exonerationTaxElSpark Expression to determine if account is exonerated from taxes - for Spark
     */
    public void setExonerationTaxElSpark(String exonerationTaxElSpark) {
        this.exonerationTaxElSpark = exonerationTaxElSpark;
    }

    /**
     * @return the exonerationReason
     */
    public String getExonerationReason() {
        return exonerationReason;
    }

    /**
     * @param exonerationReason the exonerationReason to set
     */
    public void setExonerationReason(String exonerationReason) {
        this.exonerationReason = exonerationReason;
    }

    @Override
    public String toString() {
        return "CustomerCategoryDto [exoneratedFromTaxes=" + exoneratedFromTaxes + ", exonerationTaxEl=" + exonerationTaxEl + ", exonerationTaxElSpark=" + exonerationTaxElSpark
                + ", exonerationReason=" + exonerationReason + "]";
    }
}