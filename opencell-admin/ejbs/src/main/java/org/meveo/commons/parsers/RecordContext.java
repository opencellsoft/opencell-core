package org.meveo.commons.parsers;

public class RecordContext {
    int lineNumber = -1;
    String lineContent = null;
    Object record = null;

    /**
     * When line is rejected, exception of reject
     */
    private Exception rejectReason = null;

    public RecordContext() {

    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the lineContent
     */
    public String getLineContent() {
        return lineContent;
    }

    /**
     * @param lineContent the lineContent to set
     */
    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }

    /**
     * @return the record
     */
    public Object getRecord() {
        return record;
    }

    /**
     * @param record the record to set
     */
    public void setRecord(Object record) {
        this.record = record;
    }

    /**
     * @return When line is rejected, exception of reject
     */
    public Exception getRejectReason() {
        return rejectReason;
    }

    /**
     * @param rejectReason When line is rejected, exception of reject
     */
    public void setRejectReason(Exception rejectReason) {
        this.rejectReason = rejectReason;
    }
}