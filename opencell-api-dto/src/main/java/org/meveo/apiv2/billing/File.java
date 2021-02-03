package org.meveo.apiv2.billing;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableFile.class)
public interface File extends Resource {

	@Nullable
	Long getReferencedEntity();
	
	@Nullable
	String getFileType();
	
	@Nullable
	String getFileName();
	
	@Nullable
	byte[] getFileContent();
	
}