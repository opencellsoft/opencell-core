package org.meveo.api.dto.payment;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.message.exception.InvalidDTOException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.TipPaymentMethod;
import org.meveo.model.payments.WirePaymentMethod;

/**
 * The PaymentMethod Dto.
 *
 * @author anasseh
 */
@XmlRootElement(name = "PaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodDto extends BaseDto {

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
     * Default constructor.
     */
    public PaymentMethodDto() {
    }

    /**
     * constructor with paymentType.
     *
     * @param paymentType
     */
    public PaymentMethodDto(PaymentMethodEnum paymentType) {
        this.paymentMethodType = paymentType;
    }

    /**
     * Constructor for TIP/DD types.
     *
     * @param paymentType
     * @param bankCoordinatesDto
     * @param mandateIdentification
     * @param mandateDate
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
        if (paymentMethod.getCustomerAccount() != null) {
            this.customerAccountCode = paymentMethod.getCustomerAccount().getCode();
        }
        if (paymentMethod instanceof DDPaymentMethod) {
            this.setPaymentMethodType(PaymentMethodEnum.DIRECTDEBIT);
            this.mandateDate = ((DDPaymentMethod) paymentMethod).getMandateDate();
            this.mandateIdentification = ((DDPaymentMethod) paymentMethod).getMandateIdentification();
            this.bankCoordinates = new BankCoordinatesDto(((DDPaymentMethod) paymentMethod).getBankCoordinates());
        }
        if (paymentMethod instanceof TipPaymentMethod) {
            this.setPaymentMethodType(PaymentMethodEnum.TIP);
            this.bankCoordinates = new BankCoordinatesDto(((TipPaymentMethod) paymentMethod).getBankCoordinates());
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
     * @param cardPaymentMethodDto
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
     * @return PaymentMethod entity.
     */
    public final PaymentMethod fromDto(CustomerAccount customerAccount) {
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

            case TIP:
                pmEntity = new TipPaymentMethod(customerAccount, isDisabled(), getAlias(), getBankCoordinates() != null ? getBankCoordinates().fromDto() : null);
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
     * @return the paymentMethodType
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * @param paymentMethodType the paymentMethodType to set
     */
    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the preferred
     */
    public boolean isPreferred() {
        return preferred;
    }

    /**
     * @param preferred the preferred to set
     */
    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    /**
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * @param customerAccountCode the customerAccountCode to set
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * @return the info1
     */
    public String getInfo1() {
        return info1;
    }

    /**
     * @param info1 the info1 to set
     */
    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    /**
     * @return the info2
     */
    public String getInfo2() {
        return info2;
    }

    /**
     * @param info2 the info2 to set
     */
    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    /**
     * @return the info3
     */
    public String getInfo3() {
        return info3;
    }

    /**
     * @param info3 the info3 to set
     */
    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    /**
     * @return the info4
     */
    public String getInfo4() {
        return info4;
    }

    /**
     * @param info4 the info4 to set
     */
    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    /**
     * @return the info5
     */
    public String getInfo5() {
        return info5;
    }

    /**
     * @param info5 the info5 to set
     */
    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    /**
     * @return the bankCoordinates
     */
    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    /**
     * @param bankCoordinates the bankCoordinates to set
     */
    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    /**
     * @return the mandateIdentification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * @param mandateIdentification the mandateIdentification to set
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * @return the mandateDate
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * @param mandateDate the mandateDate to set
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /**
     * @return the cardType
     */
    public CreditCardTypeEnum getCardType() {
        return cardType;
    }

    /**
     * @param cardType the cardType to set
     */
    public void setCardType(CreditCardTypeEnum cardType) {
        this.cardType = cardType;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the monthExpiration
     */
    public Integer getMonthExpiration() {
        return monthExpiration;
    }

    /**
     * @param monthExpiration the monthExpiration to set
     */
    public void setMonthExpiration(Integer monthExpiration) {
        this.monthExpiration = monthExpiration;
    }

    /**
     * @return the yearExpiration
     */
    public Integer getYearExpiration() {
        return yearExpiration;
    }

    /**
     * @param yearExpiration the yearExpiration to set
     */
    public void setYearExpiration(Integer yearExpiration) {
        this.yearExpiration = yearExpiration;
    }

    /**
     * @return the tokenId
     */
    public String getTokenId() {
        return tokenId;
    }

    /**
     * @param tokenId the tokenId to set
     */
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * @return the cardNumber
     */
    public String getCardNumber() {
        if (cardNumber != null) {
            return cardNumber.replaceAll("\\s+", "");
        }
        return cardNumber;
    }

    /**
     * @param cardNumber the cardNumber to set
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * @return the issueNumber
     */
    public String getIssueNumber() {
        return issueNumber;
    }

    /**
     * @param issueNumber the issueNumber to set
     */
    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Validate the PaymentMethodDto.
     */
    public void validate() {
        validate(false);
    }

    /**
     * Validate the PaymentMethodDto.
     *
     * @param isRoot is the root Dto or sub Dto.
     */
    public void validate(boolean isRoot) {
        PaymentMethodEnum type = getPaymentMethodType();
        if (type == null) {
            throw new InvalidDTOException("Missing payment method type");
        }
        if (isRoot && StringUtils.isBlank(getCustomerAccountCode())) {
            throw new InvalidDTOException("Missing customerAccountCode");
        }
        if (type == PaymentMethodEnum.CARD) {
            int numberLength = getCardNumber().length();
            CreditCardTypeEnum cardType = getCardType();
            if (StringUtils.isBlank(getCardNumber())
                    || (numberLength != 16 && cardType != CreditCardTypeEnum.AMERICAN_EXPRESS)
                    || (numberLength != 15 && cardType == CreditCardTypeEnum.AMERICAN_EXPRESS)) {
                throw new InvalidDTOException("Invalid cardNumber");
            }
            if (StringUtils.isBlank(getOwner())) {
                throw new InvalidDTOException("Missing Owner");
            }
            if (StringUtils.isBlank(getMonthExpiration()) || StringUtils.isBlank(getYearExpiration())) {
                throw new InvalidDTOException("Missing expiryDate");
            }

            return;
        }
        if (type == PaymentMethodEnum.DIRECTDEBIT || type == PaymentMethodEnum.TIP) {
            validateBankCoordinates(type);
            return;
        }

    }

    /**
     * Check bank coordinates fields.
     *
     *
     * @param bankCoordinatesDto the bankCoordinatesDto.
     */
    private void validateBankCoordinates(PaymentMethodEnum type) {
        BankCoordinatesDto bankCoordinates = getBankCoordinates();
        if (bankCoordinates == null) {
            throw new InvalidDTOException("Missing bank coordinates.");
        }
        if (StringUtils.isBlank(bankCoordinates.getAccountOwner())) {
            throw new InvalidDTOException("Missing account owner.");
        }
        if (StringUtils.isBlank(bankCoordinates.getBankName())) {
            throw new InvalidDTOException("Missing bank name.");
        }

        if (type == PaymentMethodEnum.DIRECTDEBIT) {
            if (StringUtils.isBlank(bankCoordinates.getBic())) {
                throw new InvalidDTOException("Missing BIC.");
            }

            if (StringUtils.isBlank(bankCoordinates.getIban())) {
                throw new InvalidDTOException("Missing IBAN.");
            }
        }

        if (type == PaymentMethodEnum.TIP) {
            if (StringUtils.isBlank(bankCoordinates.getAccountNumber())) {
                throw new InvalidDTOException("Missing account number.");
            }

            if (StringUtils.isBlank(bankCoordinates.getBankCode())) {
                throw new InvalidDTOException("Missing bank code.");
            }

            if (StringUtils.isBlank(bankCoordinates.getBranchCode())) {
                throw new InvalidDTOException("Missing branch code.");
            }

            if (StringUtils.isBlank(bankCoordinates.getKey())) {
                throw new InvalidDTOException("Missing key.");
            }
        }
    }

    @Override
    public final String toString() {
        return "PaymentMethodDto [paymentMethodType=" + paymentMethodType + ", id=" + id + ", disabled=" + disabled + ", alias=" + alias + ", preferred=" + preferred
                + ", customerAccountCode=" + customerAccountCode + ", info1=" + info1 + ", info2=" + info2 + ", info3=" + info3 + ", info4=" + info4 + ", info5=" + info5
                + ", bankCoordinates=" + bankCoordinates + ", mandateIdentification=" + mandateIdentification + ", mandateDate=" + mandateDate + ", cardType=" + cardType
                + ", owner=" + owner + ", monthExpiration=" + monthExpiration + ", yearExpiration=" + yearExpiration + ", tokenId=" + tokenId + ", cardNumber="
                + CardPaymentMethod.hideCardNumber(cardNumber) + ", issueNumber=" + issueNumber + ", userId=" + userId + "]";
    }
}
