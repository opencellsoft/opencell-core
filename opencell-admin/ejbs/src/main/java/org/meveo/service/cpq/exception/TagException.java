package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class TagException extends Exception {

	private String msgError;
	
	public TagException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public TagException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public TagException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
