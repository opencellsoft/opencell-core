/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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