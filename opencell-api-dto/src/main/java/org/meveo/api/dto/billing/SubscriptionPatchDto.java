package org.meveo.api.dto.billing;

import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.TerminationReasonsDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionPatchDto {

    private String offerTemplate;
    private String newSubscriptionCode;
    private String terminationReason;
    private Date effectiveDate;
    private ServicesToInstantiateDto servicesToInstantiate;
    private ServicesToActivateDto servicesToActivate;
    private Boolean updateSubscriptionDate;
    private Boolean resetRenewalTerms;

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

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public Date getEffectiveDate() {
        return effectiveDate == null ? new Date() : effectiveDate;
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

    public Boolean getResetRenewalTerms() {
        return resetRenewalTerms;
    }

    public void setResetRenewalTerms(Boolean resetRenewalTerms) {
        this.resetRenewalTerms = resetRenewalTerms;
    }
}
