package org.meveo.apiv2.admin;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableContactInformation.class)
public interface ContactInformation extends Resource{

	@Nullable
	String getEmail();

	@Nullable
	String getPhone();

	@Nullable
	String getMobile();

	@Nullable
	String getFax();
	
}
