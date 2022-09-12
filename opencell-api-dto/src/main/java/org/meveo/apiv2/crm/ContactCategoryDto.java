package org.meveo.apiv2.crm;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableContactCategoryDto.class)
public interface ContactCategoryDto extends Resource {

	@Nullable
    @Schema(description = "code of the contact category")
	String getCode();

	@Nullable
	@Schema(description = "description of the contact category")
	String getDescription();

	@Nullable
    @Schema(description = "custom field associated the contact category")
    CustomFieldsDto getCustomFields();
	
}
