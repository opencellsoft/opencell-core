

package org.meveo.apiv2.ordering.resource.order;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.ordering.ThresholdRecipientsEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableThresholdInput.class)
public interface ThresholdInput extends Resource {

    @NotNull
    Integer getSequence();

    @Nullable
    Integer getPercentage();

    List<ThresholdRecipientsEnum> getRecipients();
}