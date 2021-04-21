package org.meveo.api.dto.cpq.order;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.commercial.OrderType;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("serial")
public class OrderTypeDto extends BusinessEntityDto {

	
	public OrderTypeDto() {
	}

	public OrderTypeDto(OrderType order) {
		this.code = order.getCode();
		this.description = order.getDescription();
	}
	
	@Schema(description = "code of the order type")
	@Override
	public String getCode() {
		return super.getCode();
	}
	
	@Schema(description = "description of order type")
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return super.getDescription();
	}
	
	
}
