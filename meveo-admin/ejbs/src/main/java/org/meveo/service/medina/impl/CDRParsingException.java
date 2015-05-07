package org.meveo.service.medina.impl;

import java.io.Serializable;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class CDRParsingException extends Exception {

	private static final long serialVersionUID = 2383961368878309626L;
	
	private Serializable cdr;
	private CDRRejectionCauseEnum rejectionCause;

	public CDRParsingException(Serializable cdr,CDRRejectionCauseEnum cause) {
        super();
		setCdr(cdr);
		setRejectionCause(cause);
	}
	
	public CDRParsingException(Serializable cdr,CDRRejectionCauseEnum cause,String message) {
		super(message);
		setCdr(cdr);
		setRejectionCause(cause);
	}

	public CDRRejectionCauseEnum getRejectionCause() {
		return rejectionCause;
	}

	public void setRejectionCause(CDRRejectionCauseEnum rejectionCause) {
		this.rejectionCause = rejectionCause;
	}

	public Serializable getCdr() {
		return cdr;
	}

	public void setCdr(Serializable cdr) {
		this.cdr = cdr;
	}

    @Override
    public String getMessage() {
        return "Failed to parse CDR. Reason: " + rejectionCause + " " + super.getMessage();
	}	
}
