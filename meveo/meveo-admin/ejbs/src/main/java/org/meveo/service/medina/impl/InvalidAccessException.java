package org.meveo.service.medina.impl;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class InvalidAccessException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public InvalidAccessException() {
		super(CDRRejectionCauseEnum.INVALID_ACCESS);
	}

	public InvalidAccessException(String message) {
		super(CDRRejectionCauseEnum.INVALID_ACCESS,message);
	}
}
