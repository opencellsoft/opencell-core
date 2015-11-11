package org.meveo.api.dto.response;

import org.meveo.api.dto.CustomEntityInstanceDto;

/**
 * @author Andrius Karpavicius
 **/
public class GetCustomEntityInstanceResponseDto extends BaseResponse {

    private static final long serialVersionUID = 7328605270701696329L;

    private CustomEntityInstanceDto customEntityInstance;

    public CustomEntityInstanceDto getCustomEntityInstance() {
        return customEntityInstance;
    }

    public void setCustomEntityInstance(CustomEntityInstanceDto customEntityInstance) {
        this.customEntityInstance = customEntityInstance;
    }
}