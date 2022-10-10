package org.meveo.service.medina.impl;

public class CDRAlreadyProcessedException extends CDRParsingException {

    /**
     * 
     */
    private static final long serialVersionUID = -2842692724622329673L;

    public CDRAlreadyProcessedException(String message) {
        super(message);
    }

}
