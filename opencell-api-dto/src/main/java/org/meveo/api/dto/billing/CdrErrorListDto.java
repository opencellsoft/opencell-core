package org.meveo.api.dto.billing;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;

import java.util.List;

public class CdrErrorListDto extends ActionStatus {

    private List<CdrErrorDto> errors;

    public CdrErrorListDto(ActionStatusEnum statusEnum, String message, List<CdrErrorDto> cdrErrorDtos) {
        super(statusEnum, message);
        this.errors = cdrErrorDtos;
    }

    public List<CdrErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<CdrErrorDto> errors) {
        this.errors = errors;
    }
}
