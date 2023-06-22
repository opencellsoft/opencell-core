package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.annotations.SerializedName;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonDeserialize(as = ImmutableInfoSigner.class)
public interface InfoSigner {
	
	@Nullable
	@JsonProperty("first_name")
	String getFirstName();
	@Nullable
	@JsonProperty("last_name")
	String getLastName();
	
	@Nullable
	String getEmail();
	
	@Nullable
	@JsonProperty("phone_number")
	String getPhoneNumber();
	
	@Nullable
	String getLocale();
}
