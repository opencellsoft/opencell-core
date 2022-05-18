

package org.meveo.apiv2.ordering.resource.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.ordering.ThresholdRecipientsEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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


}
