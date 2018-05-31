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
package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.crm.Customer;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.AccountService;

/**
 * A service class to manage CRUD operations on CustomerAccount entity
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Stateless
public class CustomerService extends AccountService<Customer> {

    @Inject
    private SellerService sellerService;

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
     * If Seller country is different from IBAN customer country (two first letter), then the BIC is mandatory. If no country on seller, check this on "Application configuration"
     * Bank information Iban two first letters. If no seller nor system country information, BIC stay mandatory.
     *
     * @param customer The customer
     * @param iban The customer account iban to check
     * @return True if the BIC are required, False if not
     */
    public boolean isBicRequired(Customer customer, String iban) {
        log.trace("Check isBicRequired for iban:{} customer:{} ", iban, customer);
        if (customer == null || iban == null) {
            return true;
        }
        String countryCodeFromSellerOrProvider = null;
        TradingCountry sellerTradingCountry = sellerService.refreshOrRetrieve(customer.getSeller()).getTradingCountry();
        if (sellerTradingCountry != null) {
            countryCodeFromSellerOrProvider = sellerTradingCountry.getCountryCode();
        } else {
            if (appProvider.getBankCoordinates() != null && !StringUtils.isBlank(appProvider.getBankCoordinates().getIban())
                    && appProvider.getBankCoordinates().getIban().length() > 1) {
                countryCodeFromSellerOrProvider = appProvider.getBankCoordinates().getIban().substring(0, 2);
            }
        }
        log.trace("countryCodeFromSellerOrProvider:" + countryCodeFromSellerOrProvider);
        if (countryCodeFromSellerOrProvider != null && iban.startsWith(countryCodeFromSellerOrProvider)) {
            return false;
        }
        return true;
    }
}