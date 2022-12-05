package org.meveo.apiv2.ordering.resource.order;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.ordering.ThresholdRecipientsEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableThresholdInput.class)
public interface ThresholdInput extends Resource {

    @NotNull
    Integer getSequence();

    @NotNull
    Integer getPercentage();

    List<ThresholdRecipientsEnum> getRecipients();
    
    @Nullable
    @Email
    String getExternalRecipient();
}