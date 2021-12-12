package org.meveo.apiv2.AcountReceivable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Date;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableDeferralPayments.Builder.class)
public interface DeferralPayments {
    @Nullable
    Long getAccountOperationId();

    @Nullable
    String getPaymentMethod();

    @Nullable
    Date getPaymentDate();
}
