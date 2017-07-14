package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.account.AccessesDto;
import org.meveo.model.billing.SubscriptionStatusEnum;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionDto extends BusinessDto {

    private static final long serialVersionUID = -6021918810749866648L;

    @XmlElement(required = true)
    private String userAccount;

    @XmlElement(required = true)
    private String offerTemplate;

    @XmlElement(required = true)
    private Date subscriptionDate;

    private Date terminationDate;

    private Date endAgreementDate;

    private SubscriptionStatusEnum status;
    private Date statusDate;

    @XmlElement(required = false)
    private CustomFieldsDto customFields = new CustomFieldsDto();

    @XmlElement(required = false)
    private AccessesDto accesses = new AccessesDto();

    @XmlElement(required = false)
    private ServiceInstancesDto services = new ServiceInstancesDto();

    @XmlElement(required = false)
    private ProductsDto products = new ProductsDto();

    private String terminationReason;
    private String orderNumber;

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    private Date subscribedTillDate;

    /**
     * Was subscription renewed
     */
    private boolean renewed;

    /**
     * Was/when "endOfTerm" notification fired for soon to expire subscription
     */
    private Date renewalNotifiedDate;

    private SubscriptionRenewalDto renewalRule;

    public SubscriptionDto() {

    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(String offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
    }

    @Override
    public String toString() {
        return "SubscriptionDto [code=" + getCode() + ", description=" + getDescription() + ", userAccount=" + userAccount + ", offerTemplate=" + offerTemplate
                + ", subscriptionDate=" + subscriptionDate + ", terminationDate=" + terminationDate + ", status=" + status + ",statusDate=" + statusDate + ", customFields="
                + customFields + ", accesses=" + accesses + ", services=" + services + ", terminationReason=" + terminationReason + ", orderNumber=" + orderNumber + "]";
    }

    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatusEnum status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public AccessesDto getAccesses() {
        return accesses;
    }

    public void setAccesses(AccessesDto accesses) {
        this.accesses = accesses;
    }

    public ServiceInstancesDto getServices() {
        return services;
    }

    public void setServices(ServiceInstancesDto services) {
        this.services = services;
    }

    public ProductsDto getProducts() {
        return products;
    }

    public void setProducts(ProductsDto products) {
        this.products = products;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    public boolean isRenewed() {
        return renewed;
    }

    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    public Date getRenewalNotifiedDate() {
        return renewalNotifiedDate;
    }

    public void setRenewalNotifiedDate(Date renewalNotifiedDate) {
        this.renewalNotifiedDate = renewalNotifiedDate;
    }

    public SubscriptionRenewalDto getRenewalRule() {
        return renewalRule;
    }

    public void setRenewalRule(SubscriptionRenewalDto renewalRule) {
        this.renewalRule = renewalRule;
    }
}