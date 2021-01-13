package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.generic.LanguageDescription;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.List;

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
    List<LanguageDescription> getLanguageDescriptions();

    //CustomFieldsDto getCustomFields();



}
