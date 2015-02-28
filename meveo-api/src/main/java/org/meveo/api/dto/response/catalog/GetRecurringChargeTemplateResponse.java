package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetRecurringChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetRecurringChargeTemplateResponse extends BaseResponse {

	private static final long serialVersionUID = -2699333443036516206L;

	private RecurringChargeTemplateDto recurringChargeTemplate;

	public RecurringChargeTemplateDto getRecurringChargeTemplate() {
		return recurringChargeTemplate;
	}

	public void setRecurringChargeTemplate(
			RecurringChargeTemplateDto recurringChargeTemplate) {
		this.recurringChargeTemplate = recurringChargeTemplate;
	}

	@Override
	public String toString() {
		return "GetRecurringChargeTemplateResponse [recurringChargeTemplate=" + recurringChargeTemplate
				+ ", toString()=" + super.toString() + "]";
	}

}
