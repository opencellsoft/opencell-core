package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableWalletOperationRerate.class)
public interface WalletOperationRerate extends Resource {

    @Nullable
    @Value.Default
    default Map<String, Object> getFilters() {
        return Collections.emptyMap();
    }

}
