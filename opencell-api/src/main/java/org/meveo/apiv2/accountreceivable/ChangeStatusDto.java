package org.meveo.apiv2.accountreceivable;

import java.util.List;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.payments.AccountOperationStatus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableChangeStatusDto.class)
public interface ChangeStatusDto extends Resource {

    @Schema(description = "ids of account operations to update")
    List<Long> getAccountOperations();

    @Schema(description = "new status of account operations")
    AccountOperationStatus getStatus();
    
}