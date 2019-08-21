package org.meveo.admin.job;

import java.io.File;
import java.text.SimpleDateFormat;
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
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
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

/**
 * The Class ExportMediationEntityJob bean to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportMediationEntityJobBean extends BaseJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

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

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
            ParamBean param = paramBeanFactory.getInstance();
            String exportDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "edr" + File.separator;
            log.info("exportDir=" + exportDir);
            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_firstTransactionDate");
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_lastTransactionDate");
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }
            Boolean edrCf = (Boolean) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_edrCf");
            Boolean woCf = (Boolean) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_woCf");
            Boolean rtCf = (Boolean) this.getParamOrCFValue(jobInstance, "ExportMediationEntityJob_rtCf");
            long nbItems = 0;
            if (edrCf) {
                exportEDR(result, param, dir, firstTransactionDate, lastTransactionDate);
                nbItems = result.getNbItemsToProcess();
            }
            if (woCf) {
                exportWalletOperation(result, param, dir, firstTransactionDate, lastTransactionDate);
                nbItems += result.getNbItemsToProcess();
            }
            if (rtCf) {
                exportRatedTransaction(result, param, dir, firstTransactionDate, lastTransactionDate);
                nbItems += result.getNbItemsToProcess();
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
     *
     * @param result
     * @param param
     * @param dir
     * @param firstTransactionDate
     * @param lastTransactionDate
     * @return
     * @throws JAXBException
     */
    private JobExecutionResultImpl exportEDR(JobExecutionResultImpl result, ParamBean param, File dir, Date firstTransactionDate, Date lastTransactionDate) throws JAXBException {
        String timestamp = sdf.format(new Date());
        List<EDR> edrList = edrService.getOpenEdrsBetweenTwoDates(firstTransactionDate, lastTransactionDate);
        EDRs edrs = edrsToDto(edrList, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance().getId());
        int nbItems = edrs.getEdrs() != null ? edrs.getEdrs().size() : 0;
        result.setNbItemsToProcess(nbItems);
        JAXBUtils.marshaller(edrs, new File(dir + File.separator + "EDR_" + timestamp + ".xml"));
        result.setNbItemsCorrectlyProcessed(nbItems);
        return result;
    }

    /**
     *
     * @param result
     * @param param
     * @param dir
     * @param firstTransactionDate
     * @param lastTransactionDate
     * @return
     * @throws JAXBException
     */
    private JobExecutionResultImpl exportWalletOperation(JobExecutionResultImpl result, ParamBean param, File dir, Date firstTransactionDate, Date lastTransactionDate)
            throws JAXBException {
        String timestamp = sdf.format(new Date());
        List<WalletOperation> walletOperations = walletOperationService.getOpenWalletOperationBetweenTwoDates(firstTransactionDate, lastTransactionDate);
        WalletOperations walletOperationDtos = walletOperationsToDto(walletOperations, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance().getId());
        int nbItems = walletOperationDtos.getWalletOperations() != null ? walletOperationDtos.getWalletOperations().size() : 0;
        result.setNbItemsToProcess(nbItems);
        JAXBUtils.marshaller(walletOperationDtos, new File(dir + File.separator + "WO_" + timestamp + ".xml"));
        result.setNbItemsCorrectlyProcessed(nbItems);
        return result;

    }

    /**
     *
     * @param result
     * @param param
     * @param dir
     * @param firstTransactionDate
     * @param lastTransactionDate
     * @return
     * @throws JAXBException
     */
    private JobExecutionResultImpl exportRatedTransaction(JobExecutionResultImpl result, ParamBean param, File dir, Date firstTransactionDate, Date lastTransactionDate)
            throws JAXBException {
        String timestamp = sdf.format(new Date());
        List<RatedTransaction> ratedTransactions = ratedTransactionService.getOpenRatedTransactionBetweenTwoDates(firstTransactionDate, lastTransactionDate);
        RatedTransactions ratedTransactionDtos = ratedTransactionToDto(ratedTransactions, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"),
            result.getJobInstance().getId());
        int nbItems = ratedTransactionDtos.getRatedTransactions() != null ? ratedTransactionDtos.getRatedTransactions().size() : 0;
        result.setNbItemsToProcess(nbItems);
        JAXBUtils.marshaller(ratedTransactionDtos, new File(dir + File.separator + "RTx_" + timestamp + ".xml"));
        result.setNbItemsCorrectlyProcessed(nbItems);
        return result;
    }

    /**
     *
     * @param edrs
     * @param dateFormat
     * @param jobInstanceId
     * @return
     */
    private EDRs edrsToDto(List<EDR> edrs, String dateFormat, Long jobInstanceId) {
        EDRs dto = new EDRs();
        if (edrs != null) {
            int i = 0;
            for (EDR edr : edrs) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                    break;
                }
                dto.getEdrs().add(edrToDto(edr, dateFormat));
            }
        }
        return dto;
    }

    /**
     *
     * @param walletOperations
     * @param dateFormat
     * @param jobInstanceId
     * @return
     */
    private WalletOperations walletOperationsToDto(List<WalletOperation> walletOperations, String dateFormat, Long jobInstanceId) {
        WalletOperations dto = new WalletOperations();
        if (walletOperations != null) {
            int i = 0;
            for (WalletOperation wo : walletOperations) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                    break;
                }
                if (wo != null) {
                    WalletInstance wallet = walletService.retrieveIfNotManaged(wo.getWallet());
                    wo.setWallet(wallet);
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
     * @param dateFormat
     * @param jobInstanceId
     * @return
     */
    private RatedTransactions ratedTransactionToDto(List<RatedTransaction> ratedTransactions, String dateFormat, Long jobInstanceId) {
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
     * @param dateFormat
     * @return
     */
    private EDRDto edrToDto(EDR edr, String dateFormat) {
        if (edr == null) {
            return new EDRDto();
        }
        EDRDto dto = new EDRDto(edr);
        return dto;
    }

}