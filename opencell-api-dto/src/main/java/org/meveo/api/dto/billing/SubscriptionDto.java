package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.account.AccessesDto;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;

/**
 * The Class SubscriptionDto.
 * 
 * @author anasseh
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6021918810749866648L;

    /** The user account. */
    @XmlElement(required = true)
    private String userAccount;

    /** The offer template. */
    @XmlElement(required = true)
    private String offerTemplate;

    /** The subscription date. */
    @XmlElement(required = true)
    private Date subscriptionDate;

    /** The termination date. */
    private Date terminationDate;

    /** The end agreement date. */
    private Date endAgreementDate;

    /** The status. */
    private SubscriptionStatusEnum status;

    /** The status date. */
    private Date statusDate;

    /** The custom fields. */
    @XmlElement(required = false)
    private CustomFieldsDto customFields;

    /** The accesses. */
    @XmlElement(required = false)
    private AccessesDto accesses = new AccessesDto();

    /** The services. */
    @XmlElement(required = false)
    private ServiceInstancesDto services = new ServiceInstancesDto();

    /**
     * Use in creation and update.
     */
    @XmlElement(required = false)
    private ProductsDto products = new ProductsDto();

    /**
     * Use in find.
     */
    @XmlElementWrapper(name = "productInstances")
    @XmlElement(name = "productInstance")
    private List<ProductInstanceDto> productInstances = new ArrayList<ProductInstanceDto>();

    /** The termination reason. */
    private String terminationReason;

    /** The order number. */
    private String orderNumber;

    /**
     * Expression to determine minimum amount value
     */
    private String minimumAmountEl;

    /**
     * Expression to determine minimum amount value - for Spark
     */
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    private String minimumLabelEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    private String minimumLabelElSpark;

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    private Date subscribedTillDate;

    /** Was subscription renewed. */
    private boolean renewed;

    /** Was/when "endOfTerm" notification fired for soon to expire subscription. */
    private Date renewalNotifiedDate;

    /** The renewal rule. */
    private SubscriptionRenewalDto renewalRule;

    /** The billing cycle. */
    @XmlElement(required = true)
    private String billingCycle;

    /** The seller. */
    private String seller;
    
    /** The auto end of engagement. */
    private Boolean autoEndOfEngagement;
    
    /**
     * String value matched in the usageRatingJob to group the EDRs for rating.
     */
    private String ratingGroup;

    /**
     * Instantiates a new subscription dto.
     */
    public SubscriptionDto() {
        super();
    }

    /**
     * Instantiates a new subscription dto.
     * 
     * @param e Subscription entity
     */
    public SubscriptionDto(Subscription e) {
        super(e);

        setStatus(e.getStatus());
        setStatusDate(e.getStatusDate());
        setOrderNumber(e.getOrderNumber());

        if (e.getUserAccount() != null) {
            setUserAccount(e.getUserAccount().getCode());
        }

        if (e.getOffer() != null) {
            setOfferTemplate(e.getOffer().getCode());
        }

        setSubscriptionDate(e.getSubscriptionDate());
        setTerminationDate(e.getTerminationDate());
        if (e.getSubscriptionTerminationReason() != null) {
            setTerminationReason(e.getSubscriptionTerminationReason().getCode());
        }
        setEndAgreementDate(e.getEndAgreementDate());
        setSubscribedTillDate(e.getSubscribedTillDate());
        setRenewed(e.isRenewed());
        setRenewalNotifiedDate(e.getRenewalNotifiedDate());
        setRenewalRule(new SubscriptionRenewalDto(e.getSubscriptionRenewal()));
        setMinimumAmountEl(e.getMinimumAmountEl());
        setMinimumAmountElSpark(e.getMinimumAmountElSpark());
        setMinimumLabelEl(e.getMinimumLabelEl());
        setMinimumLabelElSpark(e.getMinimumLabelElSpark());
        if(e.getSeller() != null) {
        	setSeller(e.getSeller().getCode());
        }
		setRatingGroup(e.getRatingGroup());
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
     * Gets the offer template.
     *
     * @return the offer template
     */
    public String getOfferTemplate() {
        return offerTemplate;
    }

    /**
     * Sets the offer template.
     *
     * @param offerTemplate the new offer template
     */
    public void setOfferTemplate(String offerTemplate) {
        this.offerTemplate = offerTemplate;
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
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Gets the end agreement date.
     *
     * @return the end agreement date
     */
    public Date getEndAgreementDate() {
        return endAgreementDate;
    }

    /**
     * Sets the end agreement date.
     *
     * @param endAgreementDate the new end agreement date
     */
    public void setEndAgreementDate(Date endAgreementDate) {
        this.endAgreementDate = endAgreementDate;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(SubscriptionStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the status date.
     *
     * @return the status date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the status date.
     *
     * @param statusDate the new status date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the accesses.
     *
     * @return the accesses
     */
    public AccessesDto getAccesses() {
        return accesses;
    }

    /**
     * Sets the accesses.
     *
     * @param accesses the new accesses
     */
    public void setAccesses(AccessesDto accesses) {
        this.accesses = accesses;
    }

    /**
     * Gets the services.
     *
     * @return the services
     */
    public ServiceInstancesDto getServices() {
        return services;
    }

    /**
     * Sets the services.
     *
     * @param services the new services
     */
    public void setServices(ServiceInstancesDto services) {
        this.services = services;
    }

    /**
     * Gets the products.
     *
     * @return the products
     */
    public ProductsDto getProducts() {
        return products;
    }

    /**
     * Sets the products.
     *
     * @param products the new products
     */
    public void setProducts(ProductsDto products) {
        this.products = products;
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
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
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
     * Gets the subscribed till date.
     *
     * @return the subscribed till date
     */
    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    /**
     * Sets the subscribed till date.
     *
     * @param subscribedTillDate the new subscribed till date
     */
    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    /**
     * Checks if is renewed.
     *
     * @return true, if is renewed
     */
    public boolean isRenewed() {
        return renewed;
    }

    /**
     * Sets the renewed.
     *
     * @param renewed the new renewed
     */
    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    /**
     * Gets the renewal notified date.
     *
     * @return the renewal notified date
     */
    public Date getRenewalNotifiedDate() {
        return renewalNotifiedDate;
    }

    /**
     * Sets the renewal notified date.
     *
     * @param renewalNotifiedDate the new renewal notified date
     */
    public void setRenewalNotifiedDate(Date renewalNotifiedDate) {
        this.renewalNotifiedDate = renewalNotifiedDate;
    }

    /**
     * Gets the renewal rule.
     *
     * @return the renewal rule
     */
    public SubscriptionRenewalDto getRenewalRule() {
        return renewalRule;
    }

    /**
     * Sets the renewal rule.
     *
     * @param renewalRule the new renewal rule
     */
    public void setRenewalRule(SubscriptionRenewalDto renewalRule) {
        this.renewalRule = renewalRule;
    }

    /**
     * Gets the product instances.
     *
     * @return the product instances
     */
    public List<ProductInstanceDto> getProductInstances() {
        return productInstances;
    }

    /**
     * Sets the product instances.
     *
     * @param productInstances the new product instances
     */
    public void setProductInstances(List<ProductInstanceDto> productInstances) {
        this.productInstances = productInstances;
    }

    /**
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    /**
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
    }

    /**
     * Gets the billing cycle.
     *
     * @return the billing cycle
     */
    public String getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the new billing cycle
     */
    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * @return the autoEndOfEngagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }

    
    /**
	 * @return the seller
	 */
	public String getSeller() {
		return seller;
	}

	/**
	 * @param seller the seller to set
	 */
	public void setSeller(String seller) {
		this.seller = seller;
	}

    @Override
    public String toString() {
        return "SubscriptionDto [userAccount=" + userAccount + ", offerTemplate=" + offerTemplate + ", subscriptionDate=" + subscriptionDate + ", terminationDate="
                + terminationDate + ", endAgreementDate=" + endAgreementDate + ", status=" + status + ", statusDate=" + statusDate + ", customFields=" + customFields
                + ", accesses=" + accesses + ", services=" + services + ", products=" + products + ", productInstances=" + productInstances + ", terminationReason="
                + terminationReason + ", orderNumber=" + orderNumber + ", subscribedTillDate=" + subscribedTillDate + ", renewed=" + renewed + ", renewalNotifiedDate="
                + renewalNotifiedDate + ", renewalRule=" + renewalRule + "]";
    }

	public String getRatingGroup() {
		return ratingGroup;
	}

	public void setRatingGroup(String ratingGroup) {
		this.ratingGroup = ratingGroup;
	}
}