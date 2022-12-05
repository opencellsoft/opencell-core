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
import java.util.Map;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.service.job.Job;
import org.meveo.service.payments.impl.DDRequestBuilderFactory;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestBuilderService;

/**
 * The Class SepaRejectedTransactionsJob consume sepa/paynum or any custom rejected files (ddRequest file callBacks).
 * 
 * @author anasseh
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class SepaRejectedTransactionsJob extends Job {

    /** paramBeanFactory to instantiate adequate ParamBean. */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /** The sepa rejected transactions job bean. */
    @Inject
    private SepaRejectedTransactionsJobBean sepaRejectedTransactionsJobBean;

    /** The dd request builder service. */
    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    /** The dd request builder factory. */
    @Inject
    private DDRequestBuilderFactory ddRequestBuilderFactory;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        DDRequestBuilder ddRequestBuilder = null;
        String ddRequestBuilderCode = null;
        if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "RejectSepaJob_ddRequestBuilder") != null) {
            ddRequestBuilderCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "RejectSepaJob_ddRequestBuilder")).getCode();
            ddRequestBuilder = ddRequestBuilderService.findByCode(ddRequestBuilderCode);
        } else {
            throw new BusinessException("Can't find ddRequestBuilder");
        }
        if (ddRequestBuilder == null) {
            throw new BusinessException("Can't find ddRequestBuilder by code:" + ddRequestBuilderCode);
        }
        DDRequestBuilderInterface ddRequestBuilderInterface = ddRequestBuilderFactory.getInstance(ddRequestBuilder);
        String prefix = (String) this.getParamOrCFValue(jobInstance, "RejectSepaJob_fileNamePrefix");
        String ext = (String) this.getParamOrCFValue(jobInstance, "RejectSepaJob_fileNameExtension");
        String inputDir = paramBeanFactory.getChrootDir() + ((String) this.getParamOrCFValue(jobInstance, "RejectSepaJob_inputDir")).replaceAll("\\..", "");
        log.info("inputDir=" + inputDir);
        File dir = new File(inputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ArrayList<String> fileExtensions = new ArrayList<String>();
        fileExtensions.add(ext);

        File[] files = FileUtils.listFiles(inputDir, fileExtensions, prefix);
        if (files == null || files.length == 0) {
            result.setReport("No files!");
        } else {
            int numberOfFiles = files.length;
            log.info("InputFiles job " + numberOfFiles + " to import");
            result.setNbItemsToProcess(numberOfFiles);
            for (File file : files) {
                if (!jobExecutionService.isShouldJobContinue(result.getJobInstance().getId())) {
                    break;
                }
                sepaRejectedTransactionsJobBean.execute(result, jobInstance, file, ddRequestBuilderInterface, inputDir);
            }
        }

        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.PAYMENT;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        CustomFieldTemplate inputDirectoryCF = new CustomFieldTemplate();
        inputDirectoryCF.setCode("RejectSepaJob_inputDir");
        inputDirectoryCF.setAppliesTo("JobInstance_SepaRejectedTransactionsJob");
        inputDirectoryCF.setActive(true);
        inputDirectoryCF.setDescription(resourceMessages.getString("flatFile.inputDir"));
        inputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
        inputDirectoryCF.setDefaultValue(null);
        inputDirectoryCF.setValueRequired(true);
        inputDirectoryCF.setMaxValue(256L);
        inputDirectoryCF.setGuiPosition("tab:Configuration:0;field:0");
        result.put("RejectSepaJob_inputDir", inputDirectoryCF);

        CustomFieldTemplate fileNamePrefixCF = new CustomFieldTemplate();
        fileNamePrefixCF.setCode("RejectSepaJob_fileNamePrefix");
        fileNamePrefixCF.setAppliesTo("JobInstance_SepaRejectedTransactionsJob");
        fileNamePrefixCF.setActive(true);
        fileNamePrefixCF.setDescription(resourceMessages.getString("flatFile.fileNamePrefix"));
        fileNamePrefixCF.setFieldType(CustomFieldTypeEnum.STRING);
        fileNamePrefixCF.setDefaultValue(null);
        fileNamePrefixCF.setValueRequired(true);
        fileNamePrefixCF.setMaxValue(256L);
        fileNamePrefixCF.setGuiPosition("tab:Configuration:0;field:1");
        result.put("RejectSepaJob_fileNamePrefix", fileNamePrefixCF);

        CustomFieldTemplate fileNameExtensionCF = new CustomFieldTemplate();
        fileNameExtensionCF.setCode("RejectSepaJob_fileNameExtension");
        fileNameExtensionCF.setAppliesTo("JobInstance_SepaRejectedTransactionsJob");
        fileNameExtensionCF.setActive(true);
        fileNameExtensionCF.setDescription(resourceMessages.getString("flatFile.fileNameExtension"));
        fileNameExtensionCF.setFieldType(CustomFieldTypeEnum.STRING);
        fileNameExtensionCF.setDefaultValue(null);
        fileNameExtensionCF.setValueRequired(true);
        fileNameExtensionCF.setMaxValue(256L);
        fileNameExtensionCF.setGuiPosition("tab:Configuration:0;field:2");
        result.put("RejectSepaJob_fileNameExtension", fileNameExtensionCF);

        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("RejectSepaJob_ddRequestBuilder");
        payentGatewayCF.setAppliesTo("JobInstance_SepaRejectedTransactionsJob");
        payentGatewayCF.setActive(true);
        payentGatewayCF.setDescription("DDRequest builder");
        payentGatewayCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        payentGatewayCF.setEntityClazz(DDRequestBuilder.class.getName());
        payentGatewayCF.setValueRequired(true);
        payentGatewayCF.setGuiPosition("tab:Configuration:0;field:3");
        result.put("RejectSepaJob_ddRequestBuilder", payentGatewayCF);

        Map<String, String> lisValuesCreditDebit = new HashMap<String, String>();
        lisValuesCreditDebit.put("Credit", "Payment");
        lisValuesCreditDebit.put("Debit", "Refund");

        CustomFieldTemplate creditOrDebitCF = new CustomFieldTemplate();
        creditOrDebitCF.setCode("RejectSepaJob_creditOrDebit");
        creditOrDebitCF.setAppliesTo("JobInstance_SepaRejectedTransactionsJob");
        creditOrDebitCF.setActive(true);
        creditOrDebitCF.setDefaultValue("Credit");
        creditOrDebitCF.setDescription(resourceMessages.getString("jobExecution.paymentOrRefund"));
        creditOrDebitCF.setFieldType(CustomFieldTypeEnum.LIST);
        creditOrDebitCF.setValueRequired(true);
        creditOrDebitCF.setListValues(lisValuesCreditDebit);
        creditOrDebitCF.setGuiPosition("tab:Configuration:0;field:4");
        result.put("RejectSepaJob_creditOrDebit", creditOrDebitCF);

        return result;
    }
}
