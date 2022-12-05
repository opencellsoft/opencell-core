package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.cpq.enums.OperatorEnum;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableArticleMappingLine.class)
public interface ArticleMappingLine extends Resource {

	@Schema(description = "article mapping associated to this article mapping line")
	@Nullable
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
	@Schema(description = "first parameter of article mapping line")
    String getParameter1();

    @Nullable
	@Schema(description = "second parameter of article mapping line")
    String getParameter2();

    @Nullable
	@Schema(description = "third parameter of article mapping line")
    String getParameter3();

    @Nullable
	@Schema(description = "mapping expression language of article mapping line")
    String getMappingKeyEL();

    @Nullable
	@Schema(description = "list of attribute mapping")
    List<org.meveo.apiv2.article.AttributeMapping> getAttributesMapping();

    @Schema(description = "article mapping line description")
    @Nullable
    String getDescription();

    @Nullable
    @Schema(description = "Applied operator for AttributMapping")
    OperatorEnum getAttributeOperator();
}