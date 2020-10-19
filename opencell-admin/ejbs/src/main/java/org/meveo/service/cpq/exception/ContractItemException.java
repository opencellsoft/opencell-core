package org.meveo.service.cpq.exception;

@SuppressWarnings("serial")
public class ContractItemException extends Exception {

	private String msgError;
	
	public ContractItemException(String msg) {
		super(msg);
		this.msgError = msg;
	}
	
	public ContractItemException(Throwable e) {
		super(e);
		this.msgError = e.getMessage();
	}
	
	public ContractItemException(String msg, Throwable error) {
		super(msg, error);
	}
	
	public String getMsgError() {
		return this.msgError;
	}
	
	
}
