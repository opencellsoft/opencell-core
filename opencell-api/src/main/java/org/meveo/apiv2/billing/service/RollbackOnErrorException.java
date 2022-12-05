package org.meveo.apiv2.billing.service;

import jakarta.ejb.ApplicationException;

import org.meveo.apiv2.billing.ProcessCdrListResult;

@ApplicationException(rollback = true)
public class RollbackOnErrorException extends RuntimeException {

    private static final long serialVersionUID = -2412839308300692994L;

    private ProcessCdrListResult cdrListResult;

    public RollbackOnErrorException(ProcessCdrListResult cdrListResult) {
        super();
        this.cdrListResult = cdrListResult;
    }

    public ProcessCdrListResult getCdrListResult() {
        return cdrListResult;
    }
}
