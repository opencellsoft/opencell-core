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
    }

    /**
     * Checks if is exonerated from taxes.
     *
     * @return the boolean
     */
    public Boolean isExoneratedFromTaxes() {
        return exoneratedFromTaxes;
    }

    /**
     * Sets the exonerated from taxes.
     *
     * @param exoneratedFromTaxes the new exonerated from taxes
     */
    public void setExoneratedFromTaxes(Boolean exoneratedFromTaxes) {
        this.exoneratedFromTaxes = exoneratedFromTaxes;
    }

    /**
     * Gets the exoneration tax el.
     *
     * @return the exonerationTaxEl
     */
    public String getExonerationTaxEl() {
        return exonerationTaxEl;
    }

    /**
     * Sets the exoneration tax el.
     *
     * @param exonerationTaxEl the exonerationTaxEl to set
     */
    public void setExonerationTaxEl(String exonerationTaxEl) {
        this.exonerationTaxEl = exonerationTaxEl;
    }

    /**
     * Gets the exoneration reason.
     *
     * @return the exonerationReason
     */
    public String getExonerationReason() {
        return exonerationReason;
    }

    /**
     * Sets the exoneration reason.
     *
     * @param exonerationReason the exonerationReason to set
     */
    public void setExonerationReason(String exonerationReason) {
        this.exonerationReason = exonerationReason;
    }
    
    @Override
    public String toString() {
        return "CustomerCategoryDto [code=" + getCode() + ", description=" + getDescription() + ", exoneratedFromTaxes=" + exoneratedFromTaxes + ", exonerationTaxEl="
                + exonerationTaxEl + ", exonerationReason=" + exonerationReason + "]";
    }    
}