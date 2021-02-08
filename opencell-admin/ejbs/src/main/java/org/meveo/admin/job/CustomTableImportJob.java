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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobMultithreadingHistoryInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.job.Job;

/**
 * Import data to custom tables
 * 
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class CustomTableImportJob extends Job {

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private CustomTableService customTableService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @EJB
    private CustomTableImportJob customTableImportJobAsync;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterRunningThreads(result, nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            ParamBean parambean = paramBeanFactory.getInstance();
            String customTableDir = parambean.getChrootDir(currentUser.getProviderCode()) + File.separator + "imports" + File.separator + "customTables" + File.separator;

            MeveoUser lastCurrentUser = currentUser.unProxy();

            // Process files that should overrite data

            File inputDir = new File(customTableDir + "input");

            if (!inputDir.exists()) {
                inputDir.mkdirs();
            }
            List<File> files = FileUtils.listFiles(inputDir, "csv", null);
            if (files != null && !files.isEmpty()) {

                result.setNbItemsToProcess(files.size());

                List<Future<String>> futures = new ArrayList<>();
                SubListCreator subListCreator = new SubListCreator(files, nbRuns.intValue());

                while (subListCreator.isHasNext()) {
                    futures.add(customTableImportJobAsync.launchAndForget(customTableDir, (List<File>) subListCreator.getNextWorkSet(), result, jobInstance.getParametres(),
                        lastCurrentUser));
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
                        jobExecutionService.registerError(result, cause.getMessage());
                        log.error("Failed to execute async method", cause);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Failed to run custom table data import", e);
            jobExecutionService.registerError(result, e.getMessage());
        }
    }

    /**
     * Import a set of file
     * 
     * @param customTableDir Custom table import file directory
     * @param files Files to import
     * @param result Job execution result
     * @param parameter Execution parameters
     * @param lastCurrentUser Current user
     * @return Future execution object
     * @throws BusinessException Business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    @Interceptors({ JobMultithreadingHistoryInterceptor.class })
    public Future<String> launchAndForget(String customTableDir, List<File> files, JobExecutionResultImpl result, String parameter, MeveoUser lastCurrentUser)
            throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        String outputDir = customTableDir + "output";
        String rejectDir = customTableDir + "reject";

        File f = new File(outputDir);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(rejectDir);
        if (!f.exists()) {
            f.mkdirs();
        }

        for (File file : files) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }

            log.info("Importing data into custom table from file {}", file.getAbsolutePath());
            String filename = file.getName();

            boolean appendImportedData = filename.contains(CustomTableService.FILE_APPEND);

            List<CustomEntityTemplate> cets = customEntityTemplateService.listCustomTableTemplates();
            CustomEntityTemplate customTable = null;

            for (CustomEntityTemplate cet : cets) {
                if (filename.startsWith(cet.getDbTablename())) {
                    customTable = cet;
                    break;
                }
            }

            try {
                if (customTable == null) {
                    throw new BusinessException("No Custom table matched by name " + filename);
                }

                customTableService.importData(customTable, file, appendImportedData);
                jobExecutionService.registerSucces(result);
                FileUtils.moveFile(outputDir, file, filename);

            } catch (Exception e) {
                log.error("Failed to import data from file {} into custom table", filename, e);
                jobExecutionService.registerError(result, filename, e.getMessage());
                FileUtils.moveFile(rejectDir, file, filename);
            }
        }

        return new AsyncResult<String>("OK");
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode(CF_NB_RUNS);
        nbRuns.setAppliesTo("JobInstance_CustomTableImportJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setDefaultValue("-1");
        nbRuns.setValueRequired(false);
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode(Job.CF_WAITING_MILLIS);
        waitingMillis.setAppliesTo("JobInstance_CustomTableImportJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setValueRequired(false);
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, waitingMillis);

        return result;
    }
}