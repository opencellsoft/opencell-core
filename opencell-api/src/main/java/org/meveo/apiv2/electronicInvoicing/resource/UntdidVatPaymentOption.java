package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidVatPaymentOption.Builder.class)
public interface UntdidVatPaymentOption extends Resource {
	
	@Nullable
	String getCode2005();
	  
	@Nullable
	String getValue2005();
	  
	@Nullable
	String getCode2475();
	  
	@Nullable
	String getValue2475();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidVatPaymentOption toEntity() {
		org.meveo.model.billing.UntdidVatPaymentOption untdidVatPaymentOption = new org.meveo.model.billing.UntdidVatPaymentOption();
		
		untdidVatPaymentOption.setCode2005(getCode2005());
		untdidVatPaymentOption.setValue2005(getValue2005());
		untdidVatPaymentOption.setCode2475(getCode2475());
		untdidVatPaymentOption.setValue2475(getValue2475());
		untdidVatPaymentOption.setVersion(getVersion());
		
        return untdidVatPaymentOption;
    }

}
