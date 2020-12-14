package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMapping.class)
public interface ArticleMapping extends Resource {

    String getCode();

    String getDescription();

    @Nullable
    Resource getMappingScript();
}
