package org.meveo.apiv2.billing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A Batch entity definition.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableBatchEntity.class)
public interface BatchEntity extends Resource {

    @Schema(description = "An optional text for user to input a customized description")
    @NotNull
    String getCode();

    @NotNull
    @Schema(description = "The job template that can process the batch.\n" +
            "For now, we won’t limit to a specific job instance.")
    String getTargetJob();

    @NotNull
    @Schema(description = "The target entity of the filters.\n" +
            "Short entity name.")
    String getTargetEntity();

    @NotEmpty(message = "At least one filter is required")
    @Schema(description = "The filters defining the batch")
    Map<String, Object> getFilters();

    @Nullable
    @Value.Default
    @Schema(description = "If true then an email will be sent to notify the creator that his batch has been processed.")
    default Boolean getNotify() {
        return Boolean.FALSE;
    }
}