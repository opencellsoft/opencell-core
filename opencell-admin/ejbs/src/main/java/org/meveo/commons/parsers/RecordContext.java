package org.meveo.commons.parsers;

public class RecordContext {
	int lineNumber = -1;
	String lineContent = null;
	Object record = null;
	//When line rejected, reason of reject
	String reason = null;

	public RecordContext() {

	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber
	 *            the lineNumber to set
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
	 * @param lineContent
	 *            the lineContent to set
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
	 * @param record
	 *            the record to set
	 */
	public void setRecord(Object record) {
		this.record = record;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	

}
