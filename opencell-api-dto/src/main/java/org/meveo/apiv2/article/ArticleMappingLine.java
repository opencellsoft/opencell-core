package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMappingLine.class)
public interface ArticleMappingLine extends Resource {

    Resource getArticleMapping();

    Resource getAccountingArticle();

    Resource getOffer();

    Resource getProduct();

    Resource getCharge();

    String getParameter1();

    String getParameter2();

    String getParameter3();

    String getMappingKeyEL();

    List<AttributeMapping> getAttributesMapping();
}
