package org.meveo.apiv2.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableCustomerSequence.class)
public interface CustomerSequence extends Resource {
	
	@Nullable
	String getPrefix();
	@Nullable
	public Long getSequenceSize();
	@Nullable
	public Long getCurrentSequenceNb();
	
	
}
