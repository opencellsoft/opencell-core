package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class ProductVersionException extends Exception {

	private String msgError;
	
	public ProductVersionException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public ProductVersionException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public ProductVersionException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
