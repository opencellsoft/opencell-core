package org.meveo.apiv2.payments;

import static java.lang.Boolean.FALSE;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.payments.ImmutableCustomerBalance;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableCustomerBalance.Builder.class)
public interface CustomerBalance extends Resource {

    @Nullable
    String getCode();

    @Nullable
    String getLabel();

    @Nullable
    @Value.Default
    default Boolean getDefaultBalance() {
        return FALSE;
    }

    @Nullable
    List<Resource> getOccTemplates();
}