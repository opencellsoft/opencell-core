package org.meveo.service.medina.impl;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class CDRParsingException extends Exception {

	private static final long serialVersionUID = 2383961368878309626L;
	
	private CDRRejectionCauseEnum rejectionCause;

	public CDRParsingException(CDRRejectionCauseEnum cause) {
		super();
		setRejectionCause(cause);
	}
	
	public CDRParsingException(CDRRejectionCauseEnum cause,String message) {
		super(message);
		setRejectionCause(cause);
	}

	public CDRRejectionCauseEnum getRejectionCause() {
		return rejectionCause;
	}

	public void setRejectionCause(CDRRejectionCauseEnum rejectionCause) {
		this.rejectionCause = rejectionCause;
	}
	
}
