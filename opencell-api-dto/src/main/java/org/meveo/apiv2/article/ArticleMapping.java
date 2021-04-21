package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMapping.class)
public interface ArticleMapping extends Resource {

	@Schema(description = "code of article mapping")
    String getCode();

	@Schema(description = "description of article mapping")
    String getDescription();

	@Schema(description = "mapping script associated to this article mapping")
    @Nullable
    Resource getMappingScript();
}
