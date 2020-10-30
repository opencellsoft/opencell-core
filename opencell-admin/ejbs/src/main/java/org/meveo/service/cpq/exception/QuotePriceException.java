package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class QuotePriceException extends Exception {

	private String msgError;
	
	public QuotePriceException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public QuotePriceException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public QuotePriceException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
