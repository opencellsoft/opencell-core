package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;

/**
 * The Class ProductChargeInstanceDto.
 *
 * @author Edward P. Legaspi
 */
public class ProductChargeInstanceDto extends ChargeInstanceDto {

    /** The quantity. */
    protected BigDecimal quantity = BigDecimal.ONE;

    /**
     * Instantiates a new product charge instance dto.
     *
     * @param productChargeInstance the ProductChargeInstance entity
     */
    public ProductChargeInstanceDto(ProductChargeInstance productChargeInstance) {
        super((ChargeInstance) productChargeInstance);
        if (productChargeInstance != null) {
            quantity = productChargeInstance.getQuantity();
        }
    }

}