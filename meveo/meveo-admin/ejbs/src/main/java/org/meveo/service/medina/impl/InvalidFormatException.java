package org.meveo.service.medina.impl;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class InvalidFormatException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public InvalidFormatException() {
		super(CDRRejectionCauseEnum.INVALID_FORMAT);
	}

	public InvalidFormatException(String message) {
		super(CDRRejectionCauseEnum.INVALID_FORMAT,message);
	}
}
