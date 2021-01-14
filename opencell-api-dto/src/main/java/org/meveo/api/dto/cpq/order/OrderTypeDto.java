package org.meveo.api.dto.cpq.order;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.commercial.OrderType;

@SuppressWarnings("serial")
public class OrderTypeDto extends BusinessEntityDto {

	
	public OrderTypeDto() {
	}

	public OrderTypeDto(OrderType order) {
		this.code = order.getCode();
		this.description = order.getDescription();
	}
	
	
	
}
