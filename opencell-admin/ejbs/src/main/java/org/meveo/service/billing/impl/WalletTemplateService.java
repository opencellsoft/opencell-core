package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.BusinessService;

/**
 * Service for CRUD operations and other functions of WalletTemplate entity
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class WalletTemplateService extends BusinessService<WalletTemplate> {

    /**
     * Find wallet templates with code starting with a given phrase
     * 
     * @param code Partial code
     * @param walletType Wallet type - optional
     * @return A list of wallet templates
     */
    @SuppressWarnings("unchecked")
    public List<WalletTemplate> findStartsWithCode(String code, BillingWalletTypeEnum walletType) {
        try {
            QueryBuilder qb = new QueryBuilder(WalletTemplate.class, "t");
            qb.like("code", code, QueryLikeStyleEnum.MATCH_BEGINNING, false);
            if (walletType != null) {
                qb.addCriterionEnum("walletType", walletType);
            }
            return (List<WalletTemplate>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException ne) {
            return null;
        }
    }

    /**
     * Find prepaid wallet templates that are associated with subscription via charge instances
     * 
     * @param subscription Subscription
     * @return A list of prepaid wallet templats
     */
    public List<WalletTemplate> findBySubscription(Subscription subscription) {

        return getEntityManager().createNamedQuery("WalletTemplate.listPrepaidBySubscription", WalletTemplate.class).setParameter("subscription", subscription).getResultList();
    }
}