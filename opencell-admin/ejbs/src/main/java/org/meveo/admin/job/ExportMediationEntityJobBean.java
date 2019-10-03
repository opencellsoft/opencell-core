package org.meveo.admin.job;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.billing.EDRDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletInstance;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_FIRST_TRANSACTION_DATE;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_LAST_TRANSACTION_DATE;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_EDR_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_RT_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_WO_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF;
import static org.meveo.admin.job.ExportMediationEntityJob.EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME;

/**
 * The Class ExportMediationEntityJob bean to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportMediationEntityJobBean extends BaseJobBean {
	
    private static final long OLD_DATE = 10;
    
    private static final String SPLIT_CHAR = ";";

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
    
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
    	
        log.debug("Running with parameter={}", jobInstance.getParametres());
        try {
        	
        	ParamBean param = paramBeanFactory.getInstance();
            String exportParentDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator;
            File dir = new File(exportParentDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String exportFileName = (String) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME);
            exportFileName = exportFileName == null ? "" : exportFileName;
            
            Date firstTransactionDate = (Date) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_FIRST_TRANSACTION_DATE);
            Date lastTransactionDate = (Date) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_LAST_TRANSACTION_DATE);
            if (lastTransactionDate == null) {
                lastTransactionDate = new Date();
            }
            
            Boolean edrCf = (Boolean) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_EDR_CF);
            Boolean woCf = (Boolean) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_WO_CF);
            Boolean rtCf = (Boolean) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_ENTITY_JOB_RT_CF);
            
            long daysToExport = (long) this.getParamOrCFValue(jobInstance, EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE);
            if (daysToExport > 0) {
                firstTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusYears(OLD_DATE));
                lastTransactionDate = java.sql.Date.valueOf(LocalDate.now().minusDays(daysToExport));
            }
            
            long nbItems = 0;
            
            if (edrCf) {
            	List<EDRStatusEnum> formattedStatus = getTargetStatusList(jobInstance, EDRStatusEnum.class, EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF);
                if(!formattedStatus.isEmpty()) {
                	 exportEDR(result, param, firstTransactionDate, lastTransactionDate, formattedStatus, exportParentDir, exportFileName);
                     nbItems += result.getNbItemsToProcess();
                }
               
            }
            
            if (woCf) {
            	List<WalletOperationStatusEnum> formattedStatus = getTargetStatusList(jobInstance, WalletOperationStatusEnum.class, EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF);
                if(!formattedStatus.isEmpty()) {
                	exportWalletOperation(result, param, firstTransactionDate, lastTransactionDate, formattedStatus, exportParentDir, exportFileName);
                    nbItems += result.getNbItemsToProcess();
                }
            }
            
            if (rtCf) {
            	List<RatedTransactionStatusEnum> formattedStatus = getTargetStatusList(jobInstance, RatedTransactionStatusEnum.class, EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF);
                if(!formattedStatus.isEmpty()) {
                	exportRatedTransaction(result, param, firstTransactionDate, lastTransactionDate, formattedStatus, exportParentDir, exportFileName);
                    nbItems += result.getNbItemsToProcess();
                }
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
     * @param formattedStatus 
     * @param exportFileName 
     * @param exportParentDir 
     * @return
     * @throws JAXBException
     */
    private JobExecutionResultImpl exportEDR(JobExecutionResultImpl result, ParamBean param, Date firstTransactionDate, Date lastTransactionDate, List<EDRStatusEnum> formattedStatus, String exportParentDir, String exportFileName) throws JAXBException {
        
        List<EDR> edrList = edrService.getEdrsBetweenTwoDatesByStatus(firstTransactionDate, lastTransactionDate, formattedStatus);
        EDRs edrs = edrsToDto(edrList, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance().getId());
        int nbItems = edrs.getEdrs() != null ? edrs.getEdrs().size() : 0;
        result.setNbItemsToProcess(nbItems);
        
        String exportDir = exportParentDir + "edr" + File.separator;
    	File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    	
        if(exportFileName.isEmpty()) {
        	exportFileName = "EDR_";
        }
        
        String timestamp = sdf.format(new Date());
        
        JAXBUtils.marshaller(edrs, new File(dir + File.separator + exportFileName + "_" + timestamp + ".xml"));
        
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
     * @param exportFileName 
     * @param exportParentDir 
     * @param formattedStatus 
     * @return
     * @throws JAXBException
     */
    private JobExecutionResultImpl exportWalletOperation(JobExecutionResultImpl result, ParamBean param, Date firstTransactionDate, Date lastTransactionDate, List<WalletOperationStatusEnum> formattedStatus, String exportParentDir, String exportFileName) throws JAXBException {
        
    	List<WalletOperation> walletOperations = walletOperationService.getWalletOperationBetweenTwoDatesByStatus(firstTransactionDate, lastTransactionDate, formattedStatus);
        WalletOperations walletOperationDtos = walletOperationsToDto(walletOperations, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance().getId());
        int nbItems = walletOperationDtos.getWalletOperations() != null ? walletOperationDtos.getWalletOperations().size() : 0;
        result.setNbItemsToProcess(nbItems);
        
        String exportDir = exportParentDir + "wo" + File.separator;
    	File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    	
        if(exportFileName.isEmpty()) {
        	exportFileName = "WO_";
        }
        
        String timestamp = sdf.format(new Date());
        
        JAXBUtils.marshaller(walletOperationDtos, new File(dir + File.separator + exportFileName + "_" + timestamp + ".xml"));
        
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
     * @param exportFileName 
     * @param exportParentDir 
     * @param formattedStatus 
     * @return
     * @throws JAXBException
     */
    private JobExecutionResultImpl exportRatedTransaction(JobExecutionResultImpl result, ParamBean param, Date firstTransactionDate, Date lastTransactionDate, List<RatedTransactionStatusEnum> formattedStatus, String exportParentDir, String exportFileName) throws JAXBException {

    	List<RatedTransaction> ratedTransactions = ratedTransactionService.getRatedTransactionBetweenTwoDatesByStatus(firstTransactionDate, lastTransactionDate, formattedStatus);
		RatedTransactions ratedTransactionDtos = ratedTransactionToDto(ratedTransactions, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance().getId());
        int nbItems = ratedTransactionDtos.getRatedTransactions() != null ? ratedTransactionDtos.getRatedTransactions().size() : 0;
        result.setNbItemsToProcess(nbItems);
        
        String exportDir = exportParentDir + "rt" + File.separator;
    	File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    	
        if(exportFileName.isEmpty()) {
        	exportFileName = "RT_";
        }
        
        String timestamp = sdf.format(new Date());
        
        JAXBUtils.marshaller(ratedTransactionDtos, new File(dir + File.separator + exportFileName + "_" + timestamp + ".xml"));
        
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
    
    private  <T extends Enum<T>> List<T> getTargetStatusList(JobInstance jobInstance, Class<T> clazz, String cfCode) {
        List<T> formattedStatus = new ArrayList<T>();
        String statusListStr = (String) this.getParamOrCFValue(jobInstance, cfCode);
        if (statusListStr != null && !statusListStr.isEmpty()) {
            List<String> statusList = Arrays.asList(statusListStr.split(SPLIT_CHAR));
            for (String status : statusList) {
                T statusEnum = T.valueOf(clazz, status.toUpperCase());
                if (statusEnum != null) {
                    formattedStatus.add(statusEnum);
                }
            }
        }
        return formattedStatus;
    }

}