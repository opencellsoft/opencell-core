package org.meveo.api.rest.impl;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.exception.*;
import org.meveo.api.ws.impl.ExceptionProcessorWs;
import org.meveo.commons.utils.StringUtils;

public class ExceptionProcessorRs extends ExceptionProcessorWs {

    public ExceptionProcessorRs(ResourceBundle resourceMessages) {
        super(resourceMessages);
    }

    @Override
    public void process(Exception e, ActionStatus status) {
        super.process(e, status);
        handleErrorStatus(status);
    }

    /**
     * @param status action status.
     */
    private void handleErrorStatus(ActionStatus status) {
        if (StringUtils.isBlank(status.getErrorCode())) {
            throw new InternalServerErrorException(status);
        } else {
            String str = status.getErrorCode().toString();
            if ("MISSING_PARAMETER".equals(str)//
                    || "INVALID_PARAMETER".equals(str)//
                    || "INVALID_ENUM_VALUE".equals(str)//
                    || "INVALID_IMAGE_DATA".equals(str)) {
                throw new BadRequestException(status);
            } else if ("UNAUTHORIZED".equals(str) //
                    || "AUTHENTICATION_AUTHORIZATION_EXCEPTION".equals(str)) {
                throw new NotAuthorizedException(status);
            } else if ("DELETE_REFERENCED_ENTITY_EXCEPTION".equals(str) //
                    || "DUPLICATE_ACCESS".equals(str) || "ACTION_FORBIDDEN".equals(str)//
                    || "INSUFFICIENT_BALANCE".equals(str)) {
                throw new ForbiddenException(status);
            } else if ("ENTITY_DOES_NOT_EXISTS_EXCEPTION".equals(str)) {
                throw new NotFoundException(status);
            } else if ("ENTITY_ALREADY_EXISTS_EXCEPTION".equals(str)){
            	throw new AlreadyExistException(status);
            }else {
                throw new InternalServerErrorException(status);
            }
        }
    }
}
