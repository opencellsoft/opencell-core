package org.meveo.apiv2.cpq.contracts;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableBillingRule.class)
public interface BillingRuleDto extends Resource {

	Integer getPriority();

	@NotNull
	@Schema(description = "Expression to tell OC if this rule should apply to the tested rated transaction")
	String getCriteriaEL();

	@NotNull
	@Schema(description = "Expression to provide the code of a billing account that will be invoiced this rated transaction.")
	String getInvoicedBACodeEL();

}
