package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMappingLine.class)
public interface ArticleMappingLine extends Resource {

    Resource getArticleMapping();

    Resource getAccountingArticle();

    @Nullable
    Resource getOffer();

    @Nullable
    Resource getProduct();

    @Nullable
    Resource getCharge();

    @Nullable
    String getParameter1();

    @Nullable
    String getParameter2();

    @Nullable
    String getParameter3();

    @Nullable
    String getMappingKeyEL();

    @Nullable
    List<AttributeMapping> getAttributesMapping();
}
