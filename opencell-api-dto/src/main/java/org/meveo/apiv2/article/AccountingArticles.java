package org.meveo.apiv2.article;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface AccountingArticles  extends PaginatedResource<AccountingArticle> {

}
