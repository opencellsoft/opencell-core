package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	private List<HashMap<String, List<String>>> serviceChargeTemplateRecurrings;
	private List<HashMap<String, List<String>>> serviceChargeTemplateSubscriptions;
	private List<HashMap<String, List<String>>> serviceChargeTemplateTerminations;
	private List<ServiceUsageChargeTemplateDto> serviceChargeTemplateUsages;

	public ServiceTemplateDto() {
	}

	public ServiceTemplateDto(ServiceTemplate serviceTemplate) {
		code = serviceTemplate.getCode();
		description = serviceTemplate.getDescription();

		// set serviceChargeTemplateRecurrings
		if (serviceTemplate.getServiceRecurringCharges().size() > 0) {
			List<String> walletsRecu = null;
			serviceChargeTemplateRecurrings = new ArrayList<HashMap<String, List<String>>>();
			HashMap<String, List<String>> recurringList = new HashMap<String, List<String>>();
			for (ServiceChargeTemplateRecurring recurring : serviceTemplate
					.getServiceRecurringCharges()) {
				walletsRecu = new ArrayList<String>();
				for (WalletTemplate wallet : recurring.getWalletTemplates()) {
					walletsRecu.add(wallet.getCode());
				}
				recurringList.put(recurring.getChargeTemplate().getCode(),
						walletsRecu);
			}
			serviceChargeTemplateRecurrings.add(recurringList);
		}

		// set serviceChargeTemplateSubscriptions
		if (serviceTemplate.getServiceSubscriptionCharges().size() > 0) {
			List<String> walletsSub = null;
			HashMap<String, List<String>> servSubsList = new HashMap<String, List<String>>();
			serviceChargeTemplateSubscriptions = new ArrayList<HashMap<String, List<String>>>();
			for (ServiceChargeTemplateSubscription subs : serviceTemplate
					.getServiceSubscriptionCharges()) {
				walletsSub = new ArrayList<String>();
				for (WalletTemplate wallet : subs.getWalletTemplates()) {
					walletsSub.add(wallet.getCode());
				}
				servSubsList
						.put(subs.getChargeTemplate().getCode(), walletsSub);
			}
			serviceChargeTemplateSubscriptions.add(servSubsList);
		}

		// set serviceChargeTemplateTerminations
		if (serviceTemplate.getServiceTerminationCharges().size() > 0) {
			serviceChargeTemplateTerminations = new ArrayList<HashMap<String, List<String>>>();
			List<String> walletsTerms = null;
			HashMap<String, List<String>> servTermList = new HashMap<String, List<String>>();
			for (ServiceChargeTemplateTermination servTerms : serviceTemplate
					.getServiceTerminationCharges()) {
				walletsTerms = new ArrayList<String>();
				for (WalletTemplate wallet : servTerms.getWalletTemplates()) {
					walletsTerms.add(wallet.getCode());
				}
				servTermList.put(servTerms.getChargeTemplate().getCode(),
						walletsTerms);
			}
			serviceChargeTemplateTerminations.add(servTermList);
		}

		// add serviceChargeTemplateUsages

		if (serviceTemplate.getServiceUsageCharges().size() > 0) {
			List<String> walletsUsage = null;
			serviceChargeTemplateUsages = new ArrayList<ServiceUsageChargeTemplateDto>();
			ServiceUsageChargeTemplateDto usageDto = null;
			for (ServiceChargeTemplateUsage usageSerTemp : serviceTemplate
					.getServiceUsageCharges()) {
				usageDto = new ServiceUsageChargeTemplateDto();
				usageDto.setUsageChargeTemplate(usageSerTemp
						.getChargeTemplate().getCode());
				if (usageSerTemp.getCounterTemplate() != null) {
					usageDto.setCounterTemplate(usageSerTemp
							.getCounterTemplate().getCode());
				}
				walletsUsage = new ArrayList<String>();
				for (WalletTemplate wallet : usageSerTemp.getWalletTemplates()) {
					walletsUsage.add(wallet.getCode());
				}
				usageDto.setWalletTemplates(walletsUsage);
				serviceChargeTemplateUsages.add(usageDto);
			}
		}

	}

	public List<HashMap<String, List<String>>> getServiceChargeTemplateSubscriptions() {
		return serviceChargeTemplateSubscriptions;
	}

	public void setServiceChargeTemplateSubscriptions(
			List<HashMap<String, List<String>>> serviceChargeTemplateSubscriptions) {
		this.serviceChargeTemplateSubscriptions = serviceChargeTemplateSubscriptions;
	}

	public List<HashMap<String, List<String>>> getServiceChargeTemplateTerminations() {
		return serviceChargeTemplateTerminations;
	}

	public void setServiceChargeTemplateTerminations(
			List<HashMap<String, List<String>>> serviceChargeTemplateTerminations) {
		this.serviceChargeTemplateTerminations = serviceChargeTemplateTerminations;
	}

	@Override
	public String toString() {
		return "ServiceTemplateDto [code=" + code + ", description="
				+ description + ", serviceChargeTemplateRecurrings="
				+ serviceChargeTemplateRecurrings
				+ ", serviceChargeTemplateSubscriptions="
				+ serviceChargeTemplateSubscriptions
				+ ", serviceChargeTemplateTerminations="
				+ serviceChargeTemplateTerminations
				+ ", serviceChargeTemplateUsages="
				+ serviceChargeTemplateUsages + "]";
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

	public List<HashMap<String, List<String>>> getServiceChargeTemplateRecurrings() {
		return serviceChargeTemplateRecurrings;
	}

	public void setServiceChargeTemplateRecurrings(
			List<HashMap<String, List<String>>> serviceChargeTemplateRecurrings) {
		this.serviceChargeTemplateRecurrings = serviceChargeTemplateRecurrings;
	}

	public List<ServiceUsageChargeTemplateDto> getServiceChargeTemplateUsages() {
		return serviceChargeTemplateUsages;
	}

	public void setServiceChargeTemplateUsages(
			List<ServiceUsageChargeTemplateDto> serviceChargeTemplateUsages) {
		this.serviceChargeTemplateUsages = serviceChargeTemplateUsages;
	}

}
