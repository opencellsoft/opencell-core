package org.meveo.apiv2.article;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.immutables.value.Value;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableAccountingArticle.class)
public interface AccountingArticle extends Resource {

	@NotNull
    String getCode();

	@NotNull
    String getDescription();

	@NotNull
    Resource getTaxClass();

	@NotNull
    Resource getInvoiceSubCategory();

    @Nullable
    Resource getAccountingCode();

    @Nullable
    Resource getArticleFamily();

    @Nullable
    String getAnalyticCode1();

    @Nullable
    String getAnalyticCode2();

    @Nullable
    String getAnalyticCode3();

    @Nullable
    List<LanguageDescriptionDto> getLanguageDescriptions();

    //CustomFieldsDto getCustomFields();



}
