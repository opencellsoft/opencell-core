package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ImmutableAccountingArticle;
import org.meveo.apiv2.article.resource.AccountingArticleResource;
import org.meveo.apiv2.article.service.AccountingArticleService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.article.AccountingArticle;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class AccountingArticleResourceImpl implements AccountingArticleResource {

    @Inject
    private AccountingArticleService accountingArticleService;
    private AccountingArticleMapper mapper = new AccountingArticleMapper();


    @Override
    public Response createAccountingArticle(org.meveo.apiv2.article.AccountingArticle accountingArticle) {

        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);
        accountingArticleEntity = accountingArticleService.create(accountingArticleEntity);

        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accountingArticleEntity)))
                .build();
    }

    private org.meveo.apiv2.article.AccountingArticle toResourceOrderWithLink(org.meveo.apiv2.article.AccountingArticle accountingResource) {
        return ImmutableAccountingArticle.copyOf(accountingResource)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(AccountingArticleResource.class)
                                .withId(accountingResource.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }

}
