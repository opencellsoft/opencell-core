package org.meveo.apiv2.billing;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.EvaluationModeEnum;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.model.cpq.enums.OperatorEnum;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceValidationRuleDto.class)
public interface InvoiceValidationRuleDto extends Resource {

    @Schema(description = "Description Of Invoice Validation Rule")
    @Nullable
    String getDescription();

    @Schema(description = "Priority")
    @Nullable
    Integer getPriority();

    @Schema(description = "Valid From")
    @Nullable
    Date getValidFrom();

    @Schema(description = "Valid To")
    @Nullable
    Date getValidTo();

    @Schema(description = "Type")
    @Nullable
    String getType();

    @Schema(description = "Fail Status")
    @Nullable
    InvoiceValidationStatusEnum getFailStatus();

    @Schema(description = "Validation Script")
    @Nullable
    String getValidationScript();

    @Schema(description = "Validation EL")
    @Nullable
    String getValidationEL();

    @Schema(description = "InvoiceType")
    @Nullable
    String getInvoiceType();

    @Schema(description = "Code")
    @Nullable
    String getCode();
    
    @Nullable
    @Schema(description = "Rule values")
    Map<String, String> getRuleValues();

    @Nullable
    @Schema(description = "Evaluation Mode")
    EvaluationModeEnum getEvaluationMode();
    
    @Nullable
    @Schema(description = "Applied operator")
    OperatorEnum getOperator();
    
    @Nullable
    @Schema(description = "Sub rules for composite rule")
	List<InvoiceValidationRuleDto> getSubRules();

}
