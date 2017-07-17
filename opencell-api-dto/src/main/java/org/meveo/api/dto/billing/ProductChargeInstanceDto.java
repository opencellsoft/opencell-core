package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;

/**
 * @author Edward P. Legaspi
 **/
public class ProductChargeInstanceDto extends ChargeInstanceDto {

	protected BigDecimal quantity = BigDecimal.ONE;

	public ProductChargeInstanceDto(ProductChargeInstance e) {
		super((ChargeInstance) e);
		if (e != null) {
			quantity = e.getQuantity();
		}
	}

}
