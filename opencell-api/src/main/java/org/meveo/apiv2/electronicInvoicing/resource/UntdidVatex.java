package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidVatex.Builder.class)
public interface UntdidVatex extends Resource {
	
	@Nullable
	String getCode();
	  
	@Nullable
	String getCodeName();
	  
	@Nullable
	String getRemark();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidVatex toEntity() {
		org.meveo.model.billing.UntdidVatex untdidVatex = new org.meveo.model.billing.UntdidVatex();
		
		untdidVatex.setCode(getCode());
		untdidVatex.setCodeName(getCodeName());
		untdidVatex.setRemark(getRemark());
		untdidVatex.setVersion(getVersion());
		
        return untdidVatex;
    }

}
