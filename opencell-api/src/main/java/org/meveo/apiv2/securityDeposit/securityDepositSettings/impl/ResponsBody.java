package org.meveo.apiv2.securityDeposit.securityDepositSettings.impl;

public class ResponsBody <T>{
    T entity;
    ActionStatus actionStatus;

    private ResponsBody(T entity, ActionStatus actionStatus) {
        this.entity = entity;
        this.actionStatus = actionStatus;
    }

    public static <T> ResponsBody<T> of(T entity, ActionStatus actionStatus)
    {
        return new ResponsBody<>(entity, actionStatus);
    }
}

class ActionStatus {

    private String status;
    private String message;

    private ActionStatus() {
    }

    private ActionStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ActionStatus of(String status, String message)
    {
       return new ActionStatus(status, message);
    }
}
