package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class ContractException extends Exception {

	private String msgError;
	
	public ContractException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public ContractException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public ContractException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
