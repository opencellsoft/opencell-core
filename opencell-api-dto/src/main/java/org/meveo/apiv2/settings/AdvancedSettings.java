package org.meveo.apiv2.settings;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAdvancedSettings.class)
public interface AdvancedSettings extends Resource {

	@Nullable
	String getOrigin();

	@Nullable
	String getCategory();

	@Nullable
	String getGroup();

	@Nullable
	String getValue();

	@Nullable
	String getType();

	@Nullable
	String getDescription();

}