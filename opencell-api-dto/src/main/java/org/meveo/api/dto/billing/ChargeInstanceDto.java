package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.ChargeInstance;

/**
 * The Class ChargeInstanceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceDto extends BusinessEntityDto {

    /**
     * The auto generated serial no
     */
    private static final long serialVersionUID = -1785188672347740647L;

    /** The status. */
    private String status;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The seller code. */
    private String sellerCode;

    /** The user account code. */
    private String userAccountCode;

    /**
     * Instantiates a new charge instance dto.
     */
    public ChargeInstanceDto() {

    }

    /**
     * Instantiates a new charge instance dto.
     *
     * @param chargeInstance the ChargeInstance entity
     */
    public ChargeInstanceDto(ChargeInstance chargeInstance) {
        super(chargeInstance);
        if (chargeInstance != null) {
            this.code = chargeInstance.getCode();
            this.description = chargeInstance.getDescription();
            if (chargeInstance.getStatus() != null) {
                this.status = chargeInstance.getStatus().name();
            }
            this.amountWithTax = chargeInstance.getAmountWithTax();
            this.amountWithoutTax = chargeInstance.getAmountWithoutTax();
            if (chargeInstance.getSeller() != null) {
                this.sellerCode = chargeInstance.getSeller().getCode();
            }
            if (chargeInstance.getUserAccount() != null) {
                this.userAccountCode = chargeInstance.getUserAccount().getCode();
            }
        }
    }

    /**
     * Instantiates a new charge instance dto.
     *
     * @param code the code
     * @param description the description
     * @param status the status
     * @param amountWithTax the amount with tax
     * @param amountWithoutTax the amount without tax
     * @param sellerCode the seller code
     * @param userAccountCode the user account code
     */
    public ChargeInstanceDto(String code, String description, String status, BigDecimal amountWithTax, BigDecimal amountWithoutTax, String sellerCode, String userAccountCode) {
        super();
        this.code = code;
        this.description = description;
        this.status = status;
        this.amountWithTax = amountWithTax;
        this.amountWithoutTax = amountWithoutTax;
        this.sellerCode = sellerCode;
        this.userAccountCode = userAccountCode;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the amount with tax.
     *
     * @return the amount with tax
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amount with tax.
     *
     * @param amountWithTax the new amount with tax
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the new amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the seller code.
     *
     * @return the seller code
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * Sets the seller code.
     *
     * @param sellerCode the new seller code
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * Gets the user account code.
     *
     * @return the user account code
     */
    public String getUserAccountCode() {
        return userAccountCode;
    }

    /**
     * Sets the user account code.
     *
     * @param userAccountCode the new user account code
     */
    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    @Override
    public String toString() {
        return "ChargeInstanceDto [code=" + code + ", description=" + description + ", status=" + status + ", amountWithTax=" + amountWithTax + ", amountWithoutTax="
                + amountWithoutTax + ", sellerCode=" + sellerCode + ", userAccountCode=" + userAccountCode + "]";
    }

}