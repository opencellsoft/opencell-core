package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonDeserialize(as = ImmutableInfoSigner.class)
public interface InfoSigner {
	
	@Nullable
	String getFirst_name();
	@Nullable
	
	String getLast_name();
	
	@Nullable
	String getEmail();
	
	@Nullable
	String getPhone_number();
	
	@Nullable
	String getLocale();
}
