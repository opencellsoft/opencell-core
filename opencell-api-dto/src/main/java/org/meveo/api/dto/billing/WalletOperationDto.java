package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;

/**
 * The Class WalletOperationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "WalletOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletOperationDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1920217666509809184L;

    /** The user account. */
    private String userAccount;

    /** The subscription. */
    private String subscription;

    /** The wallet template. */
    private String walletTemplate;

    /** The seller. */
    private String seller;

    /** The charge instance. */
    private String chargeInstance;

    /** The charge instance id. */
    private Long chargeInstanceId;

    /** The currency. */
    private String currency;

    /** The type. */
    private OperationTypeEnum type;

    /** The status. */
    private WalletOperationStatusEnum status;

    /** The rating unit description. */
    private String ratingUnitDescription;

    /** The tax percent. */
    private BigDecimal taxPercent;

    /** The unit amount without tax. */
    private BigDecimal unitAmountWithoutTax;

    /** The unit amount with tax. */
    private BigDecimal unitAmountWithTax;

    /** The unit amount tax. */
    private BigDecimal unitAmountTax;

    /** The quantity. */
    private BigDecimal quantity;

    /** The amount without tax. */
    private BigDecimal amountWithoutTax;

    /** The amount with tax. */
    private BigDecimal amountWithTax;

    /** The amount tax. */
    private BigDecimal amountTax;

    /** The parameter 1. */
    private String parameter1;

    /** The parameter 2. */
    private String parameter2;

    /** The parameter 3. */
    private String parameter3;

    /** The parameter extra. */
    private String parameterExtra;

    /** The order number. */
    private String orderNumber;

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /** The operation date. */
    private Date operationDate;

    /** The subscription date. */
    private Date subscriptionDate;

    /** The offer code. */
    private String offerCode;

    /** The raw amount without tax. */
    private BigDecimal rawAmountWithoutTax;

    /** The raw amount with tax. */
    private BigDecimal rawAmountWithTax;

    /**
     * Instantiates a new wallet operation dto.
     */
    public WalletOperationDto() {

    }

    /**
     * Instantiates a new wallet operation dto.
     *
     * @param walletOperation the WalletOperation entity
     */
    public WalletOperationDto(WalletOperation walletOperation) {
        super(walletOperation);
        seller = walletOperation.getSeller().getCode();

        if (walletOperation.getWallet() != null && walletOperation.getWallet().getWalletTemplate() != null) {
            walletTemplate = walletOperation.getWallet().getWalletTemplate().getCode();
        }

        currency = walletOperation.getCurrency().getCurrencyCode();
        type = walletOperation.getType();
        status = walletOperation.getStatus();
        ratingUnitDescription = walletOperation.getRatingUnitDescription();
        taxPercent = walletOperation.getTaxPercent();
        unitAmountWithoutTax = walletOperation.getUnitAmountWithoutTax();
        unitAmountWithTax = walletOperation.getUnitAmountWithTax();
        unitAmountTax = walletOperation.getUnitAmountTax();
        quantity = walletOperation.getQuantity();
        amountWithoutTax = walletOperation.getAmountWithoutTax();
        amountWithTax = walletOperation.getAmountWithTax();
        amountTax = walletOperation.getAmountTax();
        parameter1 = walletOperation.getParameter1();
        parameter2 = walletOperation.getParameter2();
        parameter3 = walletOperation.getParameter3();
        parameterExtra = walletOperation.getParameterExtra();
        startDate = walletOperation.getStartDate();
        endDate = walletOperation.getEndDate();
        operationDate = walletOperation.getOperationDate();
        subscriptionDate = walletOperation.getSubscriptionDate();
        walletTemplate = walletOperation.getWallet().getCode();
        userAccount = walletOperation.getWallet().getUserAccount().getCode();
        offerCode = walletOperation.getOfferCode();
        chargeInstance = walletOperation.getChargeInstance().getCode();
        chargeInstanceId = walletOperation.getChargeInstance().getId();
        rawAmountWithoutTax = walletOperation.getRawAmountWithoutTax();
        rawAmountWithTax = walletOperation.getRawAmountWithTax();
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
     * Gets the currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
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
     * @param seller the new seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public OperationTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(OperationTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
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
     * Gets the tax percent.
     *
     * @return the tax percent
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * Sets the tax percent.
     *
     * @param taxPercent the new tax percent
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    /**
     * Gets the unit amount without tax.
     *
     * @return the unit amount without tax
     */
    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    /**
     * Sets the unit amount without tax.
     *
     * @param unitAmountWithoutTax the new unit amount without tax
     */
    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    /**
     * Gets the unit amount with tax.
     *
     * @return the unit amount with tax
     */
    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    /**
     * Sets the unit amount with tax.
     *
     * @param unitAmountWithTax the new unit amount with tax
     */
    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    /**
     * Gets the unit amount tax.
     *
     * @return the unit amount tax
     */
    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    /**
     * Sets the unit amount tax.
     *
     * @param unitAmountTax the new unit amount tax
     */
    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
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
     * Gets the amount tax.
     *
     * @return the amount tax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amount tax.
     *
     * @param amountTax the new amount tax
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Gets the parameter 1.
     *
     * @return the parameter 1
     */
    public String getParameter1() {
        return parameter1;
    }

    /**
     * Sets the parameter 1.
     *
     * @param parameter1 the new parameter 1
     */
    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    /**
     * Gets the parameter 2.
     *
     * @return the parameter 2
     */
    public String getParameter2() {
        return parameter2;
    }

    /**
     * Sets the parameter 2.
     *
     * @param parameter2 the new parameter 2
     */
    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    /**
     * Gets the parameter 3.
     *
     * @return the parameter 3
     */
    public String getParameter3() {
        return parameter3;
    }

    /**
     * Sets the parameter 3.
     *
     * @param parameter3 the new parameter 3
     */
    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    /**
     * Gets the parameter extra.
     *
     * @return the parameter extra
     */
    public String getParameterExtra() {
        return parameterExtra;
    }

    /**
     * Sets the parameter extra.
     *
     * @param parameterExtra the new parameter extra
     */
    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * Gets the wallet template.
     *
     * @return the wallet template
     */
    public String getWalletTemplate() {
        return walletTemplate;
    }

    /**
     * Sets the wallet template.
     *
     * @param walletTemplate the new wallet template
     */
    public void setWalletTemplate(String walletTemplate) {
        this.walletTemplate = walletTemplate;
    }

    /**
     * Gets the charge instance.
     *
     * @return the charge instance
     */
    public String getChargeInstance() {
        return chargeInstance;
    }

    /**
     * Sets the charge instance.
     *
     * @param chargeInstance the new charge instance
     */
    public void setChargeInstance(String chargeInstance) {
        this.chargeInstance = chargeInstance;
    }

    /**
     * Gets the charge instance id.
     *
     * @return the charge instance id
     */
    public Long getChargeInstanceId() {
        return chargeInstanceId;
    }

    /**
     * Sets the charge instance id.
     *
     * @param chargeInstanceId the new charge instance id
     */
    public void setChargeInstanceId(Long chargeInstanceId) {
        this.chargeInstanceId = chargeInstanceId;
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
     * Gets the rating unit description.
     *
     * @return the rating unit description
     */
    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    /**
     * Sets the rating unit description.
     *
     * @param ratingUnitDescription the new rating unit description
     */
    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    /**
     * Gets the offer code.
     *
     * @return the offer code
     */
    public String getOfferCode() {
        return offerCode;
    }

    /**
     * Sets the offer code.
     *
     * @param offerCode the new offer code
     */
    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    /**
     * Gets the raw amount without tax.
     *
     * @return the raw amount without tax
     */
    public BigDecimal getRawAmountWithoutTax() {
        return rawAmountWithoutTax;
    }

    /**
     * Sets the raw amount without tax.
     *
     * @param rawAmountWithoutTax the new raw amount without tax
     */
    public void setRawAmountWithoutTax(BigDecimal rawAmountWithoutTax) {
        this.rawAmountWithoutTax = rawAmountWithoutTax;
    }

    /**
     * Gets the raw amount with tax.
     *
     * @return the raw amount with tax
     */
    public BigDecimal getRawAmountWithTax() {
        return rawAmountWithTax;
    }

    /**
     * Sets the raw amount with tax.
     *
     * @param rawAmountWithTax the new raw amount with tax
     */
    public void setRawAmountWithTax(BigDecimal rawAmountWithTax) {
        this.rawAmountWithTax = rawAmountWithTax;
    }
    
    @Override
    public String toString() {
        return "WalletOperationDto [code=" + code + ", description=" + description + ", userAccount=" + userAccount + ", subscription=" + subscription + ", walletTemplate="
                + walletTemplate + ", seller=" + seller + ", chargeInstance=" + chargeInstance + ", chargeInstanceId=" + chargeInstanceId + ", currency=" + currency + ", type="
                + type + ", status=" + status + ", ratingUnitDescription=" + ratingUnitDescription + ", taxPercent=" + taxPercent + ", unitAmountWithoutTax=" + unitAmountWithoutTax
                + ", unitAmountWithTax=" + unitAmountWithTax + ", unitAmountTax=" + unitAmountTax + ", quantity=" + quantity + ", amountWithoutTax=" + amountWithoutTax
                + ", amountWithTax=" + amountWithTax + ", amountTax=" + amountTax + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", parameter3=" + parameter3
                + ", parameterExtra=" + parameterExtra + ", orderNumber=" + orderNumber + ", startDate=" + startDate + ", endDate=" + endDate + ", operationDate=" + operationDate
                + ", subscriptionDate=" + subscriptionDate + ", offerCode=" + offerCode + "]";
    }
}