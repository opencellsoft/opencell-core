package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidAllowanceCode.Builder.class)
public interface UntdidAllowanceCode extends Resource {
	
	@Nullable
	String getCode();
	  
	@Nullable
	String getDescription();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidAllowanceCode toEntity() {
		org.meveo.model.billing.UntdidAllowanceCode untdidAllowanceCode = new org.meveo.model.billing.UntdidAllowanceCode();
		
		untdidAllowanceCode.setCode(getCode());
		untdidAllowanceCode.setDescription(getDescription());
		untdidAllowanceCode.setVersion(getVersion());
		
        return untdidAllowanceCode;
    }

}
