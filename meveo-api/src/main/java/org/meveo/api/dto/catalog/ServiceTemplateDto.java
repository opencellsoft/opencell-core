package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "ServiceTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceTemplateDto implements Serializable {

	private static final long serialVersionUID = -6794700715161690227L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = true)
	private String description;

	private ServiceChargeTemplateRecurringsDto serviceChargeTemplateRecurrings;
	private ServiceChargeTemplateSubscriptionsDto serviceChargeTemplateSubscriptions;
	private ServiceChargeTemplateTerminationsDto serviceChargeTemplateTerminations;
	private ServiceChargeTemplateUsagesDto serviceChargeTemplateUsages;

	public ServiceTemplateDto() {
	}

	public ServiceTemplateDto(ServiceTemplate serviceTemplate) {
		code = serviceTemplate.getCode();
		description = serviceTemplate.getDescription();

		// set serviceChargeTemplateRecurrings
		if (serviceTemplate.getServiceRecurringCharges().size() > 0) {
			serviceChargeTemplateRecurrings = new ServiceChargeTemplateRecurringsDto();

			for (ServiceChargeTemplateRecurring recCharge : serviceTemplate.getServiceRecurringCharges()) {
				ServiceChargeTemplateRecurringDto serviceChargeTemplateRecurring = new ServiceChargeTemplateRecurringDto();
				serviceChargeTemplateRecurring.setCode(recCharge.getChargeTemplate().getCode());

				for (WalletTemplate wallet : recCharge.getWalletTemplates()) {
					serviceChargeTemplateRecurring.getWallets().getWallet().add(wallet.getCode());
				}

				serviceChargeTemplateRecurrings.getServiceChargeTemplateRecurring().add(serviceChargeTemplateRecurring);
			}
		}

		// set serviceChargeTemplateSubscriptions
		if (serviceTemplate.getServiceSubscriptionCharges().size() > 0) {
			serviceChargeTemplateSubscriptions = new ServiceChargeTemplateSubscriptionsDto();

			for (ServiceChargeTemplateSubscription subCharge : serviceTemplate.getServiceSubscriptionCharges()) {
				ServiceChargeTemplateSubscriptionDto serviceChargeTemplateSubscription = new ServiceChargeTemplateSubscriptionDto();
				serviceChargeTemplateSubscription.setCode(subCharge.getChargeTemplate().getCode());

				for (WalletTemplate wallet : subCharge.getWalletTemplates()) {
					serviceChargeTemplateSubscription.getWallets().getWallet().add(wallet.getCode());
				}

				serviceChargeTemplateSubscriptions.getServiceChargeTemplateSubscription().add(
						serviceChargeTemplateSubscription);
			}
		}

		// set serviceChargeTemplateTerminations
		if (serviceTemplate.getServiceTerminationCharges().size() > 0) {
			serviceChargeTemplateTerminations = new ServiceChargeTemplateTerminationsDto();

			for (ServiceChargeTemplateTermination terminationCharge : serviceTemplate.getServiceTerminationCharges()) {
				ServiceChargeTemplateTerminationDto serviceChargeTemplateTermination = new ServiceChargeTemplateTerminationDto();
				serviceChargeTemplateTermination.setCode(terminationCharge.getChargeTemplate().getCode());

				for (WalletTemplate wallet : terminationCharge.getWalletTemplates()) {
					serviceChargeTemplateTermination.getWallets().getWallet().add(wallet.getCode());
				}

				serviceChargeTemplateTerminations.getServiceChargeTemplateTermination().add(
						serviceChargeTemplateTermination);
			}

		}

		// add serviceChargeTemplateUsages

		if (serviceTemplate.getServiceUsageCharges().size() > 0) {
			serviceChargeTemplateUsages = new ServiceChargeTemplateUsagesDto();

			for (ServiceChargeTemplateUsage usageCharge : serviceTemplate.getServiceUsageCharges()) {
				ServiceUsageChargeTemplateDto serviceUsageChargeTemplate = new ServiceUsageChargeTemplateDto();
				serviceUsageChargeTemplate.setCode(usageCharge.getChargeTemplate().getCode());

				if (usageCharge.getCounterTemplate() != null) {
					serviceUsageChargeTemplate.setCounterTemplate(usageCharge.getCounterTemplate().getCode());
				}

				for (WalletTemplate wallet : usageCharge.getWalletTemplates()) {
					serviceUsageChargeTemplate.getWallets().getWallet().add(wallet.getCode());
				}

				serviceChargeTemplateUsages.getServiceChargeTemplateUsage().add(serviceUsageChargeTemplate);
			}
		}

	}

	public ServiceTemplateDto(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceChargeTemplateRecurringsDto getServiceChargeTemplateRecurrings() {
		return serviceChargeTemplateRecurrings;
	}

	public void setServiceChargeTemplateRecurrings(ServiceChargeTemplateRecurringsDto serviceChargeTemplateRecurrings) {
		this.serviceChargeTemplateRecurrings = serviceChargeTemplateRecurrings;
	}

	public ServiceChargeTemplateSubscriptionsDto getServiceChargeTemplateSubscriptions() {
		return serviceChargeTemplateSubscriptions;
	}

	public void setServiceChargeTemplateSubscriptions(
			ServiceChargeTemplateSubscriptionsDto serviceChargeTemplateSubscriptions) {
		this.serviceChargeTemplateSubscriptions = serviceChargeTemplateSubscriptions;
	}

	public ServiceChargeTemplateTerminationsDto getServiceChargeTemplateTerminations() {
		return serviceChargeTemplateTerminations;
	}

	public void setServiceChargeTemplateTerminations(
			ServiceChargeTemplateTerminationsDto serviceChargeTemplateTerminations) {
		this.serviceChargeTemplateTerminations = serviceChargeTemplateTerminations;
	}

	public ServiceChargeTemplateUsagesDto getServiceChargeTemplateUsages() {
		return serviceChargeTemplateUsages;
	}

	public void setServiceChargeTemplateUsages(ServiceChargeTemplateUsagesDto serviceChargeTemplateUsages) {
		this.serviceChargeTemplateUsages = serviceChargeTemplateUsages;
	}

	@Override
	public String toString() {
		return "ServiceTemplateDto [code=" + code + ", description=" + description
				+ ", serviceChargeTemplateRecurrings=" + serviceChargeTemplateRecurrings
				+ ", serviceChargeTemplateSubscriptions=" + serviceChargeTemplateSubscriptions
				+ ", serviceChargeTemplateTerminations=" + serviceChargeTemplateTerminations
				+ ", serviceChargeTemplateUsages=" + serviceChargeTemplateUsages + "]";
	}
}
