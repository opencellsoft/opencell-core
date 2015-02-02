package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceUsageChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceUsageChargeTemplateDto implements Serializable {

	private static final long serialVersionUID = 1612154476117247213L;

	private String usageChargeTemplate;
	private String counterTemplate;
	private List<String> walletTemplates;
	
	public ServiceUsageChargeTemplateDto() {
		
	}

	public ServiceUsageChargeTemplateDto(String usageChargeTemplate,
			String counterTemplate) {
		this.usageChargeTemplate = usageChargeTemplate;
		this.counterTemplate = counterTemplate;
	}

	public String getUsageChargeTemplate() {
		return usageChargeTemplate;
	}

	public void setUsageChargeTemplate(String usageChargeTemplate) {
		this.usageChargeTemplate = usageChargeTemplate;
	}

	public String getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(String counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	@Override
	public String toString() {
		return "ServiceUsageChargeTemplateDto [usageChargeTemplate="
				+ usageChargeTemplate + ", counterTemplate=" + counterTemplate
				+ "]";
	}

	public List<String> getWalletTemplates() {
		return walletTemplates;
	}

	public void setWalletTemplates(List<String> walletTemplates) {
		this.walletTemplates = walletTemplates;
	}

	
	

}
