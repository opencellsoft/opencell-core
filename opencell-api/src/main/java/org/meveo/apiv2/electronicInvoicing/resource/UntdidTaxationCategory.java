package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableUntdidTaxationCategory.Builder.class)
public interface UntdidTaxationCategory extends Resource {
	
	@Nullable
	String getCode();
	  
	@Nullable
	String getName();
	  
	@Nullable
	String getSemanticModel();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.UntdidTaxationCategory toEntity() {
		org.meveo.model.billing.UntdidTaxationCategory untdidTaxationCategory = new org.meveo.model.billing.UntdidTaxationCategory();
		
		untdidTaxationCategory.setCode(getCode());
		untdidTaxationCategory.setName(getName());
		untdidTaxationCategory.setSemanticModel(getSemanticModel());
		untdidTaxationCategory.setVersion(getVersion());
		
        return untdidTaxationCategory;
    }

}
