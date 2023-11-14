package org.meveo.model.cpq.contract;

import java.io.Serializable;
import java.math.BigDecimal;

public class ContractItemForRating implements Serializable {

    private static final long serialVersionUID = -367910472917780888L;

    /**
     * Price plan Id
     */
    private Long pricePlanId;

    /**
     * Rate
     */
    private Double rate;

    /**
     * Amount without tax
     */
    private BigDecimal amountWithoutTax;

    /**
     * Rate type
     */
    private ContractRateTypeEnum contractRateType = ContractRateTypeEnum.PERCENTAGE;

    /**
     * Shall discount be created as a separate WO
     */
    private boolean separateDiscount = false;

    public ContractItemForRating() {
    }

    public ContractItemForRating(Long pricePlanId, Double rate, BigDecimal amountWithoutTax, ContractRateTypeEnum contractRateType, boolean separateDiscount) {
        super();
        this.pricePlanId = pricePlanId;
        this.rate = rate;
        this.amountWithoutTax = amountWithoutTax;
        this.contractRateType = contractRateType;
        this.separateDiscount = separateDiscount;
    }

    /**
     * @return Price plan Id
     */
    public Long getPricePlanId() {
        return pricePlanId;
    }

    /**
     * @param pricePlanId Price plan Id
     */
    public void setPricePlanId(Long pricePlanId) {
        this.pricePlanId = pricePlanId;
    }

    /**
     * @return Rate
     */
    public Double getRate() {
        return rate;
    }

    /**
     * @param rate Rate
     */
    public void setRate(Double rate) {
        this.rate = rate;
    }

    /**
     * @return Amount without tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * @param amountWithoutTax Amount without tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * @return Rate type
     */
    public ContractRateTypeEnum getContractRateType() {
        return contractRateType;
    }

    /**
     * @param contractRateType Rate type
     */
    public void setContractRateType(ContractRateTypeEnum contractRateType) {
        this.contractRateType = contractRateType;
    }

    /**
     * @return Shall discount be created as a separate WO
     */
    public boolean isSeparateDiscount() {
        return separateDiscount;
    }

    /**
     * @param separateDiscount Shall discount be created as a separate WO
     */
    public void setSeparateDiscount(boolean separateDiscount) {
        this.separateDiscount = separateDiscount;
    }
}