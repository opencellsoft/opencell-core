package org.meveo.apiv2.billing.service;

import javax.ejb.ApplicationException;

import org.meveo.apiv2.billing.CdrListResult;

@ApplicationException(rollback = true)
public class RollbackOnErrorException extends RuntimeException {

    private static final long serialVersionUID = -2412839308300692994L;

    private CdrListResult cdrListResult;

    public RollbackOnErrorException(CdrListResult cdrListResult) {
        super();
        this.cdrListResult = cdrListResult;
    }

    public CdrListResult getCdrListResult() {
        return cdrListResult;
    }
}
