package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class QuoteProductException extends Exception {

	private String msgError;
	
	public QuoteProductException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public QuoteProductException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public QuoteProductException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
