package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceUsageChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceUsageChargeTemplateDto extends BaseServiceChargeTemplateDto implements Serializable {

	private static final long serialVersionUID = 1612154476117247213L;

	private String counterTemplate;

	public ServiceUsageChargeTemplateDto() {

	}

	public ServiceUsageChargeTemplateDto(String usageChargeTemplate, String counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	public String getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(String counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	@Override
	public String toString() {
		return "ServiceUsageChargeTemplateDto [counterTemplate=" + counterTemplate + ", getCode()=" + getCode()
				+ ", getWallets()=" + getWallets() + "]";
	}

}
