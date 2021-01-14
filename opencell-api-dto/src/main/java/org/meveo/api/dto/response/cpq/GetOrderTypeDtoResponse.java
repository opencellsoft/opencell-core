package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.order.OrderTypeDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Rachid.AITYAAZZA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetOrderTypeDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOrderTypeDtoResponse extends BaseResponse{

	/**
	 * order type data
	 */
	private OrderTypeDto orderTypeDto;

	/**
	 * @return the orderTypeDto
	 */
	public OrderTypeDto getOrderTypeDto() {
		return orderTypeDto;
	}

	/**
	 * @param orderTypeDto the orderTypeDto to set
	 */
	public void setOrderTypeDto(OrderTypeDto orderTypeDto) {
		this.orderTypeDto = orderTypeDto;
	}
	
	
}
