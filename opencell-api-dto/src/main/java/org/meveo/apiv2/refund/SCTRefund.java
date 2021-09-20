package org.meveo.apiv2.refund;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(builder = ImmutableSCTRefund.Builder.class)
public interface SCTRefund {
    @Nullable
    Double getCtsAmount();
    @Nullable
    @JsonAlias("IBAN")
    String getIBAN();
    @Nullable
    String getCustomerAccountCode();
    @Nullable
    List<Long> getAoToRefund();
    @Value.Default default boolean createAO(){return false;}
    @Value.Default default boolean toMatch(){return false;}
    @Nullable
    String getComment();
}
