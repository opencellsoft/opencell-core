package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAccountingCodeMappingInput.class)
public interface AccountingCodeMappingInput extends Resource {

    @Schema(description = "Accounting article code")
    @Nullable
    String getAccountingArticleCode();

    @Schema(description = "Accounting code mapping list")
    @Nullable
    List<AccountingCodeMapping> getAccountingCodeMappings();
}