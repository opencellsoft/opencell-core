package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * The Class MediationJob consume standard cdr files.
 * 
 * @author Wassim Drira
 * @author HORRI khalid
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class MediationJob extends Job {

    @Inject
    private MediationJobBean mediationJobBean;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {

            ParamBean parambean = paramBeanFactory.getInstance();
            String meteringDir = parambean.getChrootDir(currentUser.getProviderCode()) + File.separator + "imports" + File.separator + "metering" + File.separator;

            String inputDir = meteringDir + "input";
            String cdrExtension = parambean.getProperty("mediation.extensions", "csv");
            ArrayList<String> cdrExtensions = new ArrayList<String>();
            cdrExtensions.add(cdrExtension);

            File f = new File(inputDir);
            if (!f.exists()) {
                f.mkdirs();
            }

            String outputDir = meteringDir + "output";
            String rejectDir = meteringDir + "reject";
            String archiveDir = meteringDir + "archive";

            f = new File(outputDir);
            if (!f.exists()) {
                log.debug("outputDir {} not exist", outputDir);
                f.mkdirs();
                log.debug("outputDir {} creation ok", outputDir);
            }
            f = new File(rejectDir);
            if (!f.exists()) {
                log.debug("rejectDir {} not exist", rejectDir);
                f.mkdirs();
                log.debug("rejectDir {} creation ok", rejectDir);
            }
            f = new File(archiveDir);
            if (!f.exists()) {
                log.debug("archiveDir {} not exist", archiveDir);
                f.mkdirs();
                log.debug("archiveDir {} creation ok", archiveDir);
            }

            File[] files = FileUtils.listFiles(inputDir, cdrExtensions);
            if (files == null || files.length == 0) {
                log.debug("There is no file in {} with extension {} to by processed by Mediation {} job", inputDir, cdrExtensions, result.getJobInstance().getCode());
                return;
            }

            for (File file : files) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                mediationJobBean.execute(result, inputDir, outputDir, archiveDir, rejectDir, file, jobInstance.getParametres(), nbRuns, waitingMillis);
            }

        } catch (Exception e) {
            log.error("Failed to run mediation job", e);
            result.registerError(e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.MEDIATION;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("nbRuns");
        nbRuns.setAppliesTo("JobInstance_MediationJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setDefaultValue("-1");
        nbRuns.setValueRequired(false);
        result.put("nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("waitingMillis");
        waitingMillis.setAppliesTo("JobInstance_MediationJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setValueRequired(false);
        result.put("waitingMillis", waitingMillis);

        CustomFieldTemplate scriptJob = new CustomFieldTemplate();
        scriptJob.setCode("scriptJob");
        scriptJob.setAppliesTo("JobInstance_MediationJob");
        scriptJob.setActive(true);
        scriptJob.setAllowEdit(true);
        scriptJob.setMaxValue(Long.MAX_VALUE);
        scriptJob.setDescription(resourceMessages.getString("jobExecution.scriptJob"));
        scriptJob.setFieldType(CustomFieldTypeEnum.STRING);
        scriptJob.setValueRequired(false);
        scriptJob.setDefaultValue("");
        result.put("scriptJob", scriptJob);

        return result;
    }
}