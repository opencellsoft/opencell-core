/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.ProductTemplateService;

@Stateless
public class ProductInstanceService extends BusinessService<ProductInstance> {

    @Inject
    ProductTemplateService productTemplateService;

    @Inject
    private ProductChargeInstanceService productChargeInstanceService;

    @Inject
    private WalletService walletService;

    @SuppressWarnings("unchecked")
    public List<ProductInstance> findByCodeUserAccountAndStatus(String code, UserAccount userAccount, InstanceStatusEnum... statuses) {
        List<ProductInstance> productInstance = null;
        try {
            log.debug("start of find {} by code (code={}) ..", "ProductInstance", code);
            QueryBuilder qb = new QueryBuilder(ProductInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.userAccount", "=", userAccount, true);
            qb.startOrClause();
            if (statuses != null && statuses.length > 0) {
                for (InstanceStatusEnum status : statuses) {
                    qb.addCriterionEnum("c.status", status);
                }
            }
            qb.endOrClause();
            productInstance = (List<ProductInstance>) qb.getQuery(getEntityManager()).getResultList();
            log.debug("end of find {} by code (code={}). Result found={}.", "ServiceInstance", code, productInstance != null && !productInstance.isEmpty());
        } catch (NoResultException nre) {
            log.debug("findByCodeUserAccountAndStatus : no service has been found");
        } catch (Exception e) {
            log.error("findByCodeUserAccountAndStatus error={} ", e);
        }
        return productInstance;
    }

    @SuppressWarnings("unchecked")
    public List<ProductInstance> findByProductTemplate(EntityManager em, ProductTemplate productTemplate, Provider provider, InstanceStatusEnum status) {
        QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "i");
        try {
            qb.addCriterionEntity("productTemplate", productTemplate);

            qb.addCriterionEnum("status", status);
            return (List<ProductInstance>) qb.getQuery(em).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ProductInstance> findBySubscription(Subscription subscription) {
        QueryBuilder qb = new QueryBuilder(ProductInstance.class, "p", null);
        try {
            qb.addCriterionEntity("subscription", subscription);
            return (List<ProductInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ProductInstance> findByUserAccount(UserAccount userAccount) {
        QueryBuilder qb = new QueryBuilder(ProductInstance.class, "p", null);
        try {
            qb.addCriterionEntity("userAccount", userAccount);
            qb.addSql("subscription is null");
            return (List<ProductInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

	public List<WalletOperation> saveAndApplyProductInstance(ProductInstance productInstance, String criteria1,
			String criteria2, String criteria3, boolean persist) throws BusinessException {
		create(productInstance);
		return applyProductInstance(productInstance, criteria1, criteria2, criteria3, persist);
	}

    public List<WalletOperation> applyProductInstance(ProductInstance productInstance, String criteria1, String criteria2, String criteria3, boolean persist)
            throws BusinessException {
        return applyProductInstance(productInstance, criteria1, criteria2, criteria3, persist, true);
    }

    public List<WalletOperation> applyProductInstance(ProductInstance productInstance, String criteria1, String criteria2, String criteria3, boolean persist, boolean instantiate)
            throws BusinessException {

        if (instantiate) {
            instantiateProductInstance(productInstance, criteria1, criteria2, criteria3, !persist);
        }

        List<WalletOperation> walletOperations = new ArrayList<>();
        for (ProductChargeInstance productChargeInstance : productInstance.getProductChargeInstances()) {
            walletOperations.addAll(productChargeInstanceService.applyProductChargeInstance(productChargeInstance, !persist));
        }

        return walletOperations;
    }

    public void instantiateProductInstance(ProductInstance productInstance, String criteria1, String criteria2, String criteria3, boolean isVirtual) throws BusinessException {

        if (!isVirtual) {
            create(productInstance);
        }

        for (ProductChargeTemplate productChargeTemplate : productInstance.getProductTemplate().getProductChargeTemplates()) {
            ProductChargeInstance productChargeInstance = new ProductChargeInstance(productInstance, productChargeTemplate);
            productChargeInstance.setCriteria1(criteria1);
            productChargeInstance.setCriteria2(criteria2);
            productChargeInstance.setCriteria3(criteria3);
            productChargeInstance.setOrderNumber(productInstance.getOrderNumber());
            if (!isVirtual) {
                productChargeInstanceService.create(productChargeInstance);
            }

            productInstance.getProductChargeInstances().add(productChargeInstance);

            List<WalletTemplate> walletTemplates = productInstance.getProductTemplate().getWalletTemplates();
            productChargeInstance.setPrepaid(false);
            if (walletTemplates != null && walletTemplates.size() > 0) {
                log.debug("found {} wallets", walletTemplates.size());
                for (WalletTemplate walletTemplate : walletTemplates) {
                    log.debug("walletTemplate {}", walletTemplate.getCode());
                    if (walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                        log.debug("this wallet is prepaid, we set the charge instance itself as being prepaid");
                        productChargeInstance.setPrepaid(true);

                    }
                    WalletInstance walletInstance = walletService.getWalletInstance(productChargeInstance.getUserAccount(), walletTemplate, isVirtual);
                    log.debug("add the wallet instance {} to the chargeInstance {}", walletInstance.getId(), productChargeInstance.getId());
                    productChargeInstance.getWalletInstances().add(walletInstance);
                }
            } else {
                log.debug("as the charge is postpaid, we add the principal wallet");
                productChargeInstance.getWalletInstances().add(productChargeInstance.getUserAccount().getWallet());
            }
        }
    }

}
