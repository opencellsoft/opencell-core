package org.meveo.apiv2.ordering.orderItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableSubscription.class)
public interface Subscription extends Resource {
    @Nullable
    String getCode();
    @Nullable
    String getSubscriptionDate();
    @Nullable
    String getEndAgreementDate();
    @Nullable
    Resource getSeller();
    @Nullable
    Resource getOfferTemplate();
    @Nullable
    Resource getUserAccount();
    @Nullable
    List<ServiceInstance> getServiceInstances();
}
