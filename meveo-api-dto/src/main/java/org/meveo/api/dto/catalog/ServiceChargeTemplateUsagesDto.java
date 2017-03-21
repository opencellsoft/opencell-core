package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ServiceChargeTemplateUsages")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateUsagesDto implements Serializable {

	private static final long serialVersionUID = -5175410607345470193L;

	private List<ServiceUsageChargeTemplateDto> serviceChargeTemplateUsage;

	public List<ServiceUsageChargeTemplateDto> getServiceChargeTemplateUsage() {
		if (serviceChargeTemplateUsage == null)
			serviceChargeTemplateUsage = new ArrayList<ServiceUsageChargeTemplateDto>();
		return serviceChargeTemplateUsage;
	}

	public void setServiceChargeTemplateUsage(List<ServiceUsageChargeTemplateDto> serviceChargeTemplateUsage) {
		this.serviceChargeTemplateUsage = serviceChargeTemplateUsage;
	}

	@Override
	public String toString() {
		return "ServiceChargeTemplateUsagesDto [serviceChargeTemplateUsage=" + serviceChargeTemplateUsage + "]";
	}

}
