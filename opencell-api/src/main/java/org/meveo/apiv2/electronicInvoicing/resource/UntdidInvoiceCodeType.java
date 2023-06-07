package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidInvoiceCodeType.Builder.class)
public interface UntdidInvoiceCodeType extends Resource {
	
	@Nullable
	String getCode();
	
	@Nullable
	String getInterpretation16931();

	@Nullable
	String getName();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidInvoiceCodeType toEntity() {
		org.meveo.model.billing.UntdidInvoiceCodeType untdidInvoiceCodeType = new org.meveo.model.billing.UntdidInvoiceCodeType();
		
		untdidInvoiceCodeType.setCode(getCode());
		untdidInvoiceCodeType.setInterpretation16931(getInterpretation16931());
		untdidInvoiceCodeType.setName(getName());
		
        return untdidInvoiceCodeType;
    }

}
