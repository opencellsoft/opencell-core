package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.rating.CDRStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCdrStatusDtoInput.class)
public interface CdrStatusDtoInput extends Resource {

    @Nullable
    public String getRejectReason();
    
    @Nullable
    public CDRStatusEnum getStatus();
}