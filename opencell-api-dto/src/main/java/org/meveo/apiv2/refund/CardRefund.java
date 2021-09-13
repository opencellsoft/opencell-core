package org.meveo.apiv2.refund;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(builder = ImmutableCardRefund.Builder.class)
public interface CardRefund {
    @Nullable
    Long getCtsAmount();
    @Nullable
    String getCardNumber();
    @Nullable
    String getCustomerAccountCode();
    @Nullable
    String getOwnerName();
    @Nullable
    String getCvv();
    @Nullable
    String getExpiryDate();
    @Nullable
    String getCardType();
    @Nullable
    Date getDueDate();
    @Nullable
    List<Long> getAoToPay();
    @Value.Default default boolean isCreateAO(){return false;}
    @Value.Default default boolean isToMatch(){return false;}
    @Nullable
    String getComment();
}
