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

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.billing.EDRDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.jaxb.mediation.EDRs;
import org.meveo.model.jaxb.mediation.RatedTransactions;
import org.meveo.model.jaxb.mediation.WalletOperations;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF;

/**
 * The Class ExportMediationEntityJob bean to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportMediationEntityJobBean extends BaseJobBean {

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

    @EJB
    ExportMediationEntityJobBean exportMediationEntityJobBeanNewTx;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
            String exportParentDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator;
            File dir = new File(exportParentDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String exportFileName = (String) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME);
            exportFileName = exportFileName == null ? "" : exportFileName;

            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_firstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_lastTransactionDate");
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }

            int maxResult = ((Long) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_maxResult", 100000L)).intValue();
            long nbItems = 0;
            String report = "";
            List<EDRStatusEnum> edrStatus = getTargetStatusList(jobInstance, EDRStatusEnum.class, EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF);
            if (!edrStatus.isEmpty()) {
                log.info("==> Start exporting EDR ");
                nbItems = exportEDR(jobInstance.getId(), firstTransactionDate, lastTransactionDate, maxResult, edrStatus, exportParentDir, exportFileName);
                log.info("{} EDRs exported in total", nbItems);
                report += "EDRs : " + nbItems;
            }
            List<WalletOperationStatusEnum> woStatus = getTargetStatusList(jobInstance, WalletOperationStatusEnum.class, EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF);
            if (!woStatus.isEmpty()) {
                log.info("==> Start exporting wallet operation ");
                long woCount = exportWalletOperation(jobInstance.getId(), firstTransactionDate, lastTransactionDate, maxResult, woStatus, exportParentDir, exportFileName);
                log.info("{} WOs exported in total", woCount);
                nbItems += woCount;
                report += " WOs : " + woCount;
            }
            List<RatedTransactionStatusEnum> rtStatusList = getTargetStatusList(jobInstance, RatedTransactionStatusEnum.class, EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF);
            if (!rtStatusList.isEmpty()) {
                log.info("==> Start exporting rated transaction ");
                long rtCount = exportRatedTransaction(jobInstance.getId(), firstTransactionDate, lastTransactionDate, maxResult, rtStatusList, exportParentDir, exportFileName);
                log.info("{} RTs exported in total", rtCount);
                nbItems += rtCount;
                report += " RTs : " + rtCount;
            }
            result.addReport(report);
            result.setNbItemsToProcess(nbItems);
            result.setNbItemsCorrectlyProcessed(nbItems);
        } catch (Exception e) {
            log.error("Failed to run export EDR/WO/RT job", e);
            jobExecutionService.registerError(result, e.getMessage());
            result.addReport(e.getMessage());
        }
    }

    /**
     * @param jobInstanceId        a job instance id
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @param maxResult            a number of rows to fetch
     * @param formattedStatus
     * @param exportFileName
     * @param exportParentDir
     * @return number of items
     * @throws JAXBException
     */
    private int exportEDR(Long jobInstanceId, Date firstTransactionDate, Date lastTransactionDate, int maxResult, List<EDRStatusEnum> formattedStatus, String exportParentDir,
            String exportFileName) throws JAXBException {
        int nbItems = 0;
        long lastId = 0L;
        boolean moreToProcess = true;
        do {
            String timestamp = sdf.format(new Date());
            List<EDR> edrList = edrService.getEdrsBetweenTwoDatesByStatus(firstTransactionDate, lastTransactionDate, lastId, maxResult, formattedStatus);
            if (!edrList.isEmpty()) {
                int size = edrList.size();
                lastId = edrList.get(size - 1).getId();
                EDRs edrs = edrsToDto(edrList, jobInstanceId);

                String exportDir = exportParentDir + "edr" + File.separator;
                File dir = new File(exportDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (exportFileName.isEmpty()) {
                    exportFileName = "EDR_";
                }

                JAXBUtils.marshaller(edrs, new File(dir + File.separator + exportFileName + "_" + timestamp + ".xml"));
                log.info("{} edr processed", size);
                nbItems += size;
            } else
                moreToProcess = false;
        } while (moreToProcess);
        return nbItems;
    }

    /**
     * @param jobInstanceId        a job instance id
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @param maxResult            a number of rows to fetch
     * @param exportFileName
     * @param exportParentDir
     * @param formattedStatus
     * @return
     * @throws JAXBException
     */
    private long exportWalletOperation(long jobInstanceId, Date firstTransactionDate, Date lastTransactionDate, int maxResult, List<WalletOperationStatusEnum> formattedStatus,
            String exportParentDir, String exportFileName) throws JAXBException {
        int nbItems = 0;
        long lastId = 0L;
        boolean moreToProcess;
        do {
            PaginationResult paginationResult = exportMediationEntityJobBeanNewTx
                    .exportWalletOperationPerPage(jobInstanceId, firstTransactionDate, lastTransactionDate, maxResult, lastId, formattedStatus, exportParentDir, exportFileName);
            lastId = paginationResult.getLastId();
            nbItems += paginationResult.getNbItems();
            moreToProcess = paginationResult.getMoreToProcess();
        } while (moreToProcess);
        return nbItems;
    }

    /**
     * @param jobInstanceId        a job instance id
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @param exportFileName
     * @param exportParentDir
     * @param formattedStatus
     * @return a number of processed items
     * @throws JAXBException an exception
     */
    private long exportRatedTransaction(long jobInstanceId, Date firstTransactionDate, Date lastTransactionDate, int maxResult, List<RatedTransactionStatusEnum> formattedStatus,
            String exportParentDir, String exportFileName) throws JAXBException {
        int nbItems = 0;
        long lastId = 0L;
        boolean moreToProcess;
        do {
            PaginationResult paginationResult = exportMediationEntityJobBeanNewTx
                    .exportRatedTransactionPerPage(jobInstanceId, firstTransactionDate, lastTransactionDate, maxResult, lastId, formattedStatus, exportParentDir, exportFileName);
            lastId = paginationResult.getLastId();
            nbItems += paginationResult.getNbItems();
            moreToProcess = paginationResult.getMoreToProcess();
        } while (moreToProcess);
        return nbItems;
    }

    /**
     * @param edrs
     * @param jobInstanceId
     * @return
     */
    private EDRs edrsToDto(List<EDR> edrs, Long jobInstanceId) {
        EDRs dto = new EDRs();
        if (edrs != null) {
            int i = 0;
            for (EDR edr : edrs) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                    break;
                }
                dto.getEdrs().add(edrToDto(edr));
            }
        }
        return dto;
    }

    /**
     * @param walletOperations a wallet operation
     * @param jobInstanceId    a a job instance id
     * @return a list of wallet operations
     */

    private WalletOperations walletOperationsToDto(List<WalletOperation> walletOperations, Long jobInstanceId) {
        WalletOperations dto = new WalletOperations();
        if (walletOperations != null) {
            int i = 0;
            for (WalletOperation wo : walletOperations) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                    break;
                }
                if (wo != null) {
                    WalletOperationDto walletOperationDto = new WalletOperationDto(wo);
                    dto.getWalletOperations().add(walletOperationDto);
                }
            }
        }
        return dto;
    }

    /**
     * Convert a list of rated transaction to RatedTransactionDto.
     *
     * @param ratedTransactions
     * @param jobInstanceId
     * @return
     */
    private RatedTransactions ratedTransactionToDto(List<RatedTransaction> ratedTransactions, Long jobInstanceId) {
        RatedTransactions dto = new RatedTransactions();
        if (ratedTransactions != null) {
            int i = 0;
            for (RatedTransaction rt : ratedTransactions) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                    break;
                }
                if (rt != null) {
                    RatedTransactionDto ratedTransactionDto = new RatedTransactionDto(rt);
                    dto.getRatedTransactions().add(ratedTransactionDto);
                }
            }
        }
        return dto;
    }

    /**
     * @param edr an event data record
     * @return a EDR dto
     */
    private EDRDto edrToDto(EDR edr) {
        if (edr == null) {
            return new EDRDto();
        }
        return new EDRDto(edr);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PaginationResult exportWalletOperationPerPage(long jobInstanceId, Date firstDate, Date lastDate, int maxResult, Long lastId,
            List<WalletOperationStatusEnum> formattedStatus, String exportParentDir, String exportFileName) throws JAXBException {
        PaginationResult result = new PaginationResult();
        String timestamp = sdf.format(new Date());
        List<WalletOperation> walletOperation = walletOperationService.getWalletOperationBetweenTwoDatesByStatus(firstDate, lastDate, lastId, maxResult, formattedStatus);
        int size = walletOperation.size();

        if (size > 0) {
            WalletOperations walletOperations = walletOperationsToDto(walletOperation, jobInstanceId);
            String exportDir = exportParentDir + "wo" + File.separator;
            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (exportFileName.isEmpty()) {
                exportFileName = "WO_";
            }
            JAXBUtils.marshaller(walletOperations, new File(dir + File.separator + exportFileName + "_" + timestamp + ".xml"));
            result.setLastId(walletOperation.get(size - 1).getId());
            result.setNbItems(size);
            result.setMoreToProcess(true);
            log.info("{} WOs processed", size);
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PaginationResult exportRatedTransactionPerPage(long jobInstanceId, Date firstDate, Date lastDate, int maxResult, long lastId,
            List<RatedTransactionStatusEnum> formattedStatus, String exportParentDir, String exportFileName) throws JAXBException {
        String timestamp = sdf.format(new Date());
        List<RatedTransaction> ratedTransactions = ratedTransactionService.getRatedTransactionBetweenTwoDatesByStatus(firstDate, lastDate, lastId, maxResult, formattedStatus);
        PaginationResult result = new PaginationResult();
        if (!ratedTransactions.isEmpty()) {
            RatedTransactions ratedTransactionDtos = ratedTransactionToDto(ratedTransactions, jobInstanceId);
            String exportDir = exportParentDir + "rt" + File.separator;
            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (exportFileName.isEmpty()) {
                exportFileName = "RT_";
            }
            JAXBUtils.marshaller(ratedTransactionDtos, new File(dir + File.separator + exportFileName + "_" + timestamp + ".xml"));
            int size = ratedTransactions.size();
            result.setLastId(ratedTransactions.get(size - 1).getId());
            result.setNbItems(size);
            result.setMoreToProcess(true);
            log.info("{} RT processed", size);
        }
        return result;
    }

    /**
     * Inner class to handle pagination
     */
    private static class PaginationResult {
        private long lastId;
        private int nbItems;
        private boolean moreToProcess;

        public PaginationResult() {
            this.moreToProcess = false;
            this.lastId = 0L;
            this.nbItems = 0;
        }

        public void setLastId(long lastId) {
            this.lastId = lastId;
        }

        public long getLastId() {
            return lastId;
        }

        public void setNbItems(int nbItems) {
            this.nbItems = nbItems;
        }

        public int getNbItems() {
            return nbItems;
        }

        public void setMoreToProcess(boolean moreToProcess) {
            this.moreToProcess = moreToProcess;
        }

        public boolean getMoreToProcess() {
            return moreToProcess;
        }
    }
}