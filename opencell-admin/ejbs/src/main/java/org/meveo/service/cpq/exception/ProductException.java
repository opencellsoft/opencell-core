package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class ProductException extends Exception {

	private String msgError;
	
	public ProductException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public ProductException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public ProductException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
