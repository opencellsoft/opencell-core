package org.meveo.apiv2.electronicInvoicing.resource;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableIsoIcd.Builder.class)
public interface IsoIcd extends Resource {

	@Nullable
	String getCode();
	  
	@Nullable
	String getSchemeName();
	
	@Value.Default 
	default Integer getVersion(){return 0;}
	
	default org.meveo.model.billing.IsoIcd toEntity() {
		org.meveo.model.billing.IsoIcd isoIcd = new org.meveo.model.billing.IsoIcd();
		
		isoIcd.setCode(getCode());
		isoIcd.setSchemeName(getSchemeName());
		isoIcd.setVersion(getVersion());
		
        return isoIcd;
    }
}