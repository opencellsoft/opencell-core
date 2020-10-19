package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class ProductLineException extends Exception {

	private String msgError;
	
	public ProductLineException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public ProductLineException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public ProductLineException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
