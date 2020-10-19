package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class TagTypeException extends Exception {

	private String msgError;
	
	public TagTypeException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public TagTypeException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public TagTypeException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
