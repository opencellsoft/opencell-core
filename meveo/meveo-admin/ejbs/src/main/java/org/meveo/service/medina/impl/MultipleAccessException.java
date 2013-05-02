package org.meveo.service.medina.impl;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class MultipleAccessException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public MultipleAccessException() {
		super(CDRRejectionCauseEnum.INVALID_ACCESS);
	}

	public MultipleAccessException(String message) {
		super(CDRRejectionCauseEnum.INVALID_ACCESS,message);
	}
}
