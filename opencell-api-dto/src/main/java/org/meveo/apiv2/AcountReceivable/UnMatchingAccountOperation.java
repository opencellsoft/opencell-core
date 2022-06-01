package org.meveo.apiv2.AcountReceivable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableUnMatchingAccountOperation.class)
public interface UnMatchingAccountOperation {

    @Schema(description = "List of AccountOperation for matching")
    @NotEmpty
    List<UnMatchingAccountOperationDetail> getAccountOperations();
}