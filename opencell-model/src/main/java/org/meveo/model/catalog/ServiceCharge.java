package org.meveo.model.catalog;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meveo.model.EnableBusinessCFEntity;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class ServiceCharge extends EnableBusinessCFEntity {
    /**
     * Mapping between service and recurring charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected List<ServiceChargeTemplateRecurring> serviceRecurringCharges = new ArrayList<>();
    /**
     * Mapping between service and subscription charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = new ArrayList<>();
    /**
     * Mapping between service and termination charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected List<ServiceChargeTemplateTermination> serviceTerminationCharges = new ArrayList<>();
    /**
     * Mapping between service and usage charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    protected List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<>();

    public ServiceChargeTemplateRecurring getServiceRecurringChargeByChargeCode(String chargeCode) {
        ServiceChargeTemplateRecurring result = null;
        for (ServiceChargeTemplateRecurring sctr : serviceRecurringCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateRecurring> getServiceRecurringCharges() {
        return serviceRecurringCharges;
    }

    public void setServiceRecurringCharges(List<ServiceChargeTemplateRecurring> serviceRecurringCharges) {
        this.serviceRecurringCharges = serviceRecurringCharges;
    }

    public ServiceChargeTemplateSubscription getServiceChargeTemplateSubscriptionByChargeCode(String chargeCode) {
        ServiceChargeTemplateSubscription result = null;
        for (ServiceChargeTemplateSubscription sctr : serviceSubscriptionCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateSubscription> getServiceSubscriptionCharges() {
        return serviceSubscriptionCharges;
    }

    public void setServiceSubscriptionCharges(List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges) {
        this.serviceSubscriptionCharges = serviceSubscriptionCharges;
    }

    public ServiceChargeTemplateTermination getServiceChargeTemplateTerminationByChargeCode(String chargeCode) {
        ServiceChargeTemplateTermination result = null;
        for (ServiceChargeTemplateTermination sctr : serviceTerminationCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateTermination> getServiceTerminationCharges() {
        return serviceTerminationCharges;
    }

    public void setServiceTerminationCharges(List<ServiceChargeTemplateTermination> serviceTerminationCharges) {
        this.serviceTerminationCharges = serviceTerminationCharges;
    }

    public ServiceChargeTemplateUsage getServiceChargeTemplateUsageByChargeCode(String chargeCode) {
        ServiceChargeTemplateUsage result = null;
        for (ServiceChargeTemplateUsage sctr : serviceUsageCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateUsage> getServiceUsageCharges() {
        return serviceUsageCharges;
    }

    public void setServiceUsageCharges(List<ServiceChargeTemplateUsage> serviceUsageCharges) {
        this.serviceUsageCharges = serviceUsageCharges;
    }
}
