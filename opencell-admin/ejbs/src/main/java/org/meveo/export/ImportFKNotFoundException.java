package org.meveo.export;

import java.util.Map;

public class ImportFKNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5406804751974983509L;
    @SuppressWarnings("rawtypes")
    private Class fkEntity;
    private String fkId;
    private Map<String, Object> parameters;
    @SuppressWarnings("rawtypes")
    private Class reason;

    @SuppressWarnings("rawtypes")
    public ImportFKNotFoundException(Class fkEntity, String fkId, Map<String, Object> parameters, Class reason) {
        super();
        this.fkEntity = fkEntity;
        this.fkId = fkId;
        this.parameters = parameters;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "No entity of type " + fkEntity.getName() + " matched. " + (reason != null ? ("Reason: " + reason.getSimpleName()) : "") + " [id=" + fkId + "] attributes "
                + parameters;
    }
}
