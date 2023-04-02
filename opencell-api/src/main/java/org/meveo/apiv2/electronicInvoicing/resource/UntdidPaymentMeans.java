package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidPaymentMeans.Builder.class)
public interface UntdidPaymentMeans extends Resource {
	
	@Nullable
	String getCode();
	
	@Nullable
	String getCodeName();
	  
	@Nullable
	String getUsageEN16931();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidPaymentMeans toEntity() {
		org.meveo.model.billing.UntdidPaymentMeans untdidPaymentMeans = new org.meveo.model.billing.UntdidPaymentMeans();
		
		untdidPaymentMeans.setCode(getCode());
		untdidPaymentMeans.setUsageEN16931(getUsageEN16931());
		untdidPaymentMeans.setCodeName(getCodeName());
		untdidPaymentMeans.setVersion(getVersion());
		
        return untdidPaymentMeans;
    }

}
