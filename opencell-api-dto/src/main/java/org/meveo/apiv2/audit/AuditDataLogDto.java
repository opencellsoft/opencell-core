package org.meveo.apiv2.audit;

import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.audit.AuditCrudActionEnum;
import org.meveo.model.audit.ChangeOriginEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAuditDataLogDto.class)
public interface AuditDataLogDto extends Resource {

    @Schema(description = "entity type/class")
    @NotNull
    String getEntityClass();

    @NotNull
    @Schema(description = "Event timestamp")
    Date getCreated();

    @NotNull
    @Schema(description = "Username that caused event")
    String getUsername();

    @Nullable
    @Schema(description = "CRUD action")
    AuditCrudActionEnum getAction();

    @Nullable
    @Schema(description = "Data change origin")
    ChangeOriginEnum getOrigin();

    @Nullable
    @Schema(description = "Data change origin name")
    String getOriginName();

    @Nullable
    @Schema(description = "Values old")
    Map<String, Object> getValuesOld();

    @Nullable
    @Schema(description = "Values changed")
    Map<String, Object> getValuesChanged();
}