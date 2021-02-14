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

package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.billing.EDRDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.jaxb.mediation.EDRs;
import org.meveo.model.jaxb.mediation.RatedTransactions;
import org.meveo.model.jaxb.mediation.WalletOperations;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The Class ExportMediationEntityJob bean to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ImportMediationEntityJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private EdrService edrService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private WalletService walletService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        long nbItems = 0;
        long nbItemsError = 0;
        File currentFile = null;
        ParamBean param = paramBeanFactory.getInstance();
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "edr" + File.separator;
        log.info("importDir=" + importDir);
        File dir = new File(importDir);
        String dirOK = importDir + "output";
        String dirKO = importDir + "reject";
        String fileName = null;
        try {

            if (dir.listFiles() == null) {
                result.setNbItemsToProcess(nbItems);
                result.setNbItemsCorrectlyProcessed(nbItems);
                return;
            }
            result.setNbItemsToProcess(dir.listFiles().length);
            jobExecutionService.initCounterElementsRemaining(result, dir.listFiles().length);

            for (File file : dir.listFiles()) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                fileName = file.getName();
                log.info("InputFiles job " + file.getName() + " in progres");
                currentFile = FileUtils.addExtension(file, ".processing");
                importFile(currentFile);
                FileUtils.moveFile(dirOK, currentFile, file.getName());
                log.info("InputFiles job " + file.getName() + " done");

                nbItems++;
                jobExecutionService.decCounterElementsRemaining(result);
            }

            result.setNbItemsCorrectlyProcessed(nbItems);
        } catch (Exception e) {
            nbItemsError++;
            log.error("Failed to run import EDR/WO/RT job", e);
            jobExecutionService.registerError(result, e.getMessage());
            result.addReport(e.getMessage());
            FileUtils.moveFile(dirKO, currentFile, fileName);
        } finally {
            if (currentFile != null) {
                currentFile.delete();
            }
        }
    }

    /**
     * @param file file to import.
     * @throws JAXBException
     * @throws BusinessException
     */
    private void importFile(File file) throws JAXBException, BusinessException {
        try {
            EDRs edrs = (EDRs) JAXBUtils.unmarshaller(EDRs.class, file);
            importEdrs(edrs);
        } catch (JAXBException e) {
            try {
                WalletOperations walletOperations = (WalletOperations) JAXBUtils.unmarshaller(WalletOperations.class, file);
                importWalletOperation(walletOperations);
            } catch (JAXBException e1) {
                RatedTransactions ratedTransactions = (RatedTransactions) JAXBUtils.unmarshaller(RatedTransactions.class, file);
                importRatedTransaction(ratedTransactions);
            }
        }
    }

    /**
     * @param edrs EDRs to import
     * @throws BusinessException
     */
    private void importEdrs(EDRs edrs) throws BusinessException {
        if (edrs == null || edrs.getEdrs() == null) {
            log.error("Empty EDR File!");
            return;
        }
        edrService.importEdrs(edrs.getEdrs());
    }

    /**
     * @param walletOperations Wallet operations to import
     * @throws BusinessException
     */
    private void importWalletOperation(WalletOperations walletOperations) throws BusinessException {
        if (walletOperations == null || walletOperations.getWalletOperations() == null) {
            log.error("Empty WO File!");
            return;
        }
        walletOperationService.importWalletOperation(walletOperations.getWalletOperations());
    }

    /**
     * @param ratedTransactions rated Transaction to import
     * @throws BusinessException
     */
    private void importRatedTransaction(RatedTransactions ratedTransactions) throws BusinessException {
        if (ratedTransactions == null || ratedTransactions.getRatedTransactions() == null) {
            log.error("Empty RTx File!");
            return;
        }
        ratedTransactionService.importRatedTransaction(ratedTransactions.getRatedTransactions());
    }

}