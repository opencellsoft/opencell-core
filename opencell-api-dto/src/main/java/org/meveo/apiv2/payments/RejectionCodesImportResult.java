package org.meveo.apiv2.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableRejectionCodesImportResult.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface RejectionCodesImportResult {

    @Nullable
    @Schema(description = "Number of lines")
    Integer getLinesCount();

    @Nullable
    @Schema(description = "Number of Successfully imported code")
    Integer getSuccessfullyImportedCodes();

    @Nullable
    @Schema(description = "Number of error occurred during import")
    Integer getErrorCount();

    @Nullable
    @Schema(description = "Errors occurred during import")
    List<String> getErrors();

    @Nullable
    @Schema(description = "Imported file encoded")
    String getImportedFile();

    @Nullable
    @Schema(description = "Import mode")
    RejectionCodeImportMode getImportMode();
}
