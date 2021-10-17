package org.meveo.apiv2.billing;

import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCdrListInput.class)
public interface CdrListInput extends Resource {

    @Schema(description = "List of CDRs to process")
    List<String> getCdrs();

    @Default
    @Schema(description = "How the CDR list is processed : STOP_ON_FIRST_FAIL, PROCESS_ALL, ROLL_BACK_ON_ERROR")
    default RegisterCdrListModeEnum getMode() {
        return RegisterCdrListModeEnum.STOP_ON_FIRST_FAIL;
    }
}