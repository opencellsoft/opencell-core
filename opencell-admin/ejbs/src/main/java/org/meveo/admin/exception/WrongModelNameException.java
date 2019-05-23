package org.meveo.admin.exception;

public class WrongModelNameException extends GenericApiException{

    public WrongModelNameException(String modelName) {
        super(WrongRequestedType.getErrorMessageByModelNameType("model", modelName));
    }



}
