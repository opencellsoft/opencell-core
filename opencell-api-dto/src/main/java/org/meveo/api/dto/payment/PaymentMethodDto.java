package org.meveo.api.dto.payment;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.WirePaymentMethod;
import org.meveo.security.MeveoUser;

/**
 * The PaymentMethod Dto.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "PaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4815935377652350103L;

    /**
     * PaymentMethod type.
     */
    @NotNull
    @XmlAttribute()
    private PaymentMethodEnum paymentMethodType;

    /**
     * entity id.
     */
    private Long id;
    /**
     * is disabled.
     */
    private boolean disabled = false;
    /**
     * alias.
     */
    private String alias;
    /**
     * is preferred.
     */
    private boolean preferred;
    /**
     * customerAccountCode.
     */
    private String customerAccountCode;
    /**
     * Additional info1.
     */
    private String info1;
    /**
     * Additional info2.
     */
    private String info2;
    /**
     * Additional info3.
     */
    private String info3;
    /**
     * Additional info4.
     */
    private String info4;
    /**
     * Additional info5.
     */
    private String info5;

    /**
     * Bank account information.
     */
    private BankCoordinatesDto bankCoordinates;
    /**
     * mandateIdentification for SEPA.
     */
    private String mandateIdentification;
    /**
     * mandateDate for SEPA.
     */
    private Date mandateDate;

    /**
     * Card type.
     */
    private CreditCardTypeEnum cardType;

    /**
     * Cardholder: first and last name.
     */
    private String owner;

    /**
     * Card expiration: month.
     */
    private Integer monthExpiration;

    /**
     * Card expiration: year.
     */
    private Integer yearExpiration;

    /**
     * Token ID in a payment gateway.
     */
    private String tokenId;

    /**
     * Card number: full number , with first 12 digits hiding in read operation.
     */
    private String cardNumber;

    /**
     * Issue number.
     */
    private String issueNumber;

    /**
     * User identifier.
     */
    private String userId;

    /**
     * Customer code, used only on dtp validation.
     */
    @XmlTransient
    private String customerCode;

    /**
     * Default constructor.
     */
    public PaymentMethodDto() {
    }

    /**
     * constructor with paymentType.
     *
     * @param paymentType payment type.
     */
    public PaymentMethodDto(PaymentMethodEnum paymentType) {
        this.paymentMethodType = paymentType;
    }

    /**
     * Constructor for TIP/DD types.
     *
     * @param paymentType payment type.
     * @param bankCoordinatesDto bank coordinates data transfer object.
     * @param mandateIdentification mandate identification
     * @param mandateDate mandate date
     */
    public PaymentMethodDto(PaymentMethodEnum paymentType, BankCoordinatesDto bankCoordinatesDto, String mandateIdentification, Date mandateDate) {
        this(paymentType);
        this.bankCoordinates = bankCoordinatesDto;
        this.mandateIdentification = mandateIdentification;
        this.mandateDate = mandateDate;
    }

    /**
     * Constructor with entity class.
     *
     * @param paymentMethod the paymentMethod entity.
     */
    public PaymentMethodDto(PaymentMethod paymentMethod) {
        this.id = paymentMethod.getId();
        this.disabled = paymentMethod.isDisabled();
        this.alias = paymentMethod.getAlias();
        this.preferred = paymentMethod.isPreferred();
        this.userId = paymentMethod.getUserId();
        this.info1 = paymentMethod.getInfo1();
        this.info2 = paymentMethod.getInfo2();
        this.info3 = paymentMethod.getInfo3();
        this.info4 = paymentMethod.getInfo4();
        this.info5 = paymentMethod.getInfo5();
        this.paymentMethodType = paymentMethod.getPaymentType();
        if (paymentMethod.getCustomerAccount() != null) {
            this.customerAccountCode = paymentMethod.getCustomerAccount().getCode();
        }
        if (paymentMethod instanceof DDPaymentMethod) {
            this.setPaymentMethodType(PaymentMethodEnum.DIRECTDEBIT);
            this.mandateDate = ((DDPaymentMethod) paymentMethod).getMandateDate();
            this.mandateIdentification = ((DDPaymentMethod) paymentMethod).getMandateIdentification();
            this.bankCoordinates = new BankCoordinatesDto(((DDPaymentMethod) paymentMethod).getBankCoordinates());
        }
        if (paymentMethod instanceof CardPaymentMethod) {
            this.setPaymentMethodType(PaymentMethodEnum.CARD);
            this.cardNumber = ((CardPaymentMethod) paymentMethod).getHiddenCardNumber();
            this.owner = ((CardPaymentMethod) paymentMethod).getOwner();
            this.cardType = ((CardPaymentMethod) paymentMethod).getCardType();
            this.monthExpiration = ((CardPaymentMethod) paymentMethod).getMonthExpiration();
            this.yearExpiration = ((CardPaymentMethod) paymentMethod).getYearExpiration();
            this.issueNumber = ((CardPaymentMethod) paymentMethod).getIssueNumber();
            this.tokenId = ((CardPaymentMethod) paymentMethod).getTokenId();
        }
        if (paymentMethod instanceof CheckPaymentMethod) {
            this.setPaymentMethodType(PaymentMethodEnum.CHECK);
        }
        if (paymentMethod instanceof WirePaymentMethod) {
            this.setPaymentMethodType(PaymentMethodEnum.WIRETRANSFER);
        }
    }

    /**
     * Constructor with cardPaymentMethodDto.
     *
     * @param cardPaymentMethodDto card payment method dto.
     */
    @SuppressWarnings("deprecation")
    public PaymentMethodDto(CardPaymentMethodDto cardPaymentMethodDto) {
        this.setPaymentMethodType(PaymentMethodEnum.CARD);
        this.id = cardPaymentMethodDto.getId();
        this.alias = cardPaymentMethodDto.getAlias();
        this.preferred = cardPaymentMethodDto.isPreferred();
        this.userId = cardPaymentMethodDto.getUserId();
        this.info1 = cardPaymentMethodDto.getInfo1();
        this.info2 = cardPaymentMethodDto.getInfo2();
        this.info3 = cardPaymentMethodDto.getInfo3();
        this.info4 = cardPaymentMethodDto.getInfo4();
        this.info5 = cardPaymentMethodDto.getInfo5();
        this.cardNumber = cardPaymentMethodDto.getCardNumber();
        this.owner = cardPaymentMethodDto.getOwner();
        this.cardType = cardPaymentMethodDto.getCardType();
        this.monthExpiration = cardPaymentMethodDto.getMonthExpiration();
        this.yearExpiration = cardPaymentMethodDto.getYearExpiration();
        this.issueNumber = cardPaymentMethodDto.getIssueNumber();
        this.tokenId = cardPaymentMethodDto.getTokenId();
        this.customerAccountCode = cardPaymentMethodDto.getCustomerAccountCode();
    }

    /**
     * Build entity class from dto class.
     *
     * @param customerAccount the customerAccount.
     * @param currentUser the currentUser.
     * @return PaymentMethod entity.
     */
    public final PaymentMethod fromDto(CustomerAccount customerAccount, MeveoUser currentUser) {
        PaymentMethod pmEntity = null;
        switch (getPaymentMethodType()) {
        case CARD:
            pmEntity = new CardPaymentMethod(customerAccount, isDisabled(), getAlias(), getCardNumber(), getOwner(), isPreferred(), getIssueNumber(), getYearExpiration(),
                getMonthExpiration(), getCardType());
            break;

        case DIRECTDEBIT:
            pmEntity = new DDPaymentMethod(customerAccount, isDisabled(), getAlias(), isPreferred(), getMandateDate(), getMandateIdentification(),
                getBankCoordinates() != null ? getBankCoordinates().fromDto() : null);
            break;

        case CHECK:
            pmEntity = new CheckPaymentMethod(isDisabled(), alias, preferred, customerAccount);
            break;

        case WIRETRANSFER:
            pmEntity = new WirePaymentMethod(isDisabled(), alias, preferred, customerAccount);
            break;
        default:
            break;
        }
        pmEntity.setInfo1(getInfo1());
        pmEntity.setInfo2(getInfo2());
        pmEntity.setInfo3(getInfo3());
        pmEntity.setInfo4(getInfo4());
        pmEntity.setInfo5(getInfo5());
        pmEntity.setUserId(getUserId());
        pmEntity.updateAudit(currentUser);
        return pmEntity;
    }

    /**
     * Update entity from Dto.
     *
     * @param paymentMethod paymentMethod to update.
     * @return paymentMethod updated.
     */
    public final PaymentMethod updateFromDto(PaymentMethod paymentMethod) {
        if (isPreferred()) {
            paymentMethod.setPreferred(true);
        }

        if (getAlias() != null) {
            paymentMethod.setAlias(getAlias());
        }
        paymentMethod.setDisabled(isDisabled());

        switch (getPaymentMethodType()) {

        case DIRECTDEBIT:
            if (!StringUtils.isBlank(getMandateIdentification())) {
                ((DDPaymentMethod) paymentMethod).setMandateIdentification(getMandateIdentification());
            }
            if (!StringUtils.isBlank(getMandateDate())) {
                ((DDPaymentMethod) paymentMethod).setMandateDate(getMandateDate());
            }
            if (getBankCoordinates() != null) {
                if (!StringUtils.isBlank(getBankCoordinates().getAccountNumber())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getAccountNumber());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getAccountOwner())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setAccountOwner(getBankCoordinates().getAccountOwner());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getBankCode())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setBankCode(getBankCoordinates().getBankCode());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getBankId())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setBankId(getBankCoordinates().getBankId());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getBankName())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setBankName(getBankCoordinates().getBankName());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getBic())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setBic(getBankCoordinates().getBic());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getBranchCode())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setBranchCode(getBankCoordinates().getBranchCode());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getIban())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setIban(getBankCoordinates().getIban());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getIcs())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setIcs(getBankCoordinates().getIcs());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getIssuerName())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setIssuerName(getBankCoordinates().getIssuerName());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getIssuerNumber())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setIssuerNumber(getBankCoordinates().getIssuerNumber());
                }
                if (!StringUtils.isBlank(getBankCoordinates().getKey())) {
                    ((DDPaymentMethod) paymentMethod).getBankCoordinates().setKey(getBankCoordinates().getKey());
                }
            }
            break;
        default:
            break;
        }
        if (!StringUtils.isBlank(getInfo1())) {
            paymentMethod.setInfo1(getInfo1());
        }
        if (!StringUtils.isBlank(getInfo3())) {
            paymentMethod.setInfo2(getInfo2());
        }
        if (!StringUtils.isBlank(getInfo3())) {
            paymentMethod.setInfo3(getInfo3());
        }
        if (!StringUtils.isBlank(getInfo4())) {
            paymentMethod.setInfo4(getInfo4());
        }
        if (!StringUtils.isBlank(getInfo5())) {
            paymentMethod.setInfo5(getInfo5());
        }
        if (!StringUtils.isBlank(getUserId())) {
            paymentMethod.setUserId(getUserId());
        }
        return paymentMethod;
    }

    /**
     * Gets the payment method type.
     *
     * @return the paymentMethodType
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * Sets the payment method type.
     *
     * @param paymentMethodType the paymentMethodType to set
     */
    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if is disabled.
     *
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Gets the alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     *
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Checks if is preferred.
     *
     * @return the preferred
     */
    public boolean isPreferred() {
        return preferred;
    }

    /**
     * Sets the preferred.
     *
     * @param preferred the preferred to set
     */
    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    /**
     * Gets the customer account code.
     *
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * Sets the customer account code.
     *
     * @param customerAccountCode the customerAccountCode to set
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * Gets the info 1.
     *
     * @return the info1
     */
    public String getInfo1() {
        return info1;
    }

    /**
     * Sets the info 1.
     *
     * @param info1 the info1 to set
     */
    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    /**
     * Gets the info 2.
     *
     * @return the info2
     */
    public String getInfo2() {
        return info2;
    }

    /**
     * Sets the info 2.
     *
     * @param info2 the info2 to set
     */
    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    /**
     * Gets the info 3.
     *
     * @return the info3
     */
    public String getInfo3() {
        return info3;
    }

    /**
     * Sets the info 3.
     *
     * @param info3 the info3 to set
     */
    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    /**
     * Gets the info 4.
     *
     * @return the info4
     */
    public String getInfo4() {
        return info4;
    }

    /**
     * Sets the info 4.
     *
     * @param info4 the info4 to set
     */
    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    /**
     * Gets the info 5.
     *
     * @return the info5
     */
    public String getInfo5() {
        return info5;
    }

    /**
     * Sets the info 5.
     *
     * @param info5 the info5 to set
     */
    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    /**
     * Gets the bank coordinates.
     *
     * @return the bankCoordinates
     */
    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    /**
     * Sets the bank coordinates.
     *
     * @param bankCoordinates the bankCoordinates to set
     */
    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    /**
     * Gets the mandate identification.
     *
     * @return the mandateIdentification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * Sets the mandate identification.
     *
     * @param mandateIdentification the mandateIdentification to set
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * Gets the mandate date.
     *
     * @return the mandateDate
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * Sets the mandate date.
     *
     * @param mandateDate the mandateDate to set
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /**
     * Gets the card type.
     *
     * @return the cardType
     */
    public CreditCardTypeEnum getCardType() {
        return cardType;
    }

    /**
     * Sets the card type.
     *
     * @param cardType the cardType to set
     */
    public void setCardType(CreditCardTypeEnum cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the month expiration.
     *
     * @return the monthExpiration
     */
    public Integer getMonthExpiration() {
        return monthExpiration;
    }

    /**
     * Sets the month expiration.
     *
     * @param monthExpiration the monthExpiration to set
     */
    public void setMonthExpiration(Integer monthExpiration) {
        this.monthExpiration = monthExpiration;
    }

    /**
     * Gets the year expiration.
     *
     * @return the yearExpiration
     */
    public Integer getYearExpiration() {
        return yearExpiration;
    }

    /**
     * Sets the year expiration.
     *
     * @param yearExpiration the yearExpiration to set
     */
    public void setYearExpiration(Integer yearExpiration) {
        this.yearExpiration = yearExpiration;
    }

    /**
     * Gets the token id.
     *
     * @return the tokenId
     */
    public String getTokenId() {
        return tokenId;
    }

    /**
     * Sets the token id.
     *
     * @param tokenId the tokenId to set
     */
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * Gets the card number.
     *
     * @return the cardNumber
     */
    public String getCardNumber() {
        if (cardNumber != null) {
            return cardNumber.replaceAll("\\s+", "");
        }
        return cardNumber;
    }

    /**
     * Sets the card number.
     *
     * @param cardNumber the cardNumber to set
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the issue number.
     *
     * @return the issueNumber
     */
    public String getIssueNumber() {
        return issueNumber;
    }

    /**
     * Sets the issue number.
     *
     * @param issueNumber the issueNumber to set
     */
    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    /**
     * Gets the user id.
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the customer code.
     *
     * @return the customerCode
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * Sets the customer code.
     *
     * @param customerCode the customerCode to set
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return "PaymentMethodDto [paymentMethodType=" + paymentMethodType + ", id=" + id + ", disabled=" + disabled + ", alias=" + alias + ", preferred=" + preferred
                + ", customerAccountCode=" + customerAccountCode + ", info1=" + info1 + ", info2=" + info2 + ", info3=" + info3 + ", info4=" + info4 + ", info5=" + info5
                + ", bankCoordinates=" + bankCoordinates + ", mandateIdentification=" + mandateIdentification + ", mandateDate=" + mandateDate + ", cardType=" + cardType
                + ", owner=" + owner + ", monthExpiration=" + monthExpiration + ", yearExpiration=" + yearExpiration + ", tokenId=" + tokenId + ", cardNumber="
                + CardPaymentMethod.hideCardNumber(cardNumber) + ", issueNumber=" + issueNumber + ", userId=" + userId + "]";
    }
}
