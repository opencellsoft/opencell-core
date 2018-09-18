package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.ProductDto;

/**
 * The Class ApplyProductRequestDto.
 */
@XmlRootElement(name = "ApplyProductRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyProductRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3910185882621015476L;

    /** The product. */
    @XmlElement(required = true)
    private String product;

    /** The user account. */
    @XmlElement
    private String userAccount;

    /** The subscription. */
    @XmlElement
    private String subscription;

    /** The operation date. */
    @XmlElement(required = true)
    private Date operationDate;

    /** The quantity. */
    private BigDecimal quantity;

    /** The description. */
    private String description;
    
    /** The amount without tax. */
    private BigDecimal amountWithoutTax;
    
    /** The amount with tax. */
    private BigDecimal amountWithTax;
    
    /** The criteria 1. */
    private String criteria1;
    
    /** The criteria 2. */
    private String criteria2;
    
    /** The criteria 3. */
    private String criteria3;

    /** The custom fields. */
    private CustomFieldsDto customFields;
    
    /** The seller. */
    private String seller;

    /**
     * Instantiates a new apply product request dto.
     */
    public ApplyProductRequestDto() {

    }

    /**
     * Instantiates a new apply product request dto.
     *
     * @param productDto the product dto
     */
    public ApplyProductRequestDto(ProductDto productDto) {
        this.amountWithoutTax = productDto.getAmountWithoutTax();
        this.amountWithTax = productDto.getAmountWithTax();
        this.criteria1 = productDto.getCriteria1();
        this.criteria2 = productDto.getCriteria2();
        this.criteria3 = productDto.getCriteria3();
        this.description = productDto.getDescription();
        this.operationDate = productDto.getChargeDate();
        this.product = productDto.getCode();
        this.quantity = productDto.getQuantity();
        // FIXME
        // this.userAccount=productDto.get;
    }

    /**
     * Gets the product.
     *
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * Sets the product.
     *
     * @param product the new product
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * Gets the user account.
     *
     * @return the user account
     */
    public String getUserAccount() {
        return userAccount;
    }

    /**
     * Sets the user account.
     *
     * @param userAccount the new user account
     */
    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * Gets the subscription.
     *
     * @return the subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Sets the subscription.
     *
     * @param subscription the new subscription
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    /**
     * Gets the operation date.
     *
     * @return the operation date
     */
    public Date getOperationDate() {
        return operationDate;
    }

    /**
     * Sets the operation date.
     *
     * @param operationDate the new operation date
     */
    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Gets the criteria 1.
     *
     * @return the criteria 1
     */
    public String getCriteria1() {
        return criteria1;
    }

    /**
     * Sets the criteria 1.
     *
     * @param criteria1 the new criteria 1
     */
    public void setCriteria1(String criteria1) {
        this.criteria1 = criteria1;
    }

    /**
     * Gets the criteria 2.
     *
     * @return the criteria 2
     */
    public String getCriteria2() {
        return criteria2;
    }

    /**
     * Sets the criteria 2.
     *
     * @param criteria2 the new criteria 2
     */
    public void setCriteria2(String criteria2) {
        this.criteria2 = criteria2;
    }

    /**
     * Gets the criteria 3.
     *
     * @return the criteria 3
     */
    public String getCriteria3() {
        return criteria3;
    }

    /**
     * Sets the criteria 3.
     *
     * @param criteria3 the new criteria 3
     */
    public void setCriteria3(String criteria3) {
        this.criteria3 = criteria3;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
    
    /**
     * Gets the seller.
     * 
	 * @return the seller
	 */
	public String getSeller() {
		return seller;
	}

	/**
	 * Sets the seller.
	 * 
	 * @param seller the seller to set
	 */
	public void setSeller(String seller) {
		this.seller = seller;
	}

	@Override
    public String toString() {
        return "ApplyProductRequestDto [product=" + product + ", userAccount=" + userAccount + ", subscription=" + subscription + ", operationDate=" + operationDate
                + ", description=" + description + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax + ", criteria1=" + criteria1 + ", criteria2="
                + criteria2 + ", criteria3=" + criteria3 + "]";
    }
}