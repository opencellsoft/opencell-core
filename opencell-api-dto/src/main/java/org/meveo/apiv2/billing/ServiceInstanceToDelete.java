package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableServiceInstanceToDelete.class)
public interface ServiceInstanceToDelete {

    @Schema(description = "List of Service instance to delete")
    @NotEmpty
    List<Long> getIds();
}