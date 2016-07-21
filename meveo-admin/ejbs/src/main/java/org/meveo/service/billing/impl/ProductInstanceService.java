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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.ProductTemplateService;

@Stateless
public class ProductInstanceService extends BusinessService<ProductInstance> {

    @Inject
    ProductTemplateService productTemplateService;

    @Inject
	private ProductChargeInstanceService productChargeInstanceService;


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
            qb.addCriterionEntity("provider", provider);
            qb.addCriterionEnum("status", status);

            return (List<ProductInstance>) qb.getQuery(em).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public ProductInstance applyProductReturnInstance(UserAccount userAccount,ProductTemplate productTemplate, BigDecimal quantity,
    		Date chargeDate,String description,BigDecimal amountWithoutTax, BigDecimal amountWithTax, 
    		OfferTemplate offerTemplate,String criteria1, String criteria2, String criteria3,
    		User user,boolean persist) throws BusinessException{
    	ProductInstance productInstance = new ProductInstance(userAccount, productTemplate, quantity, chargeDate, description, user);
    	if(persist){
    		create(productInstance,user);
    	}
    	ProductChargeInstance pcInstance = new ProductChargeInstance(productInstance,user,persist);
    	if(persist){
    		productChargeInstanceService.create(pcInstance,user);
    	}
    	productChargeInstanceService.apply(pcInstance, description, offerTemplate, chargeDate, amountWithoutTax, amountWithTax, 
    			criteria1, criteria2, criteria3,user,persist);
    	return productInstance;
    }

    
    public WalletOperation applyProduct(UserAccount userAccount,ProductTemplate productTemplate, BigDecimal quantity,
    		Date chargeDate,String description,BigDecimal amountWithoutTax, BigDecimal amountWithTax, 
    		OfferTemplate offerTemplate,String criteria1, String criteria2, String criteria3,
    		User user,boolean persist) throws BusinessException{
    	WalletOperation result = null;
    	ProductInstance productInstance = new ProductInstance(userAccount, productTemplate, quantity, chargeDate, description, user);
    	if(persist){
    		create(productInstance,user);
    	}
    	ProductChargeInstance pcInstance = new ProductChargeInstance(productInstance,user,persist);
    	if(persist){
    		productChargeInstanceService.create(pcInstance,user);
    	}
    	result = productChargeInstanceService.apply(pcInstance, description, offerTemplate, chargeDate, amountWithoutTax, amountWithTax, 
    			criteria1, criteria2, criteria3,user,persist);
    	return result;
    }

}
