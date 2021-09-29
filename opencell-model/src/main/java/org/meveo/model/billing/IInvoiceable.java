package org.meveo.model.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.RoundingModeEnum;

/**
 * The minimum information needed to calculate invoice aggregate and invoice amounts
 * 
 * @author Andrius Karpavicius
 *
 */
public interface IInvoiceable {

    public Long getId();

    public Long getBillingAccountId();

    public Long getSellerId();

    public Long getSubscriptionId();

    public Long getUserAccountId();

    public Long getWalletId();

    public Long getInvoiceSubCategoryId();

    public BigDecimal getUnitAmountWithoutTax();

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax);

    public BigDecimal getUnitAmountWithTax();

    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax);

    public BigDecimal getUnitAmountTax();

    public void setUnitAmountTax(BigDecimal unitAmountTax);

    public BigDecimal getAmountWithoutTax();

    public void setAmountWithoutTax(BigDecimal amountWithoutTax);

    public BigDecimal getAmountWithTax();

    public void setAmountWithTax(BigDecimal amountWithTax);

    public BigDecimal getAmountTax();

    public void setAmountTax(BigDecimal amountTax);

    public Long getTaxId();
    
    public Tax getTax();

    public void setTax(Tax tax);

    public BigDecimal getTaxPercent();

    public void setTaxPercent(BigDecimal taxPercent);

    public Long getTaxClassId();

    public boolean isPrepaid();

    public default boolean isTaxOverriden() {
        return getTaxClassId() == null;
    }

    public void setTaxRecalculated(boolean taxRecalculated);

    public String getOrderNumber();

    /**
     * Recompute derived amounts amountWithoutTax/amountWithTax/amountTax unitAmountWithoutTax/unitAmountWithTax/unitAmountTax
     * 
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     */
    public default void computeDerivedAmounts(boolean isEnterprise, int rounding, RoundingModeEnum roundingMode) {

        if ((isEnterprise && getUnitAmountWithoutTax() != null) || (!isEnterprise && getUnitAmountWithTax() != null)) {
            // Unit amount calculation is left with higher precision
            BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(getUnitAmountWithoutTax(), getUnitAmountWithTax(), getTaxPercent(), isEnterprise, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
            setUnitAmountWithoutTax(amounts[0]);
            setUnitAmountWithTax(amounts[1]);
            setUnitAmountTax(amounts[2]);
        }

        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(getAmountWithoutTax(), getAmountWithTax(), getTaxPercent(), isEnterprise, rounding, roundingMode.getRoundingMode());
        setAmountWithoutTax(amounts[0]);
        setAmountWithTax(amounts[1]);
        setAmountTax(amounts[2]);
    }
}