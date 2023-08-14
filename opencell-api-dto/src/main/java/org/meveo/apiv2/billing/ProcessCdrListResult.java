package org.meveo.apiv2.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.CounterPeriodDto;
import org.meveo.api.dto.response.BaseResponse;

public class ProcessCdrListResult extends BaseResponse {

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
    private ProcessCdrListModeEnum mode;

    /**
     * Processing statistics
     */
    private Statistics statistics = null;

    /**
     * Processed CDR information
     */
    private ChargeCDRResponseDto[] chargedCDRs;

    /**
     * Counter periods that were updated during the rating
     */
    private List<CounterPeriodDto> counterPeriods;

    public ProcessCdrListResult() {

    }

    public ProcessCdrListResult(ProcessCdrListModeEnum mode, int total) {
        this.mode = mode;
        this.statistics = new Statistics(total, 0, 0);
        this.chargedCDRs = new ChargeCDRResponseDto[total];
        this.amountWithTax = BigDecimal.ZERO;
        this.amountWithoutTax = BigDecimal.ZERO;
        this.amountTax = BigDecimal.ZERO;
        this.walletOperationCount = 0;
    }

    /**
     * @return Processing mode
     */
    public ProcessCdrListModeEnum getMode() {
        return mode;
    }

    /**
     * @param mode Processing mode
     */
    public void setMode(ProcessCdrListModeEnum mode) {
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
    public ChargeCDRResponseDto[] getChargedCDRs() {
        return chargedCDRs;
    }

    public void setChargedCDRs(ChargeCDRResponseDto[] chargedCDRs) {
        this.chargedCDRs = chargedCDRs;
    }

    public void addChargedCdr(int position, ChargeCDRResponseDto chargedCdr) {

        chargedCDRs[position] = chargedCdr;
    }

    /**
     * @return Counter periods that were updated during the rating
     */
    public List<CounterPeriodDto> getCounterPeriods() {
        return counterPeriods;
    }

    /**
     * @param counterPeriods Counter periods that were updated during the rating
     */
    public void setCounterPeriods(List<CounterPeriodDto> counterPeriods) {
        this.counterPeriods = counterPeriods;
    }

    /**
     * Update total amounts and wallet operation count based on charged CDRs
     */
    public void updateAmountAndWOCountStatistics() {
        for (ChargeCDRResponseDto cdrCharge : getChargedCDRs()) {
            if (cdrCharge == null) {
                continue;
            }
            setAmountWithTax(getAmountWithTax().add(cdrCharge.getAmountWithTax() != null ? cdrCharge.getAmountWithTax() : BigDecimal.ZERO));
            setAmountWithoutTax(getAmountWithoutTax().add(cdrCharge.getAmountWithoutTax() != null ? cdrCharge.getAmountWithoutTax() : BigDecimal.ZERO));
            setAmountTax(getAmountTax().add(cdrCharge.getAmountTax() != null ? cdrCharge.getAmountTax() : BigDecimal.ZERO));
            setWalletOperationCount(getWalletOperationCount() + (cdrCharge.getWalletOperationCount() != null ? cdrCharge.getWalletOperationCount() : 0));
        }
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