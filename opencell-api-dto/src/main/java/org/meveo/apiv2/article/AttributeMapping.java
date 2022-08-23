package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.cpq.enums.RuleOperatorEnum;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableAttributeMapping.class)
public interface AttributeMapping extends Resource {

    Resource getAttribute();

    String getAttributeValue();

    @Nullable
    @Schema(description = "Rule operator")
    RuleOperatorEnum getOperator();
}
