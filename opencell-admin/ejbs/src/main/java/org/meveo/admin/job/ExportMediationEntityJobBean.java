package org.meveo.admin.job;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * The Class ExportMediationEntityJob bean to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportMediationEntityJobBean extends BaseJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

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


    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
            String exportDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "edr" + File.separator + LocalDate.now().toString() + File.separator;
            log.info("exportDir : {}", exportDir);
            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_firstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_lastTransactionDate");
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }
            boolean edrCf = (boolean) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_edrCf", false);
            boolean woCf = (boolean) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_woCf", false);
            boolean rtCf = (boolean) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_rtCf", false);

            int maxResult = ((Long) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_maxResult", 100000)).intValue();
            long nbItems = 0;
            if (edrCf) {
                log.info("==> Start exporting EDR ");
                nbItems = exportEDR(jobInstance.getId(), dir, firstTransactionDate, lastTransactionDate, maxResult);
                log.info("{} EDRs exported in total", nbItems);
            }
            if (woCf) {
                log.info("==> Start exporting wallet operation ");
                long woCount = exportWalletOperation(jobInstance.getId(), dir, firstTransactionDate, lastTransactionDate, maxResult);
                log.info("{} WOs exported in total", woCount);
                nbItems += woCount;
            }
            if (rtCf) {
                log.info("==> Start exporting rated transaction ");
                long rtCount = exportRatedTransaction(jobInstance.getId(), dir, firstTransactionDate, lastTransactionDate, maxResult);
                log.info("{} RTs exported in total", rtCount);
                nbItems += rtCount;
            }
            result.setNbItemsToProcess(nbItems);
            result.setNbItemsCorrectlyProcessed(nbItems);
        } catch (Exception e) {
            log.error("Failed to run export EDR/WO/RT job", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }

    /**
     * @param jobInstanceId        a job instance id
     * @param dir                  a directory
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @param maxResult            a number of rows to fetch
     * @return number of items
     * @throws JAXBException
     */
    private int exportEDR(Long jobInstanceId, File dir, Date firstTransactionDate, Date lastTransactionDate, int maxResult) throws JAXBException {
        int nbItems = 0;
        long lastId = 0L;
        boolean moreToProcess = true;
        do {
            String timestamp = sdf.format(new Date());
            List<EDR> edrList = edrService.getNotOpenedEdrsBetweenTwoDates(firstTransactionDate, lastTransactionDate, lastId, maxResult);
            if (!edrList.isEmpty()) {
                int size = edrList.size();
                lastId = edrList.get(size - 1).getId();
                EDRs edrs = edrsToDto(edrList, jobInstanceId);
                JAXBUtils.marshaller(edrs, new File(dir, "EDR_" + timestamp + ".xml"));
                log.info("{} edr processed", size);
                nbItems += size;
            } else
                moreToProcess = false;
        } while (moreToProcess);
        return nbItems;
    }

    /**
     * @param jobInstanceId        a job instance id
     * @param dir                  a directory
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @param maxResult            a number of rows to fetch
     * @return
     * @throws JAXBException
     */
    private long exportWalletOperation(long jobInstanceId, File dir, Date firstTransactionDate, Date lastTransactionDate, int maxResult) throws JAXBException {
        int nbItems = 0;
        long lastId = 0;
        boolean moreToProcess = true;
        do {
            String timestamp = sdf.format(new Date());
            List<WalletOperation> walletOperation = walletOperationService.getNotOpenedWalletOperationBetweenTwoDates(firstTransactionDate, lastTransactionDate, lastId, maxResult);

            int size = walletOperation.size();
            if (size > 0) {
                WalletOperations walletOperations = walletOperationsToDto(walletOperation, jobInstanceId);
                JAXBUtils.marshaller(walletOperations, new File(dir + File.separator + "WO_" + timestamp + ".xml"));
                lastId = walletOperation.get(size - 1).getId();
                nbItems += size;
                log.info("{} WOs processed", size);
            } else {
                moreToProcess = false;
            }
        } while (moreToProcess);
        return nbItems;
    }

    /**
     * @param jobInstanceId        a job instance id
     * @param dir                  a directory
     * @param firstTransactionDate a first transaction date
     * @param lastTransactionDate  a last transaction date
     * @return a number of processed items
     * @throws JAXBException an exception
     */
    private long exportRatedTransaction(long jobInstanceId, File dir, Date firstTransactionDate, Date lastTransactionDate, int maxResult) throws JAXBException {
        int nbItems = 0;
        Long lastId = 0L;
        boolean moreToProcess = true;
        do {
            String timestamp = sdf.format(new Date());
            List<RatedTransaction> ratedTransactions = ratedTransactionService.getNotOpenedRatedTransactionBetweenTwoDates(firstTransactionDate, lastTransactionDate, lastId, maxResult);
            if (!ratedTransactions.isEmpty()) {
                RatedTransactions ratedTransactionDtos = ratedTransactionToDto(ratedTransactions, jobInstanceId);
                nbItems = ratedTransactionDtos.getRatedTransactions() != null ? ratedTransactionDtos.getRatedTransactions().size() : 0;
                JAXBUtils.marshaller(ratedTransactionDtos, new File(dir + File.separator + "RTx_" + timestamp + ".xml"));
                int size = ratedTransactions.size();
                lastId = ratedTransactions.get(size - 1).getId();
                log.info("{} RT processed", size);
            } else
                moreToProcess = false;
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
     * @param walletOperations
     * @param jobInstanceId
     * @return
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
     * @param edr
     * @return
     */
    private EDRDto edrToDto(EDR edr) {
        if (edr == null) {
            return new EDRDto();
        }
        return new EDRDto(edr);
    }

}