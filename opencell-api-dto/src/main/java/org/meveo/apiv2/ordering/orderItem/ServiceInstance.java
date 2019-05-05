package org.meveo.apiv2.ordering.orderItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableServiceInstance.class)
public interface ServiceInstance extends Resource {
    Resource getServiceTemplate();
}
