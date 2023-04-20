package org.meveo.apiv2.accounts;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.api.dto.response.BaseResponse;

public class ProcessApplyChargeListResult extends BaseResponse {

    private static final long serialVersionUID = 5503067125480351868L;

    /**
     * The amount without Tax.
     */
    private BigDecimal amountWithoutTax;

    /**
     * The tax amount.
     */
    private BigDecimal amountTax;

    /**
     * The amount with tax.
     */
    private BigDecimal amountWithTax;

    /**
     * The call wallet Operation Count.
     */
    private Integer walletOperationCount;

    /**
     * Processing mode
     */
    private ApplyOneShotChargeListModeEnum mode;

    /**
     * Processing statistics
     */
    private Statistics statistics = null;

    /**
     * Processed CDR information
     */
    private AppliedChargeResponseDto[] appliedCharges;

    public ProcessApplyChargeListResult() {

    }

    public ProcessApplyChargeListResult(ApplyOneShotChargeListModeEnum mode, int total) {
        this.mode = mode;
        this.statistics = new Statistics(total, 0, 0);
        this.appliedCharges = new AppliedChargeResponseDto[total];
    }

    /**
     * @return Processing mode
     */
    public ApplyOneShotChargeListModeEnum getMode() {
        return mode;
    }

    /**
     * @param mode Processing mode
     */
    public void setMode(ApplyOneShotChargeListModeEnum mode) {
        this.mode = mode;
    }

    /**
     * @return Processing statistics
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * @param statistics Processing statistics
     */
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    /**
     * @return Processed CDR information
     */
    public AppliedChargeResponseDto[] getAppliedCharges() {
    	return appliedCharges;
    }
    
    public void setAppliedCharges(AppliedChargeResponseDto[] appliedCharges) {
    	this.appliedCharges = appliedCharges;
    }

    public void addAppliedCharge(int position, AppliedChargeResponseDto appliedCharge) {
    	appliedCharges[position] = appliedCharge;
    }

    public static class Statistics implements Serializable {
        private static final long serialVersionUID = -199653879663048902L;

        private int total;
        private int success;
        private int fail;

        public Statistics(int total, int success, int fail) {
            super();
            this.total = total;
            this.success = success;
            this.fail = fail;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public int getFail() {
            return fail;
        }

        public void setFail(int fail) {
            this.fail = fail;
        }

        public synchronized void addSuccess() {
            this.success++;
        }

        public synchronized void addFail() {
            this.fail++;
        }
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public Integer getWalletOperationCount() {
        return walletOperationCount;
    }

    public void setWalletOperationCount(Integer walletOperationCount) {
        this.walletOperationCount = walletOperationCount;
    }
}