/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.oudaya.billing.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.DateUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.config.MeveoConfig;
import org.meveo.core.inputhandler.TaskExecution;
import org.meveo.core.process.step.AbstractProcessStep;
import org.meveo.core.process.step.StepExecution;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunList;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.Wallet;
import org.meveo.model.crm.Provider;
import org.meveo.oudaya.InvoicingTicket;
import org.meveo.oudaya.OudayaConfig;
import org.meveo.persistence.MeveoPersistence;

/**
 * @author R.AITYAAZZA
 */
public class BillingProcess extends AbstractProcessStep<InvoicingTicket> {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    protected final Logger logger = Logger.getLogger(this.getClass());

    protected EntityManager em;

    public BillingProcess(AbstractProcessStep<InvoicingTicket> nextStep, MeveoConfig config) {
        super(nextStep, config);
        em = MeveoPersistence.getEntityManager();
    }

    protected boolean execute(StepExecution<InvoicingTicket> stepExecution) {
        logger.info("start BillingProcess....");
        BillingRun billingRun = null;
        EntityTransaction transac = em.getTransaction();
        if (!transac.isActive()) {
            transac.begin();
        }
        try {
            InvoicingTicket ticket = stepExecution.getTicket();

            billingRun = ticket.getBillingRun();

            TaskExecution<InvoicingTicket> taskExecution = stepExecution.getTaskExecution();

            // TODO temp solution until #621 ticket will be solved.
            if (taskExecution.getProvider() == null) {
                taskExecution.setProvider(billingRun.getProvider());
            }

            switch (billingRun.getStatus()) {
            case NEW:
                createBillingRunLists(billingRun);

                break;
            case ON_GOING:
                createAgregatesAndInvoice(billingRun);
                break;
            case VALIDATED:
                if (!billingRun.isDisabled()) {
                    validateProcess(billingRun);
                }

                break;
            default:
                break;
            }
            setAccepted(stepExecution);
        } catch (Exception e) {
            logger.error(String.format("BillingProcess %s", e));
            e.printStackTrace();
            if (billingRun.getStatus() == BillingRunStatusEnum.NEW) {
                setNotAccepted(stepExecution, "ERROR_CREATING_BILLING_RUN_LIST");
            } else if (billingRun.getStatus() == BillingRunStatusEnum.ON_GOING) {
                setNotAccepted(stepExecution, "ERROR_CREATING_AGREGATES_AND_INVOICE");
            } else if (billingRun.getStatus() == BillingRunStatusEnum.VALIDATED) {
                setNotAccepted(stepExecution, "ERROR_VALIDATING_INVOICES");
            } else {
                setNotAccepted(stepExecution, "TECHNICAL_ERROR");
            }

            transac.rollback();

        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void createBillingRunLists(BillingRun billingRun) throws BusinessException, Exception {
        logger.info("createBillingRunLists..........");
        BillingCycle billingCycle = billingRun.getBillingCycle();

        boolean entreprise = billingRun.getProvider().isEntreprise();

        logger.info("createBillingRunLists entrepriseFlag=" + entreprise);

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();

        List<BillingAccount> billingAccounts = null;
        if (billingCycle != null) {
            billingAccounts = billingCycle.getBillingAccounts();
        } else {
            billingAccounts = billingRun.getSelectedBillingAccount();
        }

        BigDecimal totalAmountTax = BigDecimal.ZERO;
        BigDecimal totalAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalAmountWithTax = BigDecimal.ZERO;
        BigDecimal totalAmount2WithoutTax = BigDecimal.ZERO;
        int billableBillingAccount = 0;
        endDate = endDate != null ? endDate : new Date();
        logger.info("startDate=" + startDate);
        logger.info("endDate=" + endDate);

        for (BillingAccount billingAccount : billingAccounts) {
            logger.info("billingAccount.getNextInvoiceDate()=" + billingAccount.getNextInvoiceDate());

            if (billingCycle == null
                    || ((startDate == null || startDate.before(billingAccount.getNextInvoiceDate())) && endDate
                            .after(billingAccount.getNextInvoiceDate()))) {
                logger.info("create billingRunList...");

                BillingRunList billingRunList = new BillingRunList();
                billingRunList.setAuditable(billingRun.getAuditable());
                billingRunList.setProvider(billingRun.getProvider());
                billingRunList.setRatedAmountTax(BigDecimal.ZERO);
                billingRunList.setRatedAmountWithoutTax(BigDecimal.ZERO);
                billingRunList.setRatedAmountWithTax(BigDecimal.ZERO);
                billingRunList.setRatedAmount2WithoutTax(BigDecimal.ZERO);
                int ratedTransactionsCount = 0;
                boolean billable = false;
                for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
                    Wallet wallet = userAccount.getWallet();

                    List<RatedTransaction> ratedTransactions = (List<RatedTransaction>) em.createQuery(
                            "from RatedTransaction where wallet=:walletId and invoice is null and status=:status and doNotTriggerInvoicing=:invoicing and amountWithoutTax<>:zeroValue")
                            .setParameter("walletId", wallet).setParameter("status", RatedTransactionStatusEnum.OPEN).setParameter("invoicing", false).setParameter("zeroValue", BigDecimal.ZERO)
                            .getResultList();
                    ratedTransactionsCount = ratedTransactionsCount + ratedTransactions.size();
                    for (RatedTransaction ratedTransaction : ratedTransactions) {
                        billingRunList.setRatedAmountTax(billingRunList.getRatedAmountTax().add(
                                ratedTransaction.getAmountTax()));
                        billingRunList.setRatedAmountWithoutTax(billingRunList.getRatedAmountWithoutTax().add(
                                ratedTransaction.getAmountWithoutTax()));
                        billingRunList.setRatedAmountWithTax(billingRunList.getRatedAmountWithTax().add(
                                ratedTransaction.getAmountWithTax()));
                        billingRunList.setRatedAmount2WithoutTax(billingRunList.getRatedAmount2WithoutTax().add(
                                ratedTransaction.getAmountWithoutTax()));
                        ratedTransaction.setBillingRun(billingRun);
                        billingRunList.setBillingRun(billingRun);
                        billable = true;
                    }
                }
                if (billable) {
                    billableBillingAccount++;
                }

                if (ratedTransactionsCount == 0) {
                    Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate();
                    billingAccount.setNextInvoiceDate(nextCalendarDate);
                    em.merge(billingAccount);
                }else{
                	billingRunList.setBillingAccount(billingAccount);
                	totalAmountTax = round(totalAmountTax.add(billingRunList.getRatedAmountTax()), 2);
                    totalAmountWithoutTax = round(totalAmountWithoutTax.add(billingRunList.getRatedAmountWithoutTax()), 2);
                    totalAmountWithTax = round(totalAmountWithTax.add(billingRunList.getRatedAmountWithTax()), 2);
                    totalAmount2WithoutTax = round(totalAmount2WithoutTax.add(billingRunList.getRatedAmount2WithoutTax()),
                            2);
                }

                

            }
        }
        billingRun.setPrAmountTax(totalAmountTax);
        billingRun.setPrAmountWithoutTax(totalAmountWithoutTax);
        if (entreprise) {
            billingRun.setPrAmountWithTax(totalAmountWithTax);
        } else {
            billingRun.setPrAmountWithTax(totalAmount2WithoutTax);
        }
        billingRun.setBillingAccountNumber(billingAccounts.size());
        billingRun.setBillableBillingAcountNumber(billableBillingAccount);
        billingRun.setProcessDate(new Date());
        billingRun.setStatus(BillingRunStatusEnum.WAITING);

        billingRun = em.merge(billingRun);
        em.flush();
        if (billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC
                || billingRun.getProvider().isAutomaticInvoicing()) {
            createAgregatesAndInvoice(billingRun);
        }
    }

	public void createAgregatesAndInvoice(BillingRun billingRun) throws BusinessException, Exception {

	        logger.info("start createAgregatesAndInvoice......");
	        boolean entreprise = billingRun.getProvider().isEntreprise();
	        Set<BillingRunList> billingRunLists = billingRun.getBillingRunLists();
	        for (BillingRunList billingRunList : billingRunLists) {
	            BillingAccount billingAccount = billingRunList.getBillingAccount();
	            BillingCycle billingCycle = billingRun.getBillingCycle();
	            if (billingCycle == null) {
	                billingCycle = billingAccount.getBillingCycle();
	            }
	            Invoice invoice = new Invoice();
	            invoice.setBillingAccount(billingAccount);
				setInvoiceType(invoice) ;
	            invoice.setBillingRun(billingRun);
	            invoice.setAuditable(billingRun.getAuditable());
	            invoice.setProvider(billingRun.getProvider());

	            /*
	             * Date invoiceDate = billingAccount.getNextInvoiceDate(); if
	             * (invoiceDate == null) { invoiceDate = new Date(); }
	             */
	            Date invoiceDate = new Date();
	            invoice.setInvoiceDate(invoiceDate);

	            Integer delay = billingCycle.getDueDateDelay();
	            Date dueDate = invoiceDate;
	            if (delay != null) {
	                dueDate = DateUtils.addDaysToDate(invoiceDate, delay);
	            }
	            invoice.setDueDate(dueDate);

	            invoice.setPaymentMethod(billingAccount.getPaymentMethod());

	            BigDecimal nonEnterprisePriceWithTax = BigDecimal.ZERO;

	            for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
	                Wallet wallet = userAccount.getWallet();

	                Set<RatedTransaction> ratedTransactions = billingRun.getRatedTransactions();
	                Map<Long, SubCategoryInvoiceAgregate> subCatInvoiceAgregateMap = new HashMap<Long, SubCategoryInvoiceAgregate>();
	                Map<Long, CategoryInvoiceAgregate> catInvoiceAgregateMap = new HashMap<Long, CategoryInvoiceAgregate>();
	                Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

	                // we first compute Sub Category aggregates and create its empty
	                // Tax and Category aggregates
	                for (RatedTransaction ratedTransaction : ratedTransactions) {
	                    if (ratedTransaction.getInvoice() == null && ratedTransaction.getWallet() != null
	                            && ratedTransaction.getWallet().getId() == wallet.getId()) {
	                        SubCategoryInvoiceAgregate invoiceAgregateF = null;
	                        long invoiceSubCatId = ratedTransaction.getInvoiceSubCategory().getId();
	                        if (subCatInvoiceAgregateMap.containsKey(invoiceSubCatId)) {
	                            invoiceAgregateF = subCatInvoiceAgregateMap.get(invoiceSubCatId);
	                        } else {
	                            invoiceAgregateF = new SubCategoryInvoiceAgregate();
	                            invoiceAgregateF.setAuditable(billingRun.getAuditable());
	                            invoiceAgregateF.setProvider(billingRun.getProvider());
	                            invoiceAgregateF.setInvoice(invoice);
	                            invoiceAgregateF.setBillingRun(billingRun);
	                            invoiceAgregateF.setAccountingCode(ratedTransaction.getInvoiceSubCategory()
	                                    .getAccountingCode());
	                            invoiceAgregateF.setSubCategoryTax(ratedTransaction.getInvoiceSubCategory().getTax());
	                            subCatInvoiceAgregateMap.put(invoiceSubCatId, invoiceAgregateF);
	                        }

	                        ratedTransaction.setInvoice(invoice);
	                        fillAgregates(invoiceAgregateF, wallet);
	                        invoiceAgregateF.addQuantity(ratedTransaction.getUsageQuantity());
	                        logger.info("createAgregates code=" + ratedTransaction.getAmountWithoutTax() + ",amoutHT="
	                                + ratedTransaction.getAmountWithoutTax());
	                        invoiceAgregateF.addAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
	                        invoiceAgregateF.setProvider(billingRun.getProvider());
	                        if (!entreprise) {
	                            nonEnterprisePriceWithTax = nonEnterprisePriceWithTax.add(ratedTransaction
	                                    .getPrAmountWithoutTax());
	                        }

	                        // start agregate T
	                        TaxInvoiceAgregate invoiceAgregateT = null;
	                        Long taxId = ratedTransaction.getInvoiceSubCategory().getTax().getId();
	                        if (taxInvoiceAgregateMap.containsKey(taxId)) {
	                            invoiceAgregateT = taxInvoiceAgregateMap.get(taxId);
	                        } else {
	                            invoiceAgregateT = new TaxInvoiceAgregate();
	                            invoiceAgregateT.setAuditable(billingRun.getAuditable());
	                            invoiceAgregateT.setProvider(billingRun.getProvider());
	                            invoiceAgregateT.setInvoice(invoice);
	                            invoiceAgregateT.setBillingRun(billingRun);
	                            invoiceAgregateT.setTax(ratedTransaction.getInvoiceSubCategory().getTax());
	                            invoiceAgregateT.setAccountingCode(ratedTransaction.getInvoiceSubCategory().getTax()
	                                    .getAccountingCode());

	                            taxInvoiceAgregateMap.put(taxId, invoiceAgregateT);
	                        }
	                        if(ratedTransaction.getInvoiceSubCategory().getTax().getPercent().compareTo(BigDecimal.ZERO)==0){
	                        	invoiceAgregateT.addAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
	                        	invoiceAgregateT.addAmountWithTax(ratedTransaction.getAmountWithTax());
	                        	invoiceAgregateT.addAmountTax(ratedTransaction.getAmountTax());
	                        }
	                        fillAgregates(invoiceAgregateT, wallet);
	                        if(invoiceAgregateF.getSubCategoryTax().getPercent().compareTo(BigDecimal.ZERO)!=0) {
	                        	invoiceAgregateT.setTaxPercent(invoiceAgregateF.getSubCategoryTax().getPercent());
	                        }
	                        invoiceAgregateT.setProvider(billingRun.getProvider());
	                        invoiceAgregateF.setSubCategoryTax(ratedTransaction.getInvoiceSubCategory().getTax());
	                        invoiceAgregateF.setInvoiceSubCategory(ratedTransaction.getInvoiceSubCategory());
	                        // end agregate T

	                        // start agregate R
	                        CategoryInvoiceAgregate invoiceAgregateR = null;
	                        Long invoiceCategoryId = ratedTransaction.getInvoiceSubCategory().getInvoiceCategory().getId();
	                        if (catInvoiceAgregateMap.containsKey(invoiceCategoryId)) {
	                            invoiceAgregateR = catInvoiceAgregateMap.get(invoiceCategoryId);
	                        } else {
	                            invoiceAgregateR = new CategoryInvoiceAgregate();
	                            invoiceAgregateR.setAuditable(billingRun.getAuditable());
	                            invoiceAgregateR.setProvider(billingRun.getProvider());

	                            invoiceAgregateR.setInvoice(invoice);
	                            invoiceAgregateR.setBillingRun(billingRun);
	                            catInvoiceAgregateMap.put(invoiceCategoryId, invoiceAgregateR);
	                        }

	                        fillAgregates(invoiceAgregateR, wallet);

	                        invoiceAgregateR.setInvoiceCategory(ratedTransaction.getInvoiceSubCategory()
	                                .getInvoiceCategory());
	                        invoiceAgregateR.setProvider(billingRun.getProvider());
	                        invoiceAgregateF.setCategoryInvoiceAgregate(invoiceAgregateR);
	                        invoiceAgregateF.setTaxInvoiceAgregate(invoiceAgregateT);
	                        ratedTransaction.setInvoiceAgregateR(invoiceAgregateR);
	                        ratedTransaction.setInvoiceAgregateF(invoiceAgregateF);
	                        ratedTransaction.setInvoiceAgregateT(invoiceAgregateT);

	                        logger.info("createAgregates invoiceAgregateF amountHT="
	                                + invoiceAgregateF.getAmountWithoutTax());
	                    }

	                }

	                SubCategoryInvoiceAgregate biggestSubCat = null;
	                BigDecimal biggestAmount = new BigDecimal("-100000000");

	                // round the amount without Tax
	                // compute the largest subcategory
	                for (Map.Entry<Long, SubCategoryInvoiceAgregate> subCatMap : subCatInvoiceAgregateMap.entrySet()) {
	                    SubCategoryInvoiceAgregate subCat = subCatMap.getValue();
	                    // first we round the amount without tax

	                    logger.info("subcat " + subCat.getAccountingCode() + " ht=" + subCat.getAmountWithoutTax() + " ->"
	                            + round(subCat.getAmountWithoutTax(), 2));
	                    subCat.setAmountWithoutTax(round(subCat.getAmountWithoutTax(), 2));
	                    // add it to taxAggregate and CategoryAggregate
	                    if(subCat.getSubCategoryTax().getPercent().compareTo(BigDecimal.ZERO)!=0){
	                    	TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(subCat.getSubCategoryTax().getId());
	                    	invoiceAgregateT.addAmountWithoutTax(subCat.getAmountWithoutTax());
	                    	logger.info("  tax " + subCat.getTaxInvoiceAgregate().getTaxPercent() + " ht ->"
	                            + invoiceAgregateT.getAmountWithoutTax());
	                    }
	                    subCat.getCategoryInvoiceAgregate().addAmountWithoutTax(subCat.getAmountWithoutTax());
	                    logger.info("  cat " + subCat.getCategoryInvoiceAgregate().getId() + " ht ->"
	                            + subCat.getCategoryInvoiceAgregate().getAmountWithoutTax());
	                    if (subCat.getAmountWithoutTax().compareTo(biggestAmount) > 0) {
	                        biggestAmount = subCat.getAmountWithoutTax();
	                        biggestSubCat = subCat;
	                    }
	                }

	                // compute the tax
	                for (Map.Entry<Long, TaxInvoiceAgregate> taxCatMap : taxInvoiceAgregateMap.entrySet()) {
	                    TaxInvoiceAgregate taxCat = taxCatMap.getValue();
	                    if(taxCat.getTax().getPercent().compareTo(BigDecimal.ZERO)!=0){
	                    	// then compute the tax
	                    	taxCat.setAmountTax(taxCat.getAmountWithoutTax().multiply(taxCat.getTaxPercent()).divide(HUNDRED));
	                    	// then round the tax
	                    	taxCat.setAmountTax(round(taxCat.getAmountTax(), 2));

	                    	// and compute amount with tax
	                    	taxCat.setAmountWithTax(round(taxCat.getAmountWithoutTax().add(taxCat.getAmountTax()), 2));
	                    	logger.info("  tax2 ht ->" + taxCat.getAmountWithoutTax());
	                    } else {
	                    	//compute the percent
	                    	taxCat.setTaxPercent(round(taxCat.getAmountTax().divide(taxCat.getAmount()).multiply(HUNDRED),2));
	                    }

	                }

	                for (Map.Entry<Long, TaxInvoiceAgregate> tax : taxInvoiceAgregateMap.entrySet()) {
	                    TaxInvoiceAgregate taxInvoiceAgregate = tax.getValue();
	                    invoice.addAmountTax(round(taxInvoiceAgregate.getAmountTax(), 2));
	                    invoice.addAmountWithoutTax(round(taxInvoiceAgregate.getAmountWithoutTax(), 2));
	                    invoice.addAmountWithTax(round(taxInvoiceAgregate.getAmountWithTax(), 2));
	                }

	                // if not enterprise we must change amountWithoutTax in the
	                // biggestSubCat, its associated TaxAggregate, its associated
	                // CategoryAggregate and its associates Invoice
	                if (!entreprise && biggestSubCat != null) {
	                    // TODO log those steps
	                    BigDecimal delta = nonEnterprisePriceWithTax.subtract(invoice.getAmountWithTax());
	                    logger.info("delta= " + nonEnterprisePriceWithTax + " - " + invoice.getAmountWithTax() + "="
	                            + delta);
	                    biggestSubCat.setAmountWithoutTax(round(biggestSubCat.getAmountWithoutTax().add(delta), 2));

	                    TaxInvoiceAgregate invoiceAgregateT = taxInvoiceAgregateMap.get(biggestSubCat.getSubCategoryTax()
	                            .getId());
	                    logger.info("  tax3 ht ->" + invoiceAgregateT.getAmountWithoutTax());
	                    invoiceAgregateT.setAmountWithoutTax(round(invoiceAgregateT.getAmountWithoutTax().add(delta), 2));
	                    logger.info("  tax4 ht ->" + invoiceAgregateT.getAmountWithoutTax());
	                    CategoryInvoiceAgregate invoiceAgregateR = biggestSubCat.getCategoryInvoiceAgregate();
	                    invoiceAgregateR.setAmountWithoutTax(round(invoiceAgregateR.getAmountWithoutTax().add(delta), 2));

	                    invoice.setAmountWithoutTax(round(invoice.getAmountWithoutTax().add(delta), 2));
	                    invoice.setAmountWithTax(round(nonEnterprisePriceWithTax, 2));
	                }

	            }
	            invoice.setProvider(billingRun.getProvider());
	            em.persist(invoice);
	            StringBuffer num1 = new StringBuffer("000000000");
	            num1.append(invoice.getId() + "");
	            String invoiceNumber = num1.substring(num1.length() - 9);
	            int key = 0;
	            for (int i = 0; i < invoiceNumber.length(); i++) {
	                key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
	            }
	            invoice.setTemporaryInvoiceNumber(invoiceNumber + "-" + key % 10);
	        }

	        billingRun.setStatus(BillingRunStatusEnum.TERMINATED);
	        billingRun = em.merge(billingRun);
	        em.flush();
	        List<Invoice> invoices = billingRun.getInvoices();
	        XMLInvoiceCreator XMLInvoiceCreator = new XMLInvoiceCreator();
	        String invoicesDir = OudayaConfig.getOudayaInvoicesDirectory();
	        File billingRundir = new File(invoicesDir + File.separator + billingRun.getId() + "-processing");
	        billingRundir.mkdirs();

	        for (Invoice invoice : invoices) {
	            em.refresh(invoice);
	            logger.info("invoice.getRatedTransactions().size()=" + invoice.getRatedTransactions().size());
	            boolean createXmlInvoice = false;
	            for (RatedTransaction ratedTrnsaction : invoice.getRatedTransactions()) {
	                BigDecimal transactionAmount = entreprise ? ratedTrnsaction.getAmountWithTax() : ratedTrnsaction
	                        .getPrAmountWithoutTax();
	                if (transactionAmount != null && !transactionAmount.equals(BigDecimal.ZERO) && !ratedTrnsaction.isDoNotTriggerInvoicing()) {
	                    createXmlInvoice = true;
	                    break;
	                }
	            }
	            if (createXmlInvoice) {
	            	XMLInvoiceCreator.createXMLInvoice(invoice, billingRundir);
	            }

	        }
	        File billingRundirTerminated = new File(invoicesDir + File.separator + billingRun.getId());
	        billingRundir.renameTo(billingRundirTerminated);
	    }

    private void fillAgregates(InvoiceAgregate invoiceAgregate, Wallet wallet) {
        invoiceAgregate.setBillingAccount(wallet.getUserAccount().getBillingAccount());
        invoiceAgregate.setUserAccount(wallet.getUserAccount());
        int itemNumber = invoiceAgregate.getItemNumber() != null ? invoiceAgregate.getItemNumber() + 1 : 1;
        invoiceAgregate.setItemNumber(itemNumber);
    }

    public void validateProcess(BillingRun billingRun) throws BusinessException, NamingException {
        String invoicesDir = OudayaConfig.getOudayaInvoicesDirectory();
        File billingRundir = new File(invoicesDir + File.separator + billingRun.getId());

        for (Invoice invoice : billingRun.getInvoices()) {
            setInvoiceNumber(billingRun.getProvider(), invoice);
            File invoiceXML = new File(billingRundir + File.separator + invoice.getTemporaryInvoiceNumber() + ".xml");
            boolean moved = FileUtils.moveFile(invoicesDir + File.separator + billingRun.getId() + "-validated",
                    invoiceXML, invoice.getTemporaryInvoiceNumber() + ".xml");
            logger.info("validateProcess moved" + moved + ", file" + invoice.getTemporaryInvoiceNumber() + ".xml");
            BillingAccount billingAccount = invoice.getBillingAccount();
            Date nextCalendarDate = billingAccount.getBillingCycle().getNextCalendarDate();
            billingAccount.setNextInvoiceDate(nextCalendarDate);
            em.merge(billingAccount);
        }
        boolean deleted = deleteDirectory(billingRundir);
        logger.info("validateProcess deleted" + deleted);
        billingRun.setDisabled(true);
        em.merge(billingRun);
        em.flush();

    }

    public static BigDecimal round(BigDecimal amount, int decimal) {
        if (amount == null) {
            return null;
        }
        amount = amount.setScale(decimal, RoundingMode.HALF_UP);
        return amount;
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public synchronized long getNextValue(Provider provider) {
        long result = 0;
        if (provider != null) {
            long currentInvoiceNbre = provider.getCurrentInvoiceNb() != null ? provider.getCurrentInvoiceNb() : 0;
            result = 1 + currentInvoiceNbre;
            provider.setCurrentInvoiceNb(result);
            em.merge(provider);
            em.flush();
        }
        return result;
    }

    public void setInvoiceNumber(Provider provider, Invoice invoice) {
        String prefix = provider.getInvoicePrefix();
        if (prefix == null) {
            prefix = "";
        }
        long nextInvoiceNb = getNextValue(provider);
        StringBuffer num1 = new StringBuffer("000000000");
        num1.append(nextInvoiceNb + "");
        String invoiceNumber = num1.substring(num1.length() - 9);
        int key = 0;
        for (int i = 0; i < invoiceNumber.length(); i++) {
            key = key + Integer.parseInt(invoiceNumber.substring(i, i + 1));
        }
        invoice.setInvoiceNumber(prefix + invoiceNumber + "-" + key % 10);
        em.merge(invoice);
    }
	
	 protected void setInvoiceType(Invoice invoice) {
		// Implemented only for EDF
		
	}

}
