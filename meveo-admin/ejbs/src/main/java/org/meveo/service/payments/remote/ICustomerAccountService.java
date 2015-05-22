/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.service.payments.remote;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * CustomerAccount service remote interface.
 * 
 */

public interface ICustomerAccountService {
    /**
     * Checks if customer account with current id exists
     * 
     * @param id
     *            Customers accounts id
     * 
     */
    public boolean isCustomerAccountWithIdExists(Long id);

    /**
     * Selects all billing keywords
     * 
     * @return list of billing keywords
     */
    public List<String> getAllBillingKeywords();

    /**
     * Imports customer accounts
     * 
     * @param customerAccountsToImport
     *            List of customer accounts to import
     * @return List of failed imports
     */
    public List<CustomerAccount> importCustomerAccounts(List<CustomerAccount> customerAccountsToImport);

    /**
     * Compute blanceExigible and multiple it by flag
     * 
     * @param customerAccountId
     * @param customerAccountCode
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceExigible(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceExigible and multiple it by flag
     * 
     * @param customerAccount
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceExigible(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Compute blanceExigible Without Litigation invoices and multiple it by 
     * flag
     * 
     * @param customerAccountId
     * @param customerAccountCode
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceExigibleWithoutLitigation(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceExigible Without Litigation invoices and multiple it by 
     * flag
     * 
     * @param customerAccount
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceExigibleWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Compute blanceDue and multiple it by flag
     * 
     * @param customerAccountId
     * @param customerAccountCode
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceDue(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceDue and multiple it by flag
     * 
     * @param customerAccount
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceDue(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Compute blanceDue Without Litigation invoices and multiple it by flag
     * 
     * @param customerAccountId
     * @param customerAccountCode
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceDueWithoutLitigation(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceDue Without Litigation invoices and multiple it by flag
     * 
     * @param customerAccount
     * @param to
     * @return
     * @throws BusinessException
     */
    public BigDecimal customerAccountBalanceDueWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Create CustomerAccount entity
     * 
     * @param code
     * @param title
     * @param firstName
     * @param lastName
     * @param address1
     * @param address2
     * @param zipCode
     * @param city
     * @param state
     * @param email
     * @param customerId
     * @param creditCategory
     * @param paymentMethod
     * @param creator
     * @throws BusinessException
     */
    public void createCustomerAccount(String code, String title, String firstName, String lastName, String address1, String address2, String zipCode,
            String city, String state, String email, Long customerId, String creditCategory, PaymentMethodEnum paymentMethod, User creator)
            throws BusinessException;

    /**
     * Update customerAccount entity
     * 
     * @param id
     * @param code
     * @param title
     * @param firstName
     * @param lastName
     * @param address1
     * @param address2
     * @param zipCode
     * @param city
     * @param state
     * @param email
     * @param creditCategory
     * @param paymentMethod
     * @param updateor
     * @throws BusinessException
     */
    public void updateCustomerAccount(Long id, String code, String title, String firstName, String lastName, String address1, String address2, String zipCode,
            String city, String state, String email, String creditCategory, PaymentMethodEnum paymentMethod, User updateor)
            throws BusinessException;

    /**
     * Close CustomerAccount and create closeAccount OCC
     * 
     * @param id
     * @param customerAccountCode
     * @param user
     * @throws BusinessException
     * @throws Exception
     */
    public void closeCustomerAccount(Long customerAccountId, String customerAccountCode, User user) throws BusinessException, Exception;

    /**
     * Close CustomerAccount and create closeAccount OCC
     * 
     * @param customerAccount
     * @param user
     * @throws BusinessException
     * @throws Exception
     */
    public void closeCustomerAccount(CustomerAccount customerAccount, User user) throws BusinessException, Exception;

    /**
     * Find one customer account by id or code, if id set,search by id or by
     * code
     * 
     * @param customerAccountId
     * @param customerAccountCode
     * @return
     * @throws BusinessException
     */
    public CustomerAccount consultCustomerAccount(Long customerAccountId, String customerAccountCode) throws BusinessException;

    /**
     * @param customerAccountId
     * @param customerAccountCode
     * @param creditCategory
     * @param updator
     * @throws BusinessException
     */
    public void updateCreditCategory(Long customerAccountId, String customerAccountCode, String creditCategory, User updator)
            throws BusinessException;

    /**
     * @param customerAccountId
     * @param customerAccountCode
     * @param dunningLevel
     * @param updator
     * @throws BusinessException
     */
    public void updateDunningLevel(Long customerAccountId, String customerAccountCode, DunningLevelEnum dunningLevel, User updator) throws BusinessException;

    /**
     * @param customerAccountId
     * @param customerAccountCode
     * @param paymentMethod
     * @param updator
     * @throws BusinessException
     */
    public void updatePaymentMethod(Long customerAccountId, String customerAccountCode, PaymentMethodEnum paymentMethod, User updator) throws BusinessException;

    /**
     * Get accountOperation created between date from and date tos
     * 
     * @param customerAccountId
     * @param customerAccountCode
     * @param from
     * @param to
     * @return
     * @throws BusinessException
     */
    public List<AccountOperation> consultOperations(Long customerAccountId, String customerAccountCode, Date from, Date to) throws BusinessException;

    /**
     * Transfer amount from fromCustomerAccountId to toCustomerAccountId
     * 
     * @param fromCustomerAccountId
     * @param fromCustomerAccountCode
     * @param toCustomerAccountId
     * @param toCustomerAccountCode
     * @param amount
     * @param user
     * @throws BusinessException
     * @throws Exception
     */
    public void transferAccount(Long fromCustomerAccountId, String fromCustomerAccountCode, Long toCustomerAccountId, String toCustomerAccountCode,
            BigDecimal amount, User user) throws BusinessException, Exception;

    /**
     * Transfer amount from fromCustomerAccountId to toCustomerAccountId
     * 
     * @param fromCustomerAccount
     * @param toCustomerAccount
     * @param amount
     * @param user
     * @throws BusinessException
     * @throws Exception
     */
    public void transferAccount(CustomerAccount fromCustomerAccount, CustomerAccount toCustomerAccount, BigDecimal amount, User user) throws BusinessException,
            Exception;

    /**
     * @param customerAccountId
     * @param customerAccountCode
     * @return
     * @throws BusinessException
     */
    public CustomerAccount findCustomerAccount(Long customerAccountId, String customerAccountCode) throws BusinessException;
}
