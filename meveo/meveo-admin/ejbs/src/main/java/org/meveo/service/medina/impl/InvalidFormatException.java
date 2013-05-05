package org.meveo.service.medina.impl;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class InvalidFormatException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public InvalidFormatException(String cdrString) {
		super(cdrString,CDRRejectionCauseEnum.INVALID_FORMAT);
	}

	public InvalidFormatException(String cdrString,String message) {
		super(cdrString,CDRRejectionCauseEnum.INVALID_FORMAT,message);
	}
}
