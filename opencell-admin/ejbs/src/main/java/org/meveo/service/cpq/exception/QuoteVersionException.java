package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class QuoteVersionException extends Exception {

	private String msgError;
	
	public QuoteVersionException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public QuoteVersionException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public QuoteVersionException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
