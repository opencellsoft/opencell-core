package org.meveo.apiv2.billing;

import java.io.Serializable;
import java.util.List;

import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.CounterPeriodDto;
import org.meveo.api.dto.response.BaseResponse;

public class ProcessCdrListResult extends BaseResponse {

    private static final long serialVersionUID = 5503067125480351868L;

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
}