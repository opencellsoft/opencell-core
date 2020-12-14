package org.meveo.apiv2.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.generic.LanguageDescription;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nonnull;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableAccountingArticle.class)
public interface AccountingArticle extends Resource {

    @Nonnull
    String getCode();

    @Nonnull
    String getDescription();

    @Nonnull
    Resource getTaxClass();

    @Nonnull
    Resource getInvoiceSubCategory();

    @Nonnull
    Resource getAccountingCode();

    Resource getArticleFamily();

    String getAnalyticCode1();

    String getAnalyticCode2();

    String getAnalyticCode3();

    List<LanguageDescription> getLanguageDescriptions();

    CustomFieldsDto getCustomFields();



}
