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
package org.meveo.service.selfcare.remote;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.EmailNotFoundException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.IPersistenceService;

@Remote
public interface ISelfcareService extends IPersistenceService<CustomerAccount> {

    public Boolean authenticate(String username, String password) throws BusinessException,EmailNotFoundException;

    public void sendPassword(String email) throws BusinessException,EmailNotFoundException;

    public Boolean updatePassword(String username, String oldpassword, String password) throws BusinessException;

    public CustomerAccount getCustomerAccount(String username) throws BusinessException;

    public List<BillingAccount> getBillingAccounts(String username) throws BusinessException;

    public List<Invoice> getBillingAccountInvoices(String code) throws BusinessException;

    public List<Invoice> getBillingAccountValidatedInvoices(String code) throws BusinessException;

    public BigDecimal getAccountBalance(String code) throws BusinessException;

    public byte[] getPDFInvoice(String invoiceNumber, String providerCode) throws BusinessException;
    public byte[] getPDFInvoice(String invoiceNumber) throws BusinessException;

    public void sendMail(String from, List<String> to, List<String> cc, String subject, String body, List<File> files)
            throws BusinessException;
    
    public void sendEmailCreationSpace(String email) throws BusinessException,EmailNotFoundException ;
    
}
