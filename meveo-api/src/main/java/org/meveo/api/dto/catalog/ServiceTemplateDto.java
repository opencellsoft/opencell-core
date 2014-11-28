package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.ServiceUsageChargeTemplate;

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

	private List<String> recurringCharges;
	private List<String> subscriptionCharges;
	private List<String> terminationCharges;
	private List<ServiceUsageChargeTemplateDto> serviceUsageCharges;

	public ServiceTemplateDto() {

	}

	public ServiceTemplateDto(ServiceTemplate e) {
		code = e.getCode();
		description = e.getDescription();

		if (e.getRecurringCharges().size() > 0) {
			recurringCharges = new ArrayList<String>();
			for (RecurringChargeTemplate rt : e.getRecurringCharges()) {
				recurringCharges.add(rt.getCode());
			}
		}
		if (e.getSubscriptionCharges().size() > 0) {
			subscriptionCharges = new ArrayList<String>();
			for (OneShotChargeTemplate ot : e.getSubscriptionCharges()) {
				subscriptionCharges.add(ot.getCode());
			}
		}
		if (e.getTerminationCharges().size() > 0) {
			terminationCharges = new ArrayList<String>();
			for (OneShotChargeTemplate ot : e.getTerminationCharges()) {
				terminationCharges.add(ot.getCode());
			}
		}
		if (e.getServiceUsageCharges().size() > 0) {
			serviceUsageCharges = new ArrayList<ServiceUsageChargeTemplateDto>();
			for (ServiceUsageChargeTemplate ot : e.getServiceUsageCharges()) {
				ServiceUsageChargeTemplateDto serviceUsageChargeTemplateDto = new ServiceUsageChargeTemplateDto();
				serviceUsageChargeTemplateDto.setUsageChargeTemplate(ot
						.getChargeTemplate().getCode());
				if (ot.getCounterTemplate() != null) {
					serviceUsageChargeTemplateDto.setCounterTemplate(ot
							.getCounterTemplate().getCode());
				}
				serviceUsageCharges.add(serviceUsageChargeTemplateDto);
			}
		}
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

	public List<String> getRecurringCharges() {
		return recurringCharges;
	}

	public void setRecurringCharges(List<String> recurringCharges) {
		this.recurringCharges = recurringCharges;
	}

	public List<String> getSubscriptionCharges() {
		return subscriptionCharges;
	}

	public void setSubscriptionCharges(List<String> subscriptionCharges) {
		this.subscriptionCharges = subscriptionCharges;
	}

	public List<String> getTerminationCharges() {
		return terminationCharges;
	}

	public void setTerminationCharges(List<String> terminationCharges) {
		this.terminationCharges = terminationCharges;
	}

	public List<ServiceUsageChargeTemplateDto> getServiceUsageCharges() {
		return serviceUsageCharges;
	}

	public void setServiceUsageCharges(
			List<ServiceUsageChargeTemplateDto> serviceUsageCharges) {
		this.serviceUsageCharges = serviceUsageCharges;
	}

	@Override
	public String toString() {
		return "ServiceTemplateDto [code=" + code + ", description="
				+ description + ", recurringCharges=" + recurringCharges
				+ ", subscriptionCharges=" + subscriptionCharges
				+ ", terminationCharges=" + terminationCharges
				+ ", serviceUsageCharges=" + serviceUsageCharges + "]";
	}

}
