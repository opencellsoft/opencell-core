package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionPatchDto {

    private String offerTemplate;
    private String newSubscriptionCode;
    @XmlElement(required = true)
    private Boolean terminateOldSubscription = Boolean.TRUE;
    private String terminationReason;
    private Date effectiveDate;
    private ServicesToInstantiateDto servicesToInstantiate;
    private ServicesToActivateDto servicesToActivate;
    private Boolean updateSubscriptionDate;
    private Boolean reengageCustomer = Boolean.TRUE;
    private Boolean resetRenewalTerms;
    List<String> subscriptionCustomFieldsToCopy;

    /**
     * A PriceList Code. (Optional)
     */
    @XmlElement(required = false)
    private String priceListCode;

    public String getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(String offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public String getNewSubscriptionCode() {
        return newSubscriptionCode;
    }

    public void setNewSubscriptionCode(String newSubscriptionCode) {
        this.newSubscriptionCode = newSubscriptionCode;
    }

    public Boolean getTerminateOldSubscription() {
        return terminateOldSubscription;
    }

    public void setTerminateOldSubscription(Boolean terminateOldSubscription) {
        this.terminateOldSubscription = terminateOldSubscription;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public ServicesToInstantiateDto getServicesToInstantiate() {
        return servicesToInstantiate;
    }

    public void setServicesToInstantiate(ServicesToInstantiateDto servicesToInstantiate) {
        this.servicesToInstantiate = servicesToInstantiate;
    }

    public ServicesToActivateDto getServicesToActivate() {
        return servicesToActivate;
    }

    public void setServicesToActivate(ServicesToActivateDto servicesToActivate) {
        this.servicesToActivate = servicesToActivate;
    }

    public Boolean getUpdateSubscriptionDate() {
        return updateSubscriptionDate == null ? false : updateSubscriptionDate;
    }

    public void setUpdateSubscriptionDate(Boolean updateSubscriptionDate) {
        this.updateSubscriptionDate = updateSubscriptionDate;
    }

    public Boolean getReengageCustomer() {
        return reengageCustomer;
    }

    public void setReengageCustomer(Boolean reengageCustomer) {
        this.reengageCustomer = reengageCustomer;
    }

    public Boolean getResetRenewalTerms() {
        return resetRenewalTerms == null ? false : resetRenewalTerms;
    }

    public void setResetRenewalTerms(Boolean resetRenewalTerms) {
        this.resetRenewalTerms = resetRenewalTerms;
    }

	/**
	 * @return the subscriptionCustomFieldsToCopy
	 */
	public List<String> getSubscriptionCustomFieldsToCopy() {
		return subscriptionCustomFieldsToCopy;
	}

	/**
	 * @param subscriptionCustomFieldsToCopy the subscriptionCustomFieldsToCopy to set
	 */
	public void setSubscriptionCustomFieldsToCopy(List<String> subscriptionCustomFieldsToCopy) {
		this.subscriptionCustomFieldsToCopy = subscriptionCustomFieldsToCopy;
	}

    /**
     * PriceListCode Getter
     * @return the Code of the PriceList linked to the subscription
     */
    public String getPriceListCode() {
        return priceListCode;
    }

    /**
     * PriceListCode Setter
     * @param priceListCode the code of the PriceList to link to this subscription
     */
    public void setPriceListCode(String priceListCode) {
        this.priceListCode = priceListCode;
    }
}
