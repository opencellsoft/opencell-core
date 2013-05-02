package org.meveo.service.medina.impl;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class DuplicateException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public DuplicateException() {
		super(CDRRejectionCauseEnum.DUPLICATE);
	}

	public DuplicateException(String message) {
		super(CDRRejectionCauseEnum.DUPLICATE,message);
	}
}
