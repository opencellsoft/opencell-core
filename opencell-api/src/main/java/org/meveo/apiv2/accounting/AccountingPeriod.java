/**
 * 
 */
package org.meveo.apiv2.accounting;


import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAccountingPeriod.class)
public interface AccountingPeriod extends Resource{


	@Schema(description = "Indicate the fiscal year")
	@Nullable
	String getFiscalYear();
	
	@Schema(description = "Indicate the end date")
	@Nullable
	Date getEndDate();

	@Schema(description = "Indicate if use SubAccountingPeriods")
	@Nullable
	Boolean getUseSubAccountingPeriods();

	@Schema(description = "Indicate the SubAccountingPeriod type")
	@Nullable
	String getSubAccountingPeriodType();
	
	@Schema(description = "Indicate the AccountingOperationAction")
	@Nullable
	String getAccountingOperationAction();

	@Schema(description = "Indicate the RegularUserLockOption")
	@Nullable
	String getRegularUserLockOption();

	@Schema(description = "Indicate the CustomLockNumberDays")
	@Nullable
	Integer getCustomLockNumberDays();

	@Schema(description = "Indicate the CustomLockOption")
	@Nullable
	String getCustomLockOption(); 

	@Schema(description = "Indicate the ForceOption")
	@Nullable
	String getForceOption(); 

	@Schema(description = "Indicate the ForceCustomDay")
	@Nullable
	Integer getForceCustomDay();
	
	
	

}

