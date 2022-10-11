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

package org.meveo.admin.job.importexport;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ExceptionUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.CustomerImportHisto;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.customer.ErrorCustomer;
import org.meveo.model.jaxb.customer.ErrorCustomerAccount;
import org.meveo.model.jaxb.customer.ErrorSeller;
import org.meveo.model.jaxb.customer.Errors;
import org.meveo.model.jaxb.customer.Seller;
import org.meveo.model.jaxb.customer.Sellers;
import org.meveo.model.jaxb.customer.WarningCustomerAccount;
import org.meveo.model.jaxb.customer.WarningSeller;
import org.meveo.model.jaxb.customer.Warnings;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.CustomerImportHistoService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.AccountImportService;
import org.meveo.service.crm.impl.CustomerImportService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ImportWarningException;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class ImportCustomersJobBean {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private CustomerImportHistoService customerImportHistoService;

    @EJB
    private CustomerImportService customerImportService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private CustomerService customerService;

    @Inject
    private SellerService sellerService;

    @Inject
    private TitleService titleService;

    @Inject
    private AccountImportService accountImportService;

    // @Inject
    // @ApplicationProvider
    // protected Provider appProvider;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private JobExecutionService jobExecutionService;

    private Sellers sellersWarning;
    private Sellers sellersError;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private int nbCustomers;
    private int nbCustomersError;
    private int nbCustomersWarning;
    private int nbCustomersIgnored;
    private int nbCustomersCreated;

    private int nbSellers;
    private int nbSellersError;
    private int nbSellersWarning;
    private int nbSellersIgnored;
    private int nbSellersCreated;

    private int nbSellersUpdated;
    private int nbCustomersUpdated;
    private int nbCustomerAccountsUpdated;

    private int nbCustomerAccounts;
    private int nbCustomerAccountsError;
    private int nbCustomerAccountsWarning;
    private int nbCustomerAccountsIgnored;
    private int nbCustomerAccountsCreated;
    private CustomerImportHisto customerImportHisto;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    public void execute(JobExecutionResultImpl result) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "customers" + File.separator;
        String dirIN = importDir + "input";
        log.info("dirIN=" + dirIN);
        String dirOK = importDir + "output";
        String dirKO = importDir + "reject";
        String prefix = paramBean.getProperty("connectorCRM.importCustomers.prefix", "CUSTOMER_");
        String ext = paramBean.getProperty("connectorCRM.importCustomers.extension", "xml");

        File dir = new File(dirIN);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<File> files = getFilesToProcess(dir, prefix, ext);
        int numberOfFiles = files.size();
        log.info("InputFiles job " + numberOfFiles + " to import");

        for (File file : files) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            File currentFile = null;
            try {
                log.info("InputFiles job " + file.getName() + " in progres");
                currentFile = FileUtils.addExtension(file, ".processing");
                importFile(currentFile, file.getName(), result.getJobInstance().getId());
                FileUtils.moveFile(dirOK, currentFile, file.getName());
                log.info("InputFiles job " + file.getName() + " done");
            } catch (Exception e) {
                log.info("InputFiles job " + file.getName() + " failed");
                FileUtils.moveFile(dirKO, currentFile, file.getName());
                log.error("failed to import file", e);
            } finally {
                if (currentFile != null)
                    currentFile.delete();
            }
        }

        result.setNbItemsToProcess(nbCustomers);
        result.setNbItemsCorrectlyProcessed(nbCustomersCreated);
        result.setNbItemsProcessedWithError(nbCustomersError);
        result.setNbItemsProcessedWithWarning(nbCustomersWarning);
    }

    /**
     * @param dir folder
     * @param prefix prefix file
     * @param ext extension
     * @return list of files
     */
    private synchronized List<File> getFilesToProcess(File dir, String prefix, String ext) {
        List<File> files = new ArrayList<File>();
        ImportFileFiltre filtre = new ImportFileFiltre(prefix, ext);
        File[] listFile = dir.listFiles(filtre);

        if (listFile == null) {
            return files;
        }

        for (File file : listFile) {
            if (file.isFile()) {
                files.add(file);
                // we just process one file
                return files;
            }
        }

        return files;
    }

    /**
     * @param file file to import
     * @param fileName file's name
     * @param jobInstanceId the job Instance Id
     * @throws JAXBException jaxb exception
     * @throws Exception exception
     */
    private void importFile(File file, String fileName, Long jobInstanceId) throws JAXBException, Exception {

        log.info("start import file :" + fileName);

        sellersWarning = new Sellers();
        sellersError = new Sellers();

        nbSellersUpdated = 0;
        nbCustomersUpdated = 0;
        nbCustomerAccountsUpdated = 0;

        nbSellers = 0;
        nbSellersError = 0;
        nbSellersWarning = 0;
        nbSellersIgnored = 0;
        nbSellersCreated = 0;

        nbCustomers = 0;
        nbCustomersError = 0;
        nbCustomersWarning = 0;
        nbCustomersIgnored = 0;
        nbCustomersCreated = 0;

        nbCustomerAccounts = 0;
        nbCustomerAccountsError = 0;
        nbCustomerAccountsWarning = 0;
        nbCustomerAccountsIgnored = 0;
        nbCustomerAccountsCreated = 0;

        customerImportHisto = new CustomerImportHisto();

        customerImportHisto.setExecutionDate(new Date());
        customerImportHisto.setFileName(fileName);

        if (file.length() < 83) {
            createSellerWarning(null, "File empty");
            generateReport(fileName);
            createHistory();
            return;
        }

        Sellers sellers = (Sellers) JAXBUtils.unmarshaller(Sellers.class, file);
        log.debug("parsing file ok");
        int i = -1;

        List<Seller> sellerList = sellers.getSeller();
        nbSellers = sellerList.size();
        if (nbSellers == 0) {
            createSellerWarning(null, "File empty");
        }

        int ji = 0;
        for (org.meveo.model.jaxb.customer.Seller sell : sellerList) {
            i++;
            org.meveo.model.admin.Seller seller = null;
            try {
                log.debug("seller found  code:" + sell.getCode());

                if (sellerCheckError(sell)) {
                    nbSellersError++;
                    log.error("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode() + ", status:Error");
                    continue;
                }

                seller = createSeller(sell, fileName, i);

                List<org.meveo.model.jaxb.customer.Customer> customerList = sell.getCustomers().getCustomer();
                 for (org.meveo.model.jaxb.customer.Customer cust : customerList) {
                    ji++;
                    if (ji % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                        break;
                    }
                    if (customerCheckError(sell, cust)) {
                        nbCustomersError++;
                        log.error("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Error");
                        continue;
                    }

                    nbCustomers++;
                    createCustomer(fileName, seller, sell, cust, i);
                }
            } catch (Exception e) {
                createSellerError(sell, ExceptionUtils.getRootCause(e).getMessage());
                nbSellersError++;
                log.error("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode() + ", status:Error");
                log.error("Failed to import customers job", e);
            }
        }

        generateReport(fileName);
        createHistory();
        log.info("end import file ");
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private synchronized org.meveo.model.admin.Seller createSeller(Seller sell, String fileName, int i) throws BusinessException {
        org.meveo.model.admin.Seller seller = null;
        try {
            seller = sellerService.findByCode(sell.getCode());
        } catch (Exception e) {
            log.warn("error while getting seller ", e);
        }

        if (seller != null) {
            nbSellersUpdated++;
            seller.setDescription(sell.getDescription());
            if (!StringUtils.isBlank(sell.getTradingCountryCode())) {
                seller.setTradingCountry(tradingCountryService.findByCode(sell.getTradingCountryCode()));
            } else {
                seller.setTradingCountry(null);
            }
            if (!StringUtils.isBlank(sell.getTradingCurrencyCode())) {
                seller.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(sell.getTradingCurrencyCode()));
            } else {
                seller.setTradingCurrency(null);
            }
            if (!StringUtils.isBlank(sell.getTradingLanguageCode())) {
                seller.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(sell.getTradingLanguageCode()));
            } else {
                seller.setTradingLanguage(null);
            }
            customerImportService.updateSeller(seller);
            log.info("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode() + ", status:Updated");
        } else {
            nbSellersCreated++;
            log.info("File:" + fileName + ", typeEntity:Seller, index:" + i + ", code:" + sell.getCode() + ", status:Created");

            seller = new org.meveo.model.admin.Seller();
            seller.setCode(sell.getCode());
            seller.setDescription(sell.getDescription());
            if (!StringUtils.isBlank(sell.getTradingCountryCode())) {
                seller.setTradingCountry(tradingCountryService.findByCode(sell.getTradingCountryCode()));
            }
            if (!StringUtils.isBlank(sell.getTradingCurrencyCode())) {
                seller.setTradingCurrency(tradingCurrencyService.findByTradingCurrencyCode(sell.getTradingCurrencyCode()));
            }
            if (!StringUtils.isBlank(sell.getTradingLanguageCode())) {
                seller.setTradingLanguage(tradingLanguageService.findByTradingLanguageCode(sell.getTradingLanguageCode()));
            }
            customerImportService.createSeller(seller);
        }

        return seller;
    }

    /**
     * @param fileName file's name
     * @param seller seller
     * @param sell jaxb seller
     * @param cust jaxb customer
     * @param i index
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void createCustomer(String fileName, org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust,
            int i) {

        nbSellers++;
        int j = 0;
        Customer customer = null;

        try {
            log.debug("customer found code={}", cust.getCode());
            boolean ignoreCheck = cust.getIgnoreCheck() != null && cust.getIgnoreCheck().booleanValue();
            try {
                if (!ignoreCheck) {
                    customer = customerService.findByCodeAndFetch(cust.getCode(), Arrays.asList("seller", "customFields"));
                }

            } catch (Exception e) {
                log.warn("failed to find custom by code and fetch ", e);
            }

            if (customer != null) {
                if(customer.getSeller() != null) {
                    if (!customer.getSeller().getCode().equals(sell.getCode())) {
                        createCustomerError(sell, cust, "The customer already exists but is attached to a different seller.");
                        nbCustomersError++;
                        log.error("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Error");
                        return;
                    }
                }

                nbCustomersUpdated++;
                customer = customerImportService.updateCustomer(customer, seller, sell, cust);
                log.info("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Updated");
            } else {
                customer = customerImportService.createCustomer(seller, sell, cust);
                nbCustomersCreated++;
                log.info("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Created");
            }

            for (org.meveo.model.jaxb.customer.CustomerAccount custAcc : cust.getCustomerAccounts().getCustomerAccount()) {
                j++;

                if (customerAccountCheckError(cust, sell, custAcc)) {
                    nbCustomerAccountsError++;
                    log.error("File:" + fileName + ", typeEntity:CustomerAccount, indexCustomer:" + i + ", index:" + j + " Code:" + custAcc.getCode() + ", status:Error");
                    continue;
                }

                if (customerAccountCheckWarning(cust, sell, custAcc)) {
                    nbCustomerAccountsWarning++;
                    log.info("File:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j + " Code:" + custAcc.getCode() + ", status:Warning");
                }

                createCustomerAccount(fileName, customer, seller, custAcc, cust, sell, i, j);
            }
        } catch (Exception e) {
            createCustomerError(sell, cust, ExceptionUtils.getRootCause(e).getMessage());
            nbCustomersError++;
            log.error("File:" + fileName + ", typeEntity:Customer, index:" + i + ", code:" + cust.getCode() + ", status:Error");
            log.error("failed to create customer", e);
        }
    }

    /**
     * @param fileName file's name
     * @param customer customer
     * @param seller seller
     * @param custAcc custom account
     * @param cust jaxb customer
     * @param sell jaxb seller
     * @param i index
     * @param j index
     * @throws BusinessException business exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void createCustomerAccount(String fileName, Customer customer, org.meveo.model.admin.Seller seller, org.meveo.model.jaxb.customer.CustomerAccount custAcc,
            org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell, int i, int j) throws BusinessException {
        nbCustomerAccounts++;
        CustomerAccount customerAccountTmp = null;
        boolean ignoreCheck = custAcc.getIgnoreCheck() != null && custAcc.getIgnoreCheck().booleanValue();
        try {
            if (!ignoreCheck) {
                customerAccountTmp = customerAccountService.findByCode(custAcc.getCode(), Arrays.asList("customer", "customFields"));
            }
        } catch (Exception e) {
            log.error("failed to create customer account", e);
        }

        if (customerAccountTmp != null) {
            if (!customerAccountTmp.getCustomer().getCode().equals(cust.getCode())) {
                nbCustomerAccountsError++;
                createCustomerAccountError(sell, cust, custAcc, "A customer account with same code exists for another customer");
                return;
            }

            customerImportService.updateCustomerAccount(customerAccountTmp, customer, seller, custAcc, cust, sell);
            nbCustomerAccountsUpdated++;
            log.info("File:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j + " code:" + custAcc.getCode() + ", status:Updated");

        } else {
            CustomerAccount customerAccount = customerImportService.createCustomerAccount(customer, seller, custAcc, cust, sell);

            BillingAccounts billingAccounts = custAcc.getBillingAccounts();

            if (billingAccounts != null) {
                List<org.meveo.model.jaxb.account.BillingAccount> billingAccountList = billingAccounts.getBillingAccount();

                for (org.meveo.model.jaxb.account.BillingAccount billingAccountJaxb : billingAccountList) {
                    BillingAccount billingAccount = null;
                    try {
                        billingAccount = accountImportService.importBillingAccount(billingAccountJaxb, customerAccount);
                    } catch (ImportWarningException e) {
                        log.error("Error when importing Billing Account", e);
                    }

                    for (org.meveo.model.jaxb.account.UserAccount uAccount : billingAccountJaxb.getUserAccounts().getUserAccount()) {
                        try {
                            accountImportService.importUserAccount(billingAccount, billingAccountJaxb, uAccount, seller);
                        } catch (ImportWarningException e) {
                            log.error("Error when importing User Account", e);
                        }
                    }
                }

            }
            nbCustomerAccountsCreated++;
            log.info("File:" + fileName + ", typeEntity:CustomerAccount,  indexCustomer:" + i + ", index:" + j + " code:" + custAcc.getCode() + ", status:Created");
        }
    }

    /**
     * @throws Exception exception
     */
    private void createHistory() throws Exception {

        customerImportHisto.setNbCustomerAccounts(nbCustomerAccounts);
        customerImportHisto.setNbCustomerAccountsCreated(nbCustomerAccountsCreated);
        customerImportHisto.setNbCustomerAccountsError(nbCustomerAccountsError);
        customerImportHisto.setNbCustomerAccountsIgnored(nbCustomerAccountsIgnored);
        customerImportHisto.setNbCustomerAccountsWarning(nbCustomerAccountsWarning);
        customerImportHisto.setNbCustomers(nbCustomers);
        customerImportHisto.setNbCustomersCreated(nbCustomersCreated);
        customerImportHisto.setNbCustomersError(nbCustomersError);
        customerImportHisto.setNbCustomersIgnored(nbCustomersIgnored);
        customerImportHisto.setNbCustomersWarning(nbCustomersWarning);
        customerImportHisto.setNbSellers(nbSellers);
        customerImportHisto.setNbSellersCreated(nbSellersCreated);
        customerImportHisto.setNbSellersError(nbSellersError);
        customerImportHisto.setNbSellersIgnored(nbSellersIgnored);
        customerImportHisto.setNbSellersWarning(nbSellersWarning);
        customerImportHistoService.create(customerImportHisto);
    }

    /**
     * @param fileName file's name
     * @throws Exception exception occurs when genering report
     */
    private void generateReport(String fileName) throws Exception {
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "customers" + File.separator;

        if (sellersWarning.getWarnings() != null) {
            String warningDir = importDir + "output" + File.separator + "warnings";
            File dir = new File(warningDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            JAXBUtils.marshaller(sellersWarning, new File(warningDir + File.separator + "WARN_" + fileName));
        }

        if (sellersError.getErrors() != null) {
            String errorDir = importDir + "output" + File.separator + "errors";

            File dir = new File(errorDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            JAXBUtils.marshaller(sellersError, new File(errorDir + File.separator + "ERR_" + fileName));
        }
    }

    /**
     * @param sell seller
     * @param cause cause of having error
     */
    private void createSellerError(org.meveo.model.jaxb.customer.Seller sell, String cause) {
        String generateFullCrmReject = paramBeanFactory.getInstance().getProperty("connectorCRM.generateFullCrmReject", "true");
        ErrorSeller errorSeller = new ErrorSeller();
        errorSeller.setCause(cause);
        errorSeller.setCode(sell.getCode());

        if (!sellersError.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            sellersError.getSeller().add(sell);
        }

        if (sellersError.getErrors() == null) {
            sellersError.setErrors(new Errors());
        }

        sellersError.getErrors().getErrorSeller().add(errorSeller);
    }

    /**
     * @param sell seller
     * @param cust customer
     * @param cause erorr
     */
    private void createCustomerError(org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust, String cause) {
        String generateFullCrmReject = paramBeanFactory.getInstance().getProperty("connectorCRM.generateFullCrmReject", "true");
        ErrorCustomer errorCustomer = new ErrorCustomer();
        errorCustomer.setCause(cause);
        errorCustomer.setCode(cust.getCode());

        if (!sellersError.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            sellersError.getSeller().add(sell);
        }

        if (sellersError.getErrors() == null) {
            sellersError.setErrors(new Errors());
        }

        sellersError.getErrors().getErrorCustomer().add(errorCustomer);
    }

    /**
     * @param sell seller
     * @param cause the reason of having error.
     */
    private void createSellerWarning(org.meveo.model.jaxb.customer.Seller sell, String cause) {
        String generateFullCrmReject = paramBeanFactory.getInstance().getProperty("connectorCRM.generateFullCrmReject", "true");
        WarningSeller warningSeller = new WarningSeller();
        warningSeller.setCause(cause);
        warningSeller.setCode(sell == null ? "" : sell.getCode());

        if (!sellersWarning.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject) && sell != null) {
            sellersWarning.getSeller().add(sell);
        }

        if (sellersWarning.getWarnings() == null) {
            sellersWarning.setWarnings(new Warnings());
        }

        sellersWarning.getWarnings().getWarningSeller().add(warningSeller);
    }

    /**
     * @param sell seller
     * @param cust customer
     * @param custAccount customer account
     * @param cause cause of error
     */
    private void createCustomerAccountError(org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust,
            org.meveo.model.jaxb.customer.CustomerAccount custAccount, String cause) {
        log.error("Seller={}, customer={}, customerAccount={}, cause={}", new Object[] { sell, cust, custAccount, cause });
        String generateFullCrmReject = paramBeanFactory.getInstance().getProperty("connectorCRM.generateFullCrmReject", "true");
        ErrorCustomerAccount errorCustomerAccount = new ErrorCustomerAccount();
        errorCustomerAccount.setCause(cause);
        errorCustomerAccount.setCode(custAccount.getCode());
        errorCustomerAccount.setCustomerCode(cust.getCode());

        if (sellersError.getErrors() == null) {
            sellersError.setErrors(new Errors());
        }

        if (!sellersError.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            sellersError.getSeller().add(sell);
        }

        sellersError.getErrors().getErrorCustomerAccount().add(errorCustomerAccount);
    }

    /**
     * @param sell seller
     * @param cust customer
     * @param custAccount customer account
     * @param cause the cause of error
     */
    private void createCustomerAccountWarning(org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust,
            org.meveo.model.jaxb.customer.CustomerAccount custAccount, String cause) {
        log.warn("Seller={}, customer={}, customerAccount={}, cause={}", new Object[] { sell, cust, custAccount, cause });
        String generateFullCrmReject = paramBeanFactory.getInstance().getProperty("connectorCRM.generateFullCrmReject", "true");
        WarningCustomerAccount warningCustomerAccount = new WarningCustomerAccount();
        warningCustomerAccount.setCause(cause);
        warningCustomerAccount.setCode(custAccount.getCode());
        warningCustomerAccount.setCustomerCode(cust.getCode());

        if (!sellersWarning.getSeller().contains(sell) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            sellersWarning.getSeller().add(sell);
        }

        if (sellersWarning.getWarnings() == null) {
            sellersWarning.setWarnings(new Warnings());
        }

        sellersWarning.getWarnings().getWarningCustomerAccount().add(warningCustomerAccount);
    }

    /**
     * @param sell seller
     * @return true/false
     */
    private boolean sellerCheckError(org.meveo.model.jaxb.customer.Seller sell) {
        if (StringUtils.isBlank(sell.getCode())) {
            createSellerError(sell, "Code is null.");
            return true;
        }

        if (sell.getCustomers() == null || sell.getCustomers().getCustomer() == null || sell.getCustomers().getCustomer().isEmpty()) {
            createSellerError(sell, "No customer.");
            return true;
        }

        return false;
    }

    /**
     * @param sell seller
     * @param cust customer
     * @return true/false
     */
    private boolean customerCheckError(org.meveo.model.jaxb.customer.Seller sell, org.meveo.model.jaxb.customer.Customer cust) {

        if (StringUtils.isBlank(cust.getCode())) {
            createCustomerError(sell, cust, "Code is null");
            return true;
        }
        if (StringUtils.isBlank(cust.getDesCustomer())) {
            createCustomerError(sell, cust, "Description is null");
            return true;
        }
        if (StringUtils.isBlank(cust.getCustomerCategory())) {
            createCustomerError(sell, cust, "CustomerCategory is null");
            return true;
        }
        if (StringUtils.isBlank(cust.getCustomerBrand())) {
            createCustomerError(sell, cust, "CustomerBrand is null");
            return true;
        }
        if (cust.getName() == null) {
            createCustomerError(sell, cust, "name.title and name.lastName is null");
        } else {
            if (StringUtils.isBlank(cust.getName().getTitle())) {
                createCustomerError(sell, cust, "name.title is null");
            }
            if (StringUtils.isBlank(cust.getName().getLastName())) {
                createCustomerError(sell, cust, "name.lastName is null");
            }

            if (titleService.findByCode(cust.getName().getTitle()) == null) {
                createCustomerError(sell, cust, "Title with code=" + cust.getName().getTitle() + " does not exists");
                return true;
            }
        }
        if (cust.getCustomerAccounts().getCustomerAccount() == null || cust.getCustomerAccounts().getCustomerAccount().isEmpty()) {
            createCustomerError(sell, cust, "No customer account");
            return true;
        }

        return false;
    }

    /**
     * @param cust customer
     * @param sell seller
     * @param custAcc customer account
     * @return true/false
     */
    private boolean customerAccountCheckError(org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell,
            org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
        if (StringUtils.isBlank(custAcc.getCode())) {
            createCustomerAccountError(sell, cust, custAcc, "Code is null");
            return true;
        }
        if (StringUtils.isBlank(custAcc.getDescription())) {
            createCustomerAccountError(sell, cust, custAcc, "Description is null");
            return true;
        }
        if (StringUtils.isBlank(custAcc.getTradingCurrencyCode())) {
            createCustomerAccountError(sell, cust, custAcc, "Currency is null");
            return true;
        }
        if (StringUtils.isBlank(custAcc.getTradingLanguageCode())) {
            createCustomerAccountError(sell, cust, custAcc, "Language is null");
            return true;
        }
        if (StringUtils.isBlank(custAcc.getCreditCategory())) {
            createCustomerAccountError(sell, cust, custAcc, "Credit Category is null");
            return true;
        }
        if (custAcc.getName() == null || StringUtils.isBlank(custAcc.getName().getLastName())) {
            createCustomerAccountError(sell, cust, custAcc, "Lastname is null");
            return true;
        }
        /*
         * if (StringUtils.isBlank(custAcc.getPaymentMethod()) || ("DIRECTDEBIT" + "CHECK" + "TIP" + "WIRETRANSFER").indexOf(custAcc.getPaymentMethod()) == -1) {
         * createCustomerAccountError(sell,cust, custAcc, "PaymentMethod is null,or not in {DIRECTDEBIT,CHECK,TIP,WIRETRANSFER}" ); return true; } if (custAcc.getAddress() == null
         * || StringUtils.isBlank(custAcc.getAddress().getZipCode())) {
         * 
         * createCustomerAccountError(sell,cust, custAcc, "ZipCode is null"); return true; } if (custAcc.getAddress() == null ||
         * StringUtils.isBlank(custAcc.getAddress().getCity())) { createCustomerAccountError(sell,cust, custAcc, "City is null"); return true; } if (custAcc.getAddress() == null ||
         * StringUtils.isBlank(custAcc.getAddress().getCountry())) { createCustomerAccountError(sell,cust, custAcc, "Country is null"); return true; } if
         * (StringUtils.isBlank(custAcc.getExternalRef1())) { createCustomerAccountError(sell,cust, custAcc, "ExternalRef1 is null"); return true; }
         */
        return false;
    }

    /**
     * @param cust customer
     * @param sell seller
     * @param custAcc customer account
     * @return true/false for warning
     */
    private boolean customerAccountCheckWarning(org.meveo.model.jaxb.customer.Customer cust, org.meveo.model.jaxb.customer.Seller sell,
            org.meveo.model.jaxb.customer.CustomerAccount custAcc) {
        boolean isWarning = false;

        if ("PRO".equals(cust.getCustomerCategory()) && StringUtils.isBlank(custAcc.getCompany())) {
            createCustomerAccountWarning(sell, cust, custAcc, "Company is null");
            isWarning = true;
        }

        if ((cust.getCustomerCategory().startsWith("PART_")) && (custAcc.getName() == null || StringUtils.isBlank(custAcc.getName().getFirstName()))) {
            createCustomerAccountWarning(sell, cust, custAcc, "Name is null");
            isWarning = true;
        }

        return isWarning;
    }

}
