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
     * Checks if customer account with current id exists.
     * 
     * @param id
     *            Customers accounts id
     * @return true if a custumer account with given id exists
     * 
     */
    boolean isCustomerAccountWithIdExists(Long id);

    /**
     * Selects all billing keywords.
     * 
     * @return list of billing keywords
     */
    List<String> getAllBillingKeywords();

    /**
     * Imports customer accounts.
     * 
     * @param customerAccountsToImport
     *            List of customer accounts to import
     * @return List of failed imports
     */
    List<CustomerAccount> importCustomerAccounts(List<CustomerAccount> customerAccountsToImport);

    /**
     * Compute blanceExigible and multiple it by flag.
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param to until date
     * @return customer account balance exigible
     * @throws BusinessException business exception.
     */
    BigDecimal customerAccountBalanceExigible(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceExigible and multiple it by flag.
     * 
     * @param customerAccount customer account
     * @param to until date
     * @return customer account balance exigible
     * @throws BusinessException business excepton.
     */
    BigDecimal customerAccountBalanceExigible(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Compute blanceExigible Without Litigation invoices and multiple it by.
     * flag
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param to until date
     * @return customer account balance exigible without litigation.
     * @throws BusinessException business exception.
     */
    BigDecimal customerAccountBalanceExigibleWithoutLitigation(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceExigible Without Litigation invoices and multiple it by 
     * flag.
     * 
     * @param customerAccount customer account
     * @param to until date
     * @return customer account balance exigible without ligigation
     * @throws BusinessException business exception
     */
    BigDecimal customerAccountBalanceExigibleWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Compute blanceDue and multiple it by flag.
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param to until date
     * @return customer account balance due
     * @throws BusinessException business exception
     */
    BigDecimal customerAccountBalanceDue(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceDue and multiple it by flag.
     * 
     * @param customerAccount customer account
     * @param to until date
     * @return customer account balance due
     * @throws BusinessException business exception.
     */
    BigDecimal customerAccountBalanceDue(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Compute blanceDue Without Litigation invoices and multiple it by flag.
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param to until date
     * @return customer account balance due without litigation.
     * @throws BusinessException business exception.
     */
    BigDecimal customerAccountBalanceDueWithoutLitigation(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException;

    /**
     * Compute blanceDue Without Litigation invoices and multiple it by flag.
     * 
     * @param customerAccount customer account
     * @param to until date
     * @return customer account balance due without litigation
     * @throws BusinessException business exception.
     */
    BigDecimal customerAccountBalanceDueWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException;

    /**
     * Create CustomerAccount entity.
     * 
     * @param code code of customer account
     * @param title title of customer account
     * @param firstName first name of customer account
     * @param lastName last name of customer account
     * @param address1 address 1 of customer account
     * @param address2 address 2 of customer account
     * @param zipCode zip code of customer account
     * @param city city of customer account
     * @param state state of customer account
     * @param email email of customer account
     * @param customerId customer id of customer account
     * @param creditCategory credit category
     * @param paymentMethod payment method.
     * @throws BusinessException business exception.
     */
    void createCustomerAccount(String code, String title, String firstName, String lastName, String address1, String address2, String zipCode,
            String city, String state, String email, Long customerId, String creditCategory, PaymentMethodEnum paymentMethod)
            throws BusinessException;

    /**
     * Update customerAccount entity.
     * 
     * @param id id of customer account.
     * @param code code of customer account
     * @param title title of customer account
     * @param firstName first name of customer account
     * @param lastName last name of customer account
     * @param address1 address 1 of customer account
     * @param address2 address 2 of customer account
     * @param zipCode zip code of customer account
     * @param city city of customer account
     * @param state state of customer account
     * @param email email of customer account
     * @param creditCategory credit category
     * @param paymentMethod payment method.
     * @param updateor who updates customer account.
     * @throws BusinessException business exception.
     */
    void updateCustomerAccount(Long id, String code, String title, String firstName, String lastName, String address1, String address2, String zipCode,
            String city, String state, String email, String creditCategory, PaymentMethodEnum paymentMethod, User updateor)
            throws BusinessException;

    /**
     * Close CustomerAccount and create closeAccount OCC.
     * 
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @throws BusinessException business exceptions
     * @throws Exception general exception
     */
    void closeCustomerAccount(Long customerAccountId, String customerAccountCode) throws BusinessException, Exception;

    /**
     * Close CustomerAccount and create closeAccount OCC.
     * 
     * @param customerAccount customer account
     * @throws BusinessException business exception
     * @throws Exception exception.
     */
    void closeCustomerAccount(CustomerAccount customerAccount) throws BusinessException, Exception;

    /**
     * Find one customer account by id or code, if id set,search by id or by
     * code.
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @return customer account
     * @throws BusinessException business exception.
     */
    CustomerAccount consultCustomerAccount(Long customerAccountId, String customerAccountCode) throws BusinessException;

    /**
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param creditCategory credit category
     * @param updator updater
     * @throws BusinessException business exception.
     */
    void updateCreditCategory(Long customerAccountId, String customerAccountCode, String creditCategory, User updator)
            throws BusinessException;

    /**
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param dunningLevel dunning level
     * @param updator who update
     * @throws BusinessException business exception.
     */
    void updateDunningLevel(Long customerAccountId, String customerAccountCode, DunningLevelEnum dunningLevel, User updator) throws BusinessException;

    /**
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param paymentMethod payment method
     * @param updator who update
     * @throws BusinessException business exception.
     */
    void updatePaymentMethod(Long customerAccountId, String customerAccountCode, PaymentMethodEnum paymentMethod, User updator) throws BusinessException;

    /**
     * Get accountOperation created between date from and date tos.
     * 
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param from from date
     * @param to until date
     * @return list of account operation
     * @throws BusinessException business exception.
     */
    List<AccountOperation> consultOperations(Long customerAccountId, String customerAccountCode, Date from, Date to) throws BusinessException;

    /**
     * Transfer amount from fromCustomerAccountId to toCustomerAccountId.
     * 
     * @param fromCustomerAccountId customer account id
     * @param fromCustomerAccountCode customer account code
     * @param toCustomerAccountId customer account id
     * @param toCustomerAccountCode customer account code
     * @param amount transfer amount
     * @throws BusinessException business exception
     * @throws Exception general excepion.
     */
    void transferAccount(Long fromCustomerAccountId, String fromCustomerAccountCode, Long toCustomerAccountId, String toCustomerAccountCode,
            BigDecimal amount) throws BusinessException, Exception;

    /**
     * Transfer amount from fromCustomerAccountId to toCustomerAccountId.
     * 
     * @param fromCustomerAccount customer account
     * @param toCustomerAccount customer account
     * @param amount transfer amount
     * @throws BusinessException business exception
     * @throws Exception exception.
     */
    void transferAccount(CustomerAccount fromCustomerAccount, CustomerAccount toCustomerAccount, BigDecimal amount) throws BusinessException,
            Exception;

    /**
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @return customer account
     * @throws BusinessException business exception.
     */
    CustomerAccount findCustomerAccount(Long customerAccountId, String customerAccountCode) throws BusinessException;
}
