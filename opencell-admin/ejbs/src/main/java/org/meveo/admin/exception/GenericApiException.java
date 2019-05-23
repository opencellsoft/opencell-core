package org.meveo.admin.exception;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class GenericApiException extends RuntimeException{

    public GenericApiException(String message) {
        super(message);
    }

    public enum WrongRequestedType {
        EMPTY_NAME(StringUtils.EMPTY::equals, (requestedType, modelName) -> String.format("The requested %s should not be empty", requestedType)),
        NULL_NAME(Objects::isNull, (requestedType, modelName) -> String.format("The requested %s should not be null", requestedType));
        private Predicate<String> wrongModelNameCondition;
        private WrongModelNameErrorMessage wrongModelNameErrorMessage;

        WrongRequestedType(Predicate<String> wrongModelNameCondition, WrongModelNameErrorMessage wrongModelNameErrorMessage) {
            this.wrongModelNameCondition = wrongModelNameCondition;
            this.wrongModelNameErrorMessage = wrongModelNameErrorMessage;
        }

        static String getErrorMessageByModelNameType(String requestedType, String modelName){
            return Arrays.stream(values())
                    .filter(value -> value.wrongModelNameCondition.test(modelName))
                    .findFirst()
                    .map(value -> value.wrongModelNameErrorMessage.message(requestedType, modelName))
                    .orElse(String.format("Wrong requested %s: %s", requestedType, modelName));

        }
    }

    private interface WrongModelNameErrorMessage{
        String message(String requestedType, String modelName);
    }
}
