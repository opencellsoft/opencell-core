package org.meveo.apiv2.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

public class CdrListResult extends BaseEntityDto {
    private static final long serialVersionUID = 1026307230457632555L;

    /**
     * Processing mode
     */
    private RegisterCdrListModeEnum mode;

    /**
     * Processing statistics
     */
    private Statistics statistics = null;

    /**
     * A list of EDR ids that were created
     */
    private List<Long> edrIds = new ArrayList<>();

    /**
     * CDR processing errors
     */
    private List<CdrError> errors = new ArrayList<>();

    public CdrListResult() {
    }

    public CdrListResult(RegisterCdrListModeEnum mode, int total) {
        this.mode = mode;
        this.statistics = new Statistics(total, 0, 0);
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

    public static class CdrError implements Serializable {
        private static final long serialVersionUID = 5139169395026374653L;

        private String errorCode;
        private String errorMessage;
        private String cdr;

        public CdrError() {

        }

        public CdrError(String errorCode, String errorMessage, String cdr) {
            super();
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.cdr = cdr;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getCdr() {
            return cdr;
        }

        public void setCdr(String cdr) {
            this.cdr = cdr;
        }
    }

    /**
     * @return the mode
     */
    public RegisterCdrListModeEnum getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(RegisterCdrListModeEnum mode) {
        this.mode = mode;
    }

    /**
     * @return the statistics
     */
    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * @param statistics the statistics to set
     */
    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    /**
     * @return the edrIds
     */
    public List<Long> getEdrIds() {
        return edrIds;
    }

    /**
     * @param edrIds the edrIds to set
     */
    public void setEdrIds(List<Long> edrIds) {
        this.edrIds = edrIds;
    }

    /**
     * @return the errors
     */
    public List<CdrError> getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(List<CdrError> errors) {
        this.errors = errors;
    }
}
