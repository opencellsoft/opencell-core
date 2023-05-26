package org.meveo.apiv2.billing;

import static org.meveo.model.billing.RatedTransactionAction.REOPEN;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.RatedTransactionAction;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCancelBillingRunInput.class)
public interface CancelBillingRunInput extends Resource {

    @Default
    @Schema(description = "Action to perform")
    default RatedTransactionAction getRatedTransactionAction() {
        return REOPEN;
    }
}
