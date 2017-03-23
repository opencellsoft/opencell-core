package org.meveo.admin.exception;

public class InsufficientBalanceException extends BusinessException {

    private static final long serialVersionUID = 8152437282691490974L;

    @Override
    public String getMessage() {
        return "INSUFFICIENT_BALANCE";
    }
}