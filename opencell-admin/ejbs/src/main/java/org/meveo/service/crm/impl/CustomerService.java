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
package org.meveo.service.crm.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.crm.Customer;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.AccountService;
/**
 * Customer service implementation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class CustomerService extends AccountService<Customer> {
    
    @Inject
    private SellerService sellerService;
    /**
     * find customer by code.
     * @param code code of customer
     * @return found customer or null
     * @see org.meveo.service.base.BusinessService#findByCode(java.lang.String)
     */
    public Customer findByCode(String code) {
        Query query = getEntityManager().createQuery("from " + Customer.class.getSimpleName() + " where code=:code").setParameter("code", code);
        if (query.getResultList().size() == 0) {
            return null;
        }
        return (Customer) query.getResultList().get(0);
    }
    /**
     * @param code code of customer
     * @param fetchFields list of fields will be fetched with.
     * @return customer.
     */
    public Customer findByCodeAndFetch(String code, List<String> fetchFields) {
        QueryBuilder qb = new QueryBuilder(Customer.class, "c", fetchFields);
        qb.addCriterion("c.code", "=", code, true);
        try {
            return (Customer) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    /**
     * @param code code of seller
     * @return list of customer for give seller's code
     */
    @SuppressWarnings("unchecked")
    public List<Customer> listBySellerCode(String code) {
        QueryBuilder qb = new QueryBuilder(Customer.class, "c");
        qb.addCriterion("seller.code", "=", code, true);
        try {
            return (List<Customer>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    /**
     * @return list of sellers.
     */
    @SuppressWarnings("unchecked")
    public List<Seller> listSellersWithCustomers() {
        try {
            return (List<Seller>) getEntityManager().createQuery("SELECT DISTINCT c.seller " + "FROM Customer c ").getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * If Seller country is different from IBAN customer country (two first letter), then the BIC is mandatory.
     * If no country on seller, check this on "Application configuration" Bank information Iban two first letters.
     * If no seller nor system country information, BIC stay mandatory.
     *
     * @param customer The customer
     * @param iban The customer account iban to check
     * @return True if the BIC are required, False if not
     */
    public boolean isBicRequired(Customer customer, String iban) {
        log.trace("Check isBicRequired for iban:{} customer:{} ",iban,customer);
        if(customer == null || iban == null ) {
            return true;
        }        
        String countryCodeFromSellerOrProvider = null;
        if (appProvider.getBankCoordinates() != null && !StringUtils.isBlank(appProvider.getBankCoordinates().getIban())
                && appProvider.getBankCoordinates().getIban().length() > 1) {
            countryCodeFromSellerOrProvider = appProvider.getBankCoordinates().getIban().substring(0, 2);
        }
        
        if(customer.getSeller() != null) {
            TradingCountry sellerTradingCountry = sellerService.refreshOrRetrieve(customer.getSeller()).getTradingCountry();
            if (sellerTradingCountry != null) {
                countryCodeFromSellerOrProvider = sellerTradingCountry.getCountryCode();
            }
        }
        
        log.trace("countryCodeFromSellerOrProvider:"+countryCodeFromSellerOrProvider);
        if (countryCodeFromSellerOrProvider != null && iban.startsWith(countryCodeFromSellerOrProvider)) {
            return false;
        }
        return true;
    }
    
    public Customer findByCompanyName(String companyName) {
    	QueryBuilder qb = new QueryBuilder(Customer.class, "c");
        qb.addCriterion("c.additionalDetails.companyName", "=", companyName, true);
        try {
            return (Customer) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
	public void anonymizeGdpr(Customer entity, String randomCode) {
    	entity.anonymize(randomCode);
	}
	
	@SuppressWarnings("unchecked")
	public List<Customer> getCustomersByQueryBuilder(QueryBuilder qb) {
		return qb.getQuery(getEntityManager()).getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Customer> listInactiveProspect(int nYear) {
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);
        
        try {
            return getEntityManager().createNamedQuery("Customer.getProspects").setParameter("creationDate", higherBound).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list subscription by customer", e);
            return null;
        }
    }
}