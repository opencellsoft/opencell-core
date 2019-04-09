package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.MediationAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
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

    /** The mediation async. */
    @Inject
    private MediationAsync mediationAsync;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            try {
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e);
            }

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
            File[] files = FileUtils.listFiles(inputDir, cdrExtensions);
            if (files == null || files.length == 0) {
                return;
            }
            SubListCreator subListCreator = new SubListCreator(Arrays.asList(files), nbRuns.intValue());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            String scriptCode = (String) this.getParamOrCFValue(jobInstance, "scriptJob");
            while (subListCreator.isHasNext()) {
                futures.add(mediationAsync.launchAndForget((List<File>) subListCreator.getNextWorkSet(), result, jobInstance.getParametres(), lastCurrentUser, scriptCode));
                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

        } catch (Exception e) {
            log.error("Failed to run mediation", e);
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
        nbRuns.setDefaultValue("1");
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