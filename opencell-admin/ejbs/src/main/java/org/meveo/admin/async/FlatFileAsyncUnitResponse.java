/**
 * 
 */
package org.meveo.admin.async;

/**
 * @author anasseh
 *
 */
public class FlatFileAsyncUnitResponse {
    private String lineRecord;
    private String reason;
    private long lineNumber =0;
    private boolean success  = false;
    
    public FlatFileAsyncUnitResponse() {
        
    }
    
    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    
    /**
     * @return the lineRecord
     */
    public String getLineRecord() {
        return lineRecord;
    }

    /**
     * @param lineRecord the lineRecord to set
     */
    public void setLineRecord(String lineRecord) {
        this.lineRecord = lineRecord;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
   
    
}
