package org.meveo.admin.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class JobExecutionException extends ValidationException {

    private static final long serialVersionUID = -8124433270643605981L;

    public JobExecutionException() {
        super();
    }

    public JobExecutionException(String message) {
        super(message);
    }
}