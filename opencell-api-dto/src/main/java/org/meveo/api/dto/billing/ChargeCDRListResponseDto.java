package org.meveo.api.dto.billing;

import java.io.Serializable;

import org.meveo.api.dto.response.BaseResponse;

public class ChargeCDRListResponseDto extends BaseResponse {

    /**
     * Processing statistics
     */
    private Statistics statistics = null;

    /**
     * Processed CDR information
     */
    private ChargeCDRResponseDto[] chargedCDRs;

    public ChargeCDRListResponseDto() {

    }

    public ChargeCDRListResponseDto(int total) {
        this.statistics = new Statistics(total, 0, 0);
        this.chargedCDRs = new ChargeCDRResponseDto[total];
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