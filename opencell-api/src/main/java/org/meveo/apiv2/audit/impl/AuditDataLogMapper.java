package org.meveo.apiv2.audit.impl;

import org.meveo.apiv2.audit.AuditDataLogDto;
import org.meveo.apiv2.audit.ImmutableAuditDataLogDto;
import org.meveo.model.audit.AuditDataLog;

public class AuditDataLogMapper {

    protected AuditDataLogDto toResource(AuditDataLog dataLog) {

        return ImmutableAuditDataLogDto.builder().action(dataLog.getAction()).id(dataLog.getId()).created(dataLog.getCreated()).entityClass(dataLog.getEntityClass()).origin(dataLog.getOrigin())
            .originName(dataLog.getOriginName()).username(dataLog.getUserName()).valuesOld(dataLog.getValuesOld()).valuesChanged(dataLog.getValuesChanged()).build();
    }
}