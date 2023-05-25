package org.meveo.apiv2.admin;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableSeller.class)
public interface Seller extends Resource{

	@Nullable
	String getDescription();

	@Nullable
	String getCurrencyCode();

	@Nullable
	String getCountryCode();

	@Nullable
	String getLanguageCode();

	@Nullable
	Address getAddress();

	@Nullable
	ContactInformation getContactInformation();
	
	@Nullable
	String getParentSeller();
	
	@Nullable
	@Value.Default 
	default List<String> getMediaCodes(){
		return Collections.emptyList();
	}
	
	@Nullable
	String getVatNumber();
}
