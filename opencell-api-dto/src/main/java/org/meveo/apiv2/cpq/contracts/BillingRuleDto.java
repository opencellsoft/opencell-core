package org.meveo.apiv2.cpq.contracts;

import javax.annotation.Nonnull;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableBillingRuleDto.class)
public interface BillingRuleDto extends Resource {

	Integer getPriority();

	@Nonnull
	@Schema(description = "Expression to tell OC if this rule should apply to the tested rated transaction")
	String getCriteriaEL();

	@Nonnull
	@Schema(description = "Expression to provide the code of a billing account that will be invoiced this rated transaction.")
	String getInvoicedBACodeEL();

}
