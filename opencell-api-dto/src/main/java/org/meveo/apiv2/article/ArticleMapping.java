package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nonnull;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMapping.class)
public interface ArticleMapping extends Resource {

    @Nonnull
    String getCode();

    @Nonnull
    String getDescription();

    Resource getMappingScript();
}
