package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "servicePlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicePricePlanDto extends BaseDto {

	private static final long serialVersionUID = 7795578557364046777L;

	private String serviceId;
	private String organizationId;
	private String taxId;
	private Boolean subscriptionProrata;
	private Boolean terminationProrata;
	private Boolean applyInAdvance;
	private String param1;
	private String param2;
	private String param3;
	private Integer billingPeriod;
	private List<RecurringChargeDto> recurringCharges;
	private String usageUnit;
	private List<UsageChargeDto> usageCharges;
	private List<SubscriptionFeeDto> subscriptionFees;
	private List<TerminationFeeDto> terminationFees;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public Boolean getSubscriptionProrata() {
		return subscriptionProrata;
	}

	public void setSubscriptionProrata(Boolean subscriptionProrata) {
		this.subscriptionProrata = subscriptionProrata;
	}

	public Boolean getTerminationProrata() {
		return terminationProrata;
	}

	public void setTerminationProrata(Boolean terminationProrata) {
		this.terminationProrata = terminationProrata;
	}

	public Boolean getApplyInAdvance() {
		return applyInAdvance;
	}

	public void setApplyInAdvance(Boolean applyInAdvance) {
		this.applyInAdvance = applyInAdvance;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public Integer getBillingPeriod() {
		return billingPeriod;
	}

	public void setBillingPeriod(Integer billingPeriod) {
		this.billingPeriod = billingPeriod;
	}

	public List<RecurringChargeDto> getRecurringCharges() {
		return recurringCharges;
	}

	public void setRecurringCharges(List<RecurringChargeDto> recurringCharges) {
		this.recurringCharges = recurringCharges;
	}

	public String getUsageUnit() {
		return usageUnit;
	}

	public void setUsageUnit(String usageUnit) {
		this.usageUnit = usageUnit;
	}

	public List<UsageChargeDto> getUsageCharges() {
		return usageCharges;
	}

	public void setUsageCharges(List<UsageChargeDto> usageCharges) {
		this.usageCharges = usageCharges;
	}

	public List<SubscriptionFeeDto> getSubscriptionFees() {
		return subscriptionFees;
	}

	public void setSubscriptionFees(List<SubscriptionFeeDto> subscriptionFees) {
		this.subscriptionFees = subscriptionFees;
	}

	public List<TerminationFeeDto> getTerminationFees() {
		return terminationFees;
	}

	public void setTerminationFees(List<TerminationFeeDto> terminationFees) {
		this.terminationFees = terminationFees;
	}

}
