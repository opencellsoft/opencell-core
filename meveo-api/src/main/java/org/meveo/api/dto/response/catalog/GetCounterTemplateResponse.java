package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetCounterTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCounterTemplateResponse extends BaseResponse {

	private static final long serialVersionUID = 4612709775410582280L;

	private CounterTemplateDto counterTemplate;

	public CounterTemplateDto getCounterTemplate() {
		return counterTemplate;
	}

	public void setCounterTemplate(CounterTemplateDto counterTemplate) {
		this.counterTemplate = counterTemplate;
	}

	@Override
	public String toString() {
		return "GetCounterTemplateResponse [counterTemplate=" + counterTemplate + ", toString()=" + super.toString()
				+ "]";
	}

}
