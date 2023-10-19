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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.meveo.model.admin.AccountImportHisto;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.account.BillingAccount;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.ErrorBillingAccount;
import org.meveo.model.jaxb.account.ErrorUserAccount;
import org.meveo.model.jaxb.account.Errors;
import org.meveo.model.jaxb.account.WarningBillingAccount;
import org.meveo.model.jaxb.account.WarningUserAccount;
import org.meveo.model.jaxb.account.Warnings;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobSpeedEnum;
import org.meveo.service.admin.impl.AccountImportHistoService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.AccountImportService;
import org.meveo.service.crm.impl.ImportWarningException;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Stateless
public class ImportAccountsJobBean {

    @Inject
    private Logger log;

    @Inject
    private AccountImportHistoService accountImportHistoService;

    @Inject
    private AccountImportService accountImportService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private SellerService sellerService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;

    private BillingAccounts billingAccountsWarning;
    private BillingAccounts billingAccountsError;

    private int nbBillingAccounts;
    private int nbBillingAccountsError;
    private int nbBillingAccountsWarning;
    private int nbBillingAccountsIgnored;
    private int nbBillingAccountsCreated;
    private int nbBillingAccountsUpdated;

    private int nbUserAccounts;
    private int nbUserAccountsError;
    private int nbUserAccountsWarning;
    private int nbUserAccountsIgnored;
    private int nbUserAccountsCreated;
    private int nbUserAccountsUpdated;
    private AccountImportHisto accountImportHisto;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionResultImpl result) {
        ParamBean param = paramBeanFactory.getInstance();
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "accounts" + File.separator;
        String dirIN = importDir + "input";

        log.info("dirIN=" + dirIN);

        String dirOK = importDir + "output";
        String dirKO = importDir + "reject";
        String prefix = param.getProperty("connectorCRM.importAccounts.prefix", "ACCOUNT_");
        String ext = param.getProperty("connectorCRM.importAccounts.extension", "xml");

        File dir = new File(dirIN);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<File> files = getFilesToProcess(dir, prefix, ext);
        int numberOfFiles = files.size();

        log.info("InputFiles job " + numberOfFiles + " to import");

        for (File file : files) {
            if (!jobExecutionService.isShouldJobContinue(result.getJobInstance().getId())) {
                break;
            }
            File currentFile = null;
            try {
                log.info("InputFiles job " + file.getName() + " in progres");
                currentFile = FileUtils.addExtension(file, ".processing");

                importFile(currentFile, file.getName(), result.getJobInstance());

                FileUtils.moveFile(dirOK, currentFile, file.getName());
                log.info("InputFiles job " + file.getName() + " done");
            } catch (Exception e) {
                log.error("InputFiles job " + file.getName() + " failed", e);
                FileUtils.moveFile(dirKO, currentFile, file.getName());

            } finally {
                if (currentFile != null) {
                    currentFile.delete();
                }
            }
        }

        result.setNbItemsToProcess(nbBillingAccounts);
        result.setNbItemsCorrectlyProcessed(nbBillingAccountsCreated + nbBillingAccountsUpdated + nbBillingAccountsIgnored);
        result.setNbItemsProcessedWithError(nbBillingAccountsError);
        result.setNbItemsProcessedWithWarning(nbBillingAccountsWarning);
    }

    private List<File> getFilesToProcess(File dir, String prefix, String ext) {
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

    private void importFile(File file, String fileName, JobInstance jobInstance) throws JAXBException, Exception {

        log.info("start import file : {}", fileName);

        billingAccountsWarning = new BillingAccounts();
        billingAccountsError = new BillingAccounts();
        nbBillingAccounts = 0;
        nbBillingAccountsError = 0;
        nbBillingAccountsWarning = 0;
        nbBillingAccountsIgnored = 0;
        nbBillingAccountsCreated = 0;
        nbBillingAccountsUpdated = 0;

        nbUserAccounts = 0;
        nbUserAccountsError = 0;
        nbUserAccountsWarning = 0;
        nbUserAccountsIgnored = 0;
        nbUserAccountsUpdated = 0;
        nbUserAccountsCreated = 0;
        accountImportHisto = new AccountImportHisto();

        accountImportHisto.setExecutionDate(new Date());
        accountImportHisto.setFileName(fileName);

        if (file.length() < 83) {
            createBillingAccountWarning(null, "Fichier vide");
            generateReport(fileName);
            createHistory();
            return;
        }
        BillingAccounts billingAccounts = (BillingAccounts) JAXBUtils.unmarshaller(BillingAccounts.class, file);
        log.debug("parsing file ok");

        int i = -1;

        nbBillingAccounts = billingAccounts.getBillingAccount().size();
        if (nbBillingAccounts == 0) {
            createBillingAccountWarning(null, "Fichier vide");

        }

        org.meveo.model.admin.Seller seller = null;
        try {
            seller = sellerService.findByCode("JOB_SELLER0");
        } catch (Exception e) {
            log.warn("error while getting seller ", e);
        }

        for (org.meveo.model.jaxb.account.BillingAccount billAccount : billingAccounts.getBillingAccount()) {
            nbUserAccounts += billAccount.getUserAccounts().getUserAccount().size();
        }

        int checkJobStatusEveryNr = jobInstance.getJobSpeed().getCheckNb();
        for (org.meveo.model.jaxb.account.BillingAccount billingAccountDto : billingAccounts.getBillingAccount()) {
            if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
                break;
            }

            if (billingCheckError(billingAccountDto)) {
                nbBillingAccountsError++;
                log.error("File:" + fileName + ", typeEntity:BillingAccount, index:" + i + ", code:" + billingAccountDto.getCode() + ", status:Error");
                continue;
            }

            createBillingAccount(seller, billingAccountDto, fileName, i);
            i++;
        }

        generateReport(fileName);
        createHistory();

        log.info("end import file ");
    }

    private boolean billingCheckError(BillingAccount billingAccount) {
        if (StringUtils.isBlank(billingAccount.getCode())) {
            createBillingAccountError(billingAccount, "Code is null");
            return true;
        }
        if (StringUtils.isBlank(billingAccount.getDescription())) {
            createBillingAccountError(billingAccount, "Description is null");
            return true;
        }
        if (StringUtils.isBlank(billingAccount.getBillingCycle())) {
            createBillingAccountError(billingAccount, "BillingCycle is null");
            return true;
        }
        if (StringUtils.isBlank(billingAccount.getTradingCountryCode())) {
            createBillingAccountError(billingAccount, "Country is null");
            return true;
        }
        if (StringUtils.isBlank(billingAccount.getTradingLanguageCode())) {
            createBillingAccountError(billingAccount, "Language is null");
            return true;
        }

        return false;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void createBillingAccount(Seller seller, org.meveo.model.jaxb.account.BillingAccount billingAccountDto, String fileName, int i)
            throws BusinessException, ImportWarningException {
        int j = -1;
        org.meveo.model.billing.BillingAccount billingAccount = null;
        try {
            try {
                boolean ignoreCheck = billingAccountDto.getIgnoreCheck() != null && billingAccountDto.getIgnoreCheck().booleanValue();
                if (!ignoreCheck) {
                    billingAccount = billingAccountService.findByCode(billingAccountDto.getCode());
                }

                if (billingAccount == null) {
                    billingAccount = accountImportService.importBillingAccount(billingAccountDto, null);
                    log.info("file6:" + fileName + ", typeEntity:BillingAccount, index:" + i + ", code:" + billingAccountDto.getCode() + ", status:Created");
                    nbBillingAccountsCreated++;
                } else {
                    log.info("file1:" + fileName + ", typeEntity:BillingAccount, index:" + i + ", code:" + billingAccountDto.getCode() + ", status:Updated");
                    billingAccount = accountImportService.updateBillingAccount(billingAccountDto);
                    nbBillingAccountsUpdated++;
                }
            } catch (ImportWarningException w) {
                createBillingAccountWarning(billingAccountDto, w.getMessage());
                nbBillingAccountsWarning++;
                log.info("file5:" + fileName + ", typeEntity:BillingAccount,  index:" + i + " code:" + billingAccountDto.getCode() + ", status:Warning");
            } catch (BusinessException e) {
                createBillingAccountError(billingAccountDto, e.getMessage());
                nbBillingAccountsError++;
                log.error("file2:" + fileName + ", typeEntity:BillingAccount, index:" + i + ", code:" + billingAccountDto.getCode() + ", status:Error");
            }
        } catch (Exception e) {
            createBillingAccountError(billingAccountDto, ExceptionUtils.getRootCause(e).getMessage());
            nbBillingAccountsError++;
            log.error("file7:" + fileName + ", typeEntity:BillingAccount, index:" + i + ", code:" + billingAccountDto.getCode() + ", status:Error");
            log.error("failed to create billing account", e);
        }

        if (billingAccount == null) {
            return;
        }

        for (org.meveo.model.jaxb.account.UserAccount uAccount : billingAccountDto.getUserAccounts().getUserAccount()) {
            j++;

            if (userAccountCheckError(billingAccountDto, uAccount)) {
                nbUserAccountsError++;
                log.error("File:" + fileName + ", typeEntity:UserAccount, index:" + i + ", code:" + billingAccountDto.getCode() + ", status:Error");
                continue;
            }

            createUserAccount(uAccount, billingAccount, billingAccountDto, fileName, i, j);
        }
    }

    private boolean userAccountCheckError(BillingAccount billingAccount, org.meveo.model.jaxb.account.UserAccount userAccount) {
        if (StringUtils.isBlank(userAccount.getCode())) {
            createUserAccountError(billingAccount, userAccount, "Code is null");
            return true;
        }
        if (StringUtils.isBlank(userAccount.getDescription())) {
            createUserAccountError(billingAccount, userAccount, "Description is null");
            return true;
        }

        return false;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void createUserAccount(org.meveo.model.jaxb.account.UserAccount uAccount, org.meveo.model.billing.BillingAccount billingAccount,
            org.meveo.model.jaxb.account.BillingAccount billingAccountDto, String fileName, int i, int j) throws BusinessException, ImportWarningException {
        UserAccount userAccount = null;
        log.debug("userAccount found code:" + uAccount.getCode());
        boolean ignoreCheck = uAccount.getIgnoreCheck() != null && uAccount.getIgnoreCheck();
        try {
            if (!ignoreCheck) {
                userAccount = userAccountService.findByCode(uAccount.getCode());
            }

        } catch (Exception e) {
            log.error("error while getting user account", e);
        }

        if (userAccount != null) {
            nbUserAccountsUpdated++;
            log.info("file:" + fileName + ", typeEntity:UserAccount,  indexBillingAccount:" + i + ", index:" + j + " code:" + uAccount.getCode() + ", status:Updated");
            accountImportService.updateUserAccount(billingAccount, billingAccountDto, uAccount);
        } else {
            try {
                userAccount = accountImportService.importUserAccount(billingAccount, billingAccountDto, uAccount, null);

                log.info("file:" + fileName + ", typeEntity:UserAccount,  indexBillingAccount:" + i + ", index:" + j + " code:" + uAccount.getCode() + ", status:Created");
                nbUserAccountsCreated++;
            } catch (ImportWarningException w) {
                createUserAccountWarning(billingAccountDto, uAccount, w.getMessage());
                nbUserAccountsWarning++;
                log.info("file:" + fileName + ", typeEntity:UserAccount,  indexBillingAccount:" + i + ", index:" + j + " code:" + uAccount.getCode() + ", status:Warning");

            } catch (BusinessException e) {
                createUserAccountError(billingAccountDto, uAccount, e.getMessage());
                nbUserAccountsError++;
                log.error("file:" + fileName + ", typeEntity:UserAccount,  indexBillingAccount:" + i + ", index:" + j + " code:" + uAccount.getCode() + ", status:Error");
            }
        }
    }

    private void createBillingAccountError(org.meveo.model.jaxb.account.BillingAccount billAccount, String cause) {
        ParamBean param = paramBeanFactory.getInstance();
        String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
        ErrorBillingAccount errorBillingAccount = new ErrorBillingAccount();
        errorBillingAccount.setCause(cause);
        errorBillingAccount.setCode(billAccount.getCode());

        if (!billingAccountsError.getBillingAccount().contains(billAccount) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            billingAccountsError.getBillingAccount().add(billAccount);
        }

        if (billingAccountsError.getErrors() == null) {
            billingAccountsError.setErrors(new Errors());
        }

        billingAccountsError.getErrors().getErrorBillingAccount().add(errorBillingAccount);
    }

    private void createUserAccountError(org.meveo.model.jaxb.account.BillingAccount billAccount, org.meveo.model.jaxb.account.UserAccount uAccount, String cause) {
        ParamBean param = paramBeanFactory.getInstance();
        String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
        ErrorUserAccount errorUserAccount = new ErrorUserAccount();
        errorUserAccount.setCause(cause);
        errorUserAccount.setCode(uAccount.getCode());
        errorUserAccount.setBillingAccountCode(billAccount.getCode());

        if (billingAccountsError.getErrors() == null) {
            billingAccountsError.setErrors(new Errors());
        }

        if (!billingAccountsError.getBillingAccount().contains(billAccount) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            billingAccountsError.getBillingAccount().add(billAccount);
        }

        billingAccountsError.getErrors().getErrorUserAccount().add(errorUserAccount);
    }

    private void createBillingAccountWarning(org.meveo.model.jaxb.account.BillingAccount billAccount, String cause) {
        ParamBean param = paramBeanFactory.getInstance();
        String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
        WarningBillingAccount warningBillingAccount = new WarningBillingAccount();
        warningBillingAccount.setCause(cause);
        warningBillingAccount.setCode(billAccount == null ? "" : billAccount.getCode());

        if (!billingAccountsWarning.getBillingAccount().contains(billAccount) && "true".equalsIgnoreCase(generateFullCrmReject) && billAccount != null) {
            billingAccountsWarning.getBillingAccount().add(billAccount);
        }

        if (billingAccountsWarning.getWarnings() == null) {
            billingAccountsWarning.setWarnings(new Warnings());
        }

        billingAccountsWarning.getWarnings().getWarningBillingAccount().add(warningBillingAccount);
    }

    private void createUserAccountWarning(org.meveo.model.jaxb.account.BillingAccount billAccount, org.meveo.model.jaxb.account.UserAccount uAccount, String cause) {
        ParamBean param = paramBeanFactory.getInstance();
        String generateFullCrmReject = param.getProperty("connectorCRM.generateFullCrmReject", "true");
        WarningUserAccount warningUserAccount = new WarningUserAccount();
        warningUserAccount.setCause(cause);
        warningUserAccount.setCode(uAccount.getCode());
        warningUserAccount.setBillingAccountCode(billAccount.getCode());

        if (!billingAccountsWarning.getBillingAccount().contains(billAccount) && "true".equalsIgnoreCase(generateFullCrmReject)) {
            billingAccountsWarning.getBillingAccount().add(billAccount);
        }

        if (billingAccountsWarning.getWarnings() == null) {
            billingAccountsWarning.setWarnings(new Warnings());
        }

        billingAccountsWarning.getWarnings().getWarningUserAccount().add(warningUserAccount);
    }

    private void generateReport(String fileName) throws Exception {
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "accounts" + File.separator;

        if (billingAccountsWarning.getWarnings() != null) {
            String warningDir = importDir + "output" + File.separator + "warnings";
            File dir = new File(warningDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            JAXBUtils.marshaller(billingAccountsWarning, new File(warningDir + File.separator + "WARN_" + fileName));
        }

        if (billingAccountsError.getErrors() != null) {
            String errorDir = importDir + "output" + File.separator + "errors";

            File dir = new File(errorDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            JAXBUtils.marshaller(billingAccountsError, new File(errorDir + File.separator + "ERR_" + fileName));
        }
    }

    private void createHistory() throws Exception {
        accountImportHisto.setNbBillingAccounts(nbBillingAccounts);
        accountImportHisto.setNbBillingAccountsCreated(nbBillingAccountsCreated);
        accountImportHisto.setNbBillingAccountsError(nbBillingAccountsError);
        accountImportHisto.setNbBillingAccountsIgnored(nbBillingAccountsIgnored);
        accountImportHisto.setNbBillingAccountsWarning(nbBillingAccountsWarning);
        accountImportHisto.setNbUserAccounts(nbUserAccounts);
        accountImportHisto.setNbUserAccountsCreated(nbUserAccountsCreated);
        accountImportHisto.setNbUserAccountsError(nbUserAccountsError);
        accountImportHisto.setNbUserAccountsIgnored(nbUserAccountsIgnored);
        accountImportHisto.setNbUserAccountsWarning(nbUserAccountsWarning);
        accountImportHistoService.create(accountImportHisto);
    }
}
