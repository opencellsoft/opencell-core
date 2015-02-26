package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetUsageChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUsageChargeTemplateResponse extends BaseResponse {

	private static final long serialVersionUID = 1826809255071018316L;

	private UsageChargeTemplateDto usageChargeTemplate;

	public UsageChargeTemplateDto getUsageChargeTemplate() {
		return usageChargeTemplate;
	}

	public void setUsageChargeTemplate(
			UsageChargeTemplateDto usageChargeTemplate) {
		this.usageChargeTemplate = usageChargeTemplate;
	}

	@Override
	public String toString() {
		return "GetUsageChargeTemplateResponse [usageChargeTemplate=" + usageChargeTemplate + ", toString()="
				+ super.toString() + "]";
	}

}
