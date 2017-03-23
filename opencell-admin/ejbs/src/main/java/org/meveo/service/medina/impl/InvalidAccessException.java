package org.meveo.service.medina.impl;

import java.io.Serializable;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class InvalidAccessException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public InvalidAccessException(Serializable cdr) {
		super(cdr,CDRRejectionCauseEnum.INVALID_ACCESS);
	}

	public InvalidAccessException(Serializable cdr,String message) {
		super(cdr,CDRRejectionCauseEnum.INVALID_ACCESS,message);
	}
}
