package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SearchDto;
import org.meveo.model.billing.WalletOperationStatusEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class FindWalletOperationsDto extends SearchDto {

    private static final long serialVersionUID = 4342970913973071312L;

    private WalletOperationStatusEnum status;

    private String walletTemplate;

    @XmlElement(required = true)
    private String userAccount;

    private Date fromDate;

    private Date toDate;

    private String chargeTemplateCode;

    private String parameter1;

    private String parameter2;

    private String parameter3;

    private String offerTemplateCode;

    private String subscriptionCode;

    private String orderNumber;

    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
    }

    public String getWalletTemplate() {
        return walletTemplate;
    }

    public void setWalletTemplate(String walletTemplate) {
        this.walletTemplate = walletTemplate;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getChargeTemplateCode() {
        return chargeTemplateCode;
    }

    public void setChargeTemplateCode(String chargeTemplateCode) {
        this.chargeTemplateCode = chargeTemplateCode;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getOfferTemplateCode() {
        return offerTemplateCode;
    }

    public void setOfferTemplateCode(String offerTemplateCode) {
        this.offerTemplateCode = offerTemplateCode;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}