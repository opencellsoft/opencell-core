package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.order.OrderOfferDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Mbarek-Ay
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetOrderOfferDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOrderOfferDtoResponse extends BaseResponse{

	/**
	 * order offer data
	 */
	private OrderOfferDto orderOfferDto;

	/**
	 * @return the orderOfferDto
	 */
	public OrderOfferDto getOrderOfferDto() {
		return orderOfferDto;
	}

	/**
	 * @param orderOfferDto the orderOfferDto to set
	 */
	public void setOrderOfferDto(OrderOfferDto orderOfferDto) {
		this.orderOfferDto = orderOfferDto;
	}

	 
	
	
}
