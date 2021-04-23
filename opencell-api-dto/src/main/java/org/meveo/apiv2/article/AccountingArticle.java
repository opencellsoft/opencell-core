package org.meveo.apiv2.article;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableAccountingArticle.class)
public interface AccountingArticle extends Resource {


	@Schema(description = "code of accounting article")
	@NotNull
    String getCode();

	@NotNull
	@Schema(description = "description of accounting article")
    String getDescription();

	@NotNull
	@Schema(description = "tax associated to accounting article")
    Resource getTaxClass();

	@NotNull
	@Schema(description = "invoice subcategory associated to accounting article")
    Resource getInvoiceSubCategory();

    @Nullable
	@Schema(description = "accounting code associated to accounting article")
    Resource getAccountingCode();

    @Nullable
	@Schema(description = "article family associated to accounting article")
    Resource getArticleFamily();

    @Nullable
	@Schema(description = "first analytic code")
    String getAnalyticCode1();

    @Nullable
	@Schema(description = "second analytic code")
    String getAnalyticCode2();

    @Nullable
	@Schema(description = "third analytic code")
    String getAnalyticCode3();

    @Nullable
	@Schema(description = "list of language description")
    List<LanguageDescriptionDto> getLanguageDescriptions();

    @Nullable
	@Schema(description = "custom field associated to accounting article")
    CustomFieldsDto getCustomFields();



}
