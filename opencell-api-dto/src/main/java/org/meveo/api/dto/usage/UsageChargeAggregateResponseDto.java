package org.meveo.api.dto.usage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "UsageChargeAggregateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageChargeAggregateResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElementWrapper
    @XmlElement(name="chargeAggregate")
	List<ChargeAggregateDto> listChargeAggregate = new ArrayList<ChargeAggregateDto>();

	public UsageChargeAggregateResponseDto() {

	}

	/**
	 * @return the listChargeAggregate
	 */
	public List<ChargeAggregateDto> getListChargeAggregate() {
		return listChargeAggregate;
	}

	/**
	 * @param listChargeAggregate the listChargeAggregate to set
	 */
	public void setListChargeAggregate(List<ChargeAggregateDto> listChargeAggregate) {
		this.listChargeAggregate = listChargeAggregate;
	}
	
	
}
