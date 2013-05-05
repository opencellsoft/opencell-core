package org.meveo.service.medina.impl;

import java.io.Serializable;

import org.meveo.model.mediation.CDRRejectionCauseEnum;

public class DuplicateException extends CDRParsingException {

	private static final long serialVersionUID = 7574354192096751354L;

	public DuplicateException(Serializable cdr) {
		super(cdr,CDRRejectionCauseEnum.DUPLICATE);
	}

	public DuplicateException(Serializable cdr,String message) {
		super(cdr,CDRRejectionCauseEnum.DUPLICATE,message);
	}
}
