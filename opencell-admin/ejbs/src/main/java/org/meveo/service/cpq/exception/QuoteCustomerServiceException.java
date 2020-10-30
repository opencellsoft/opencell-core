package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class QuoteCustomerServiceException extends Exception {

	private String msgError;
	
	public QuoteCustomerServiceException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public QuoteCustomerServiceException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public QuoteCustomerServiceException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
