package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidInvoiceSubjectCode.Builder.class)
public interface UntdidInvoiceSubjectCode extends Resource {
	
	@Nullable
	String getCode();
	  
	@Nullable
	String getCodeName();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidInvoiceSubjectCode toEntity() {
		org.meveo.model.billing.UntdidInvoiceSubjectCode untdidInvoiceSubjectCode = new org.meveo.model.billing.UntdidInvoiceSubjectCode();
		
		untdidInvoiceSubjectCode.setCode(getCode());
		untdidInvoiceSubjectCode.setCodeName(getCodeName());
		untdidInvoiceSubjectCode.setVersion(getVersion());
		
        return untdidInvoiceSubjectCode;
    }

}
