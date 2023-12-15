package org.meveo.admin.job.partitioning;

import org.meveo.admin.job.BaseJobBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.admin.partitioning.EdrPartitionLog;
import org.meveo.model.admin.partitioning.RTPartitionLog;
import org.meveo.model.admin.partitioning.WoPartitionLog;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.tech.EdrPartitionLogService;
import org.meveo.service.tech.RtPartitionLogService;
import org.meveo.service.tech.WoPartitionLogService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class PartitionPurgeJobBean extends BaseJobBean {
    
    @Inject
    private FinanceSettingsService financeSettingsService;
    
    @Inject
    private WoPartitionLogService woPartitionLogService;
    
    @Inject
    private RtPartitionLogService rtPartitionLogService;
    
    @Inject
    private EdrPartitionLogService edrPartitionLogService;
    
    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;
    
    @TransactionAttribute
    public void execute(JobExecutionResultImpl jobExecutionResult, String parameter) {

        FinanceSettings financeSetting = financeSettingsService.getFinanceSetting();
        int nbPartitionToKeep = Optional.ofNullable(financeSetting)
                                            .map(FinanceSettings::getNbPartitionsToKeep)
                                            .orElse(0);
        if(nbPartitionToKeep > 0) {

            List<WoPartitionLog> woPartitionToPurge = woPartitionLogService.list()
                                                                           .stream()
                                                                           .filter(woPartitionLog -> woPartitionLog.getPurgeDate() == null)
                                                                           .sorted((o1, o2) -> o2.getPeriodFrom().compareTo(o1.getPeriodFrom()))
                                                                           .skip(nbPartitionToKeep)
                                                                           .collect(Collectors.toList());

            if (!woPartitionToPurge.isEmpty()) {
                emWrapper.getEntityManager().createNativeQuery("DROP TABLE " + woPartitionToPurge.stream().map(WoPartitionLog::getPartitionName).collect(Collectors.joining(","))).executeUpdate();
                woPartitionToPurge.forEach(woPartitionLog -> woPartitionLog.setPurgeDate(new Date()));
                jobExecutionResult.setNbItemsCorrectlyProcessed(woPartitionToPurge.size());
            }
            
            List<RTPartitionLog> rtPartitionToPurge = rtPartitionLogService.list()
                                                                           .stream()
                                                                           .filter(rtPartitionLog -> rtPartitionLog.getPurgeDate() == null)
                                                                           .sorted((o1, o2) -> o2.getPeriodFrom().compareTo(o1.getPeriodFrom()))
                                                                           .skip(nbPartitionToKeep)
                                                                           .collect(Collectors.toList());

            if (!rtPartitionToPurge.isEmpty()) {
                emWrapper.getEntityManager().createNativeQuery("DROP TABLE " + rtPartitionToPurge.stream().map(RTPartitionLog::getPartitionName).collect(Collectors.joining(","))).executeUpdate();
                rtPartitionToPurge.forEach(rtPartitionLog -> rtPartitionLog.setPurgeDate(new Date()));
                jobExecutionResult.setNbItemsCorrectlyProcessed(jobExecutionResult.getNbItemsCorrectlyProcessed() + rtPartitionToPurge.size());
            }

            List<EdrPartitionLog> edrPartitionToPurge = edrPartitionLogService.list()
                                                                              .stream()
                                                                              .filter(edrPartitionLog -> edrPartitionLog.getPurgeDate() == null)
                                                                              .sorted((o1, o2) -> o2.getPeriodFrom().compareTo(o1.getPeriodFrom()))
                                                                              .skip(nbPartitionToKeep)
                                                                              .collect(Collectors.toList());

            if (!edrPartitionToPurge.isEmpty()) {
                emWrapper.getEntityManager().createNativeQuery("DROP TABLE " + edrPartitionToPurge.stream().map(EdrPartitionLog::getPartitionName).collect(Collectors.joining(","))).executeUpdate();
                edrPartitionToPurge.forEach(edrPartitionLog -> edrPartitionLog.setPurgeDate(new Date()));
                jobExecutionResult.setNbItemsCorrectlyProcessed(jobExecutionResult.getNbItemsCorrectlyProcessed() + edrPartitionToPurge.size());
            }
            
            jobExecutionResult.setNbItemsToProcess(woPartitionToPurge.size() + rtPartitionToPurge.size() + edrPartitionToPurge.size());

        } else {
            jobExecutionResult.registerError("The setting FinanceSettings.nbPartitionToKeep is missing");
        }
    }
    
}
