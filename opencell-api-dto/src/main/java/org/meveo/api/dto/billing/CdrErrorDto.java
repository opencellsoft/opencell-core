package org.meveo.api.dto.billing;

import org.meveo.api.dto.BaseEntityDto;

public class CdrErrorDto extends BaseEntityDto {

    private static final long serialVersionUID = 1026307230457632554L;

    private final String cdrLine;
    private final String rejectReason;

    public CdrErrorDto(String cdrLine, String rejectReason) {
        this.cdrLine = cdrLine;
        this.rejectReason = rejectReason;
    }

    public String getCdrLine() {
        return cdrLine;
    }

    public String getRejectReason() {
        return rejectReason;
    }
}
