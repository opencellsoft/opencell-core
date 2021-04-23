package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMappingLine.class)
public interface ArticleMappingLine extends Resource {

	@Schema(description = "article mapping associated to this article mapping line")
	@NotNull
    Resource getArticleMapping();

	@Schema(description = "accounting article associated to this article mapping line")
    @NotNull
    Resource getAccountingArticle();

    @Nullable
	@Schema(description = "offer template associated to this article mapping line")
    Resource getOffer();

    @Nullable
	@Schema(description = "product associated to this article mapping line")
    Resource getProduct();

    @Nullable
	@Schema(description = "charge associated to this article mapping line")
    Resource getCharge();

    @Nullable
	@Schema(description = "first parameter of artcile mapping line")
    String getParameter1();

    @Nullable
	@Schema(description = "second parameter of artcile mapping line")
    String getParameter2();

    @Nullable
	@Schema(description = "third parameter of artcile mapping line")
    String getParameter3();

    @Nullable
	@Schema(description = "mapping expression language of artcile mapping line")
    String getMappingKeyEL();

    @Nullable
	@Schema(description = "list of attribute mapping")
    List<org.meveo.apiv2.article.AttributeMapping> getAttributesMapping();
}
