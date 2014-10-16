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
package org.meveo.service.billing.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless @LocalBean
public class InvoiceService extends PersistenceService<Invoice> {

	@EJB
	private ProviderService providerService;
	
	@EJB
	private SellerService sellerService;
	
	@EJB
	private RatedTransactionService ratedTransactionService;

	public Invoice getInvoiceByNumber(String invoiceNumber, String providerCode)
			throws BusinessException {
		try {
			Query q = getEntityManager().createQuery(
					"from Invoice where invoiceNumber = :invoiceNumber and provider=:provider");
			q.setParameter("invoiceNumber", invoiceNumber).setParameter("provider",
					providerService.findByCode(providerCode));
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log.info("Invoice with invoice number #0 was not found. Returning null.", invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log.info("Multiple invoices with invoice number #0 was found. Returning null.",
					invoiceNumber);
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public Invoice getInvoice(EntityManager em,String invoiceNumber, CustomerAccount customerAccount)
			throws BusinessException {
		try {
			Query q = em.createQuery(
					"from Invoice where invoiceNumber = :invoiceNumber and billingAccount.customerAccount=:customerAccount");
			q.setParameter("invoiceNumber", invoiceNumber).setParameter("customerAccount", customerAccount);
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log.info("Invoice with invoice number #0 was not found. Returning null.", invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log.info("Multiple invoices with invoice number #0 was found. Returning null.",
					invoiceNumber);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Invoice getInvoiceByNumber(String invoiceNumber) throws BusinessException {
		try {
			Query q = getEntityManager().createQuery(
					"from Invoice where invoiceNumber = :invoiceNumber");
			q.setParameter("invoiceNumber", invoiceNumber);
			Object invoiceObject = q.getSingleResult();
			return (Invoice) invoiceObject;
		} catch (NoResultException e) {
			log.info("Invoice with invoice number #0 was not found. Returning null.", invoiceNumber);
			return null;
		} catch (NonUniqueResultException e) {
			log.info("Multiple invoices with invoice number #0 was found. Returning null.",
					invoiceNumber);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices(BillingRun billingRun) throws BusinessException {
		try {
			Query q = getEntityManager().createQuery("from Invoice where billingRun = :billingRun");
			q.setParameter("billingRun", billingRun);
			List<Invoice> invoices = q.getResultList();
			return invoices;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoices(BillingAccount billingAccount, String invoiceType)
			throws BusinessException {
		try {
			Query q = getEntityManager()
					.createQuery(
							"from Invoice where billingAccount = :billingAccount and invoiceType=:invoiceType");
			q.setParameter("billingAccount", billingAccount);
			q.setParameter("invoiceType", invoiceType);
			List<Invoice> invoices = q.getResultList();
			log.info("getInvoices: founds #0 invoices with BA_code={} and type=#2 ",
					invoices.size(), billingAccount.getCode(), invoiceType);
			return invoices;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void setInvoiceNumber(Invoice invoice){
		Seller seller = invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		String prefix = seller.getInvoicePrefix();
		if(prefix==null){
			prefix=seller.getProvider().getInvoicePrefix();
		}
		if(prefix==null){
			prefix="";
		}
        long nextInvoiceNb = getNextValue(seller);
        StringBuffer num1 = new StringBuffer("000000000");
        num1.append(nextInvoiceNb + "");
        String invoiceNumber = num1.substring(num1.length() - 9);
        invoice.setInvoiceNumber(prefix + invoiceNumber);
        update(invoice);
	}
	 

	  public synchronized long getNextValue(Seller seller) {
	        long result = 0;
	        if (seller != null) {
	        	if(seller.getCurrentInvoiceNb() != null){
	            long currentInvoiceNbre = seller.getCurrentInvoiceNb();
	            result = 1 + currentInvoiceNbre;
	            seller.setCurrentInvoiceNb(result);
	            sellerService.update(seller,seller.getAuditable().getCreator());
	        	} else {
	        		result=getNextValue(seller.getProvider());
	        	}
	        }
	        return result;
	  }
	  
	  public synchronized long getNextValue(Provider provider) {
	        long result = 0;
	        if (provider != null) {
	            long currentInvoiceNbre = provider.getCurrentInvoiceNb() != null ? provider.getCurrentInvoiceNb() : 0;
	            result = 1 + currentInvoiceNbre;
	            provider.setCurrentInvoiceNb(result);
	            providerService.update(provider);
	        }
	        return result;
	  }
	  
	  @SuppressWarnings("unchecked")
		public List<Invoice> getValidatedInvoicesWithNoPdf(BillingRun br) {
			try {
				QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
				qb.addCriterionEntity("i.billingRun.status",BillingRunStatusEnum.VALIDATED);
				qb.addSql("i.pdf is null");
				if(br!=null){
					qb.addCriterionEntity("i.billingRun", br);
				}
				return (List<Invoice>)qb.getQuery(getEntityManager()).getResultList();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	  @SuppressWarnings("unchecked")
		public List<Invoice> getInvoicesWithNoAccountOperation(BillingRun br) {
			try {
				QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
				qb.addCriterionEntity("i.billingRun.status",BillingRunStatusEnum.VALIDATED);
				qb.addSql("i.recordedInvoice is null");
				if(br!=null){
					qb.addCriterionEntity("i.billingRun", br);
				}
				return (List<Invoice>)qb.getQuery(getEntityManager()).getResultList();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	  public void createAgregatesAndInvoice(BillingAccount billingAccount,BillingRun billingRun) throws BusinessException, Exception {
			
				Long startDate=System.currentTimeMillis();
	            BillingCycle billingCycle = billingRun.getBillingCycle();
	            if (billingCycle == null) {
	                billingCycle = billingAccount.getBillingCycle();
	            }
	            Invoice invoice = new Invoice();
	            invoice.setBillingAccount(billingAccount);
	            invoice.setBillingRun(billingRun);
	            invoice.setAuditable(billingRun.getAuditable());
	            invoice.setProvider(billingRun.getProvider());
	            Date invoiceDate = new Date();
	            invoice.setInvoiceDate(invoiceDate);

	            Integer delay = billingCycle.getDueDateDelay();
	            Date dueDate = invoiceDate;
	            if (delay != null) {
	                dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
	            }
	            invoice.setDueDate(dueDate);

	            invoice.setPaymentMethod(billingAccount.getPaymentMethod());
	            invoice.setProvider(billingRun.getProvider());
	            create(invoice);
	            ratedTransactionService.createInvoiceAndAgregates(billingRun, billingAccount,invoice);

		        ratedTransactionService.updateRatedTransactions(billingRun, billingAccount,invoice);
		        
	            StringBuffer num1 = new StringBuffer("000000000");
	            num1.append(invoice.getId() + "");
	            String invoiceNumber = num1.substring(num1.length() - 9);
	            int key = 0;
	            for (int i = 0; i < invoiceNumber.length(); i++) {
	                key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
	            }
	            invoice.setTemporaryInvoiceNumber(invoiceNumber + "-" + key % 10);
	            update(invoice);
	            Long endDate=System.currentTimeMillis();
	            log.info("createAgregatesAndInvoice BR_ID="+billingRun.getId()+", BA_ID="+billingAccount.getId()+", Time en ms="+(endDate-startDate));
		        
		}

}
