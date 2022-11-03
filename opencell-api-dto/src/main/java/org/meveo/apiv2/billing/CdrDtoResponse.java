package org.meveo.apiv2.billing;

import java.util.List;

import org.immutables.value.Value;
import org.meveo.api.dto.billing.CdrErrorDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCdrDtoResponse.class)
public interface CdrDtoResponse extends Resource {

    List<Resource> getCdrs();
    
    List<CdrErrorDto> getErrors();
}