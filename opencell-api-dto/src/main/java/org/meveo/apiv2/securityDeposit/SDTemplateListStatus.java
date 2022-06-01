package org.meveo.apiv2.securityDeposit;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableSDTemplateListStatus.class)
public interface SDTemplateListStatus extends Resource {

    @Schema()
    @JsonAlias("SDTemplateList")
    @NotNull List<Resource> getSecurityDepositTemplates();

    @Schema()
    @NotNull String getStatus();




}
