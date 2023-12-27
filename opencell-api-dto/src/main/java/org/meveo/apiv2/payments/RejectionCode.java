package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRejectionCode.class)
public interface RejectionCode extends Resource {

    @Nullable
    String getDescription();

    @Nullable
    Map<String, String> getDescriptionI18n();

    @Nullable
    Resource getPaymentGateway();
}
