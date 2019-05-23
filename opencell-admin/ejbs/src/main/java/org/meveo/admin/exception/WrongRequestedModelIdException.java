package org.meveo.admin.exception;

public class WrongRequestedModelIdException extends GenericApiException{
    public WrongRequestedModelIdException(Long id) {
        super(WrongRequestedType.getErrorMessageByModelNameType("id", String.valueOf(id)));
    }
}
