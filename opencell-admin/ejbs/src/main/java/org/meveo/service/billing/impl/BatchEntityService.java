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
package org.meveo.service.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.MassUpdateJob;
import org.meveo.admin.job.UpdateStepExecutor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BatchEntity;
import org.meveo.model.billing.BatchEntityStatusEnum;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.communication.impl.InternationalSettingsService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

/**
 * BatchEntityService : A class for Batch entity persistence services.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Stateless
public class BatchEntityService extends PersistenceService<BatchEntity> {

    private static final String DEFAULT_EMAIL_ADDRESS = "no-reply@opencellsoft.com";
    private static final String MASS_UPDATE_JOB_CODE = "MassUpdateJob";

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @Inject
    private UserService userService;

    @Inject
    EmailTemplateService emailTemplateService;

    @Inject
    private InternationalSettingsService internationalSettingsService;

    @Inject
    private ProviderService providerService;

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    private EmailSender emailSender;

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    /**
     * Create the new batch entity
     *
     * @param filters      the filters
     * @param targetJob    the target job
     * @param targetEntity the target entity
     */
    public void create(Map<String, Object> filters, String targetJob, String targetEntity) {
        BatchEntity batchEntity = new BatchEntity();
        batchEntity.setCode(getBatchEntityCode(null));
        batchEntity.setDescription(targetJob + "_" + targetEntity);
        batchEntity.setTargetJob(targetJob);
        batchEntity.setTargetEntity(targetEntity);
        batchEntity.setFilters(filters);
        batchEntity.setNotify(true);
        create(batchEntity);
    }

    /**
     * Update the batch entity and register job execution error
     *
     * @param batchEntity        the batch entity
     * @param jobExecutionResult the job execution tesult
     * @param errorMessage       the error message
     */
    public void update(BatchEntity batchEntity, JobExecutionResultImpl jobExecutionResult, String errorMessage) {
        batchEntity.setStatus(BatchEntityStatusEnum.FAILURE);
        update(batchEntity);
        jobExecutionResult.registerError(errorMessage);
    }

    /**
     * Call BatchEntity.cancelOpenedBatchEntity Named query to cancel opened RatedTransaction.
     *
     * @param id rated batch entity to cancel
     */
    public void cancel(Long id) {
        getEntityManager().createNamedQuery("BatchEntity.cancelOpenedBatchEntity").setParameter("id", id).executeUpdate();
    }

    /**
     * Mark a multiple Wallet operations to rerate
     *
     * @param batchEntities      batch entities
     * @param jobExecutionResult Job execution result
     * @param emailTemplateId    Email template id
     */
    public void markWoToRerate(List<BatchEntity> batchEntities, JobExecutionResultImpl jobExecutionResult, Long emailTemplateId) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        jobExecutionResult.addNbItemsToProcess(batchEntities.size());
        for (BatchEntity batchEntity : batchEntities) {
            try {
                batchEntity.setStatus(BatchEntityStatusEnum.PROCESSING);
                batchEntity.setJobInstance(jobInstance);

                String entityClassName = "WalletOperation";
                StringBuilder updateQueryBuilder = new StringBuilder("UPDATE ").append(entityClassName).append(" SET ")
                        .append("status=").append(QueryBuilder.paramToString(WalletOperationStatusEnum.TO_RERATE))
                        .append(", updated=").append(QueryBuilder.paramToString(new Date()))
                        .append(", reratingBatch.id=").append(batchEntity.getId());

                QueryBuilder queryBuilder = new QueryBuilder(updateQueryBuilder.toString());
                Map<String, Object> filters = addFilters(batchEntity.getFilters());
                //nativePersistenceService.update(queryBuilder, entityClassName, filters);
                executeMassUpdateJob(queryBuilder, entityClassName, filters);

                batchEntity.setStatus(BatchEntityStatusEnum.SUCCESS);
                update(batchEntity);
                jobExecutionResult.registerSucces();
                if (batchEntity.isNotify() && emailTemplateId != null) {
                    sendEmail(batchEntity, emailTemplateId, jobExecutionResult);
                }
            } catch (Exception e) {
                log.error("Failed to process the entity batch id : " + batchEntity.getId(), e);
                methodCallingUtils.callMethodInNewTx(() -> update(batchEntity, jobExecutionResult,
                        e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            }
        }
    }

    /**
     * Execute the mass update job
     *
     * @param updateQueryBuilder the update query builder
     * @param entityClassName    the entity class name
     * @param filters            the filters
     */
    private void executeMassUpdateJob(QueryBuilder updateQueryBuilder, String entityClassName, Map<String, Object> filters) {
        String massUpdateJobCode = ParamBeanFactory.getAppScopeInstance().getProperty("job.massUpdateJob.code", MASS_UPDATE_JOB_CODE);
        JobInstance jobInstance = ofNullable(jobInstanceService.findByCode(massUpdateJobCode))
                .orElseThrow(() -> new EntityDoesNotExistsException(JobInstance.class, massUpdateJobCode));
        Map<String, Object> params = new HashMap<>();
        params.put(MassUpdateJob.CF_MASS_UPDATE_CHUNK, 100000L);
        params.put(UpdateStepExecutor.PARAM_UPDATE_QUERY, nativePersistenceService.getUpdateQuery(updateQueryBuilder, entityClassName, filters));
        params.put(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY, getSelectQuery(entityClassName, filters));
        params.put(UpdateStepExecutor.PARAM_TABLE_ALIAS, "a");
        params.put(UpdateStepExecutor.PARAM_NATIVE_QUERY, false);
        jobInstance.setRunTimeValues(params);
        jobExecutionService.executeJob(jobInstance, params, JobLauncherEnum.API);
    }

    /**
     * Gets the select query
     *
     * @param entityClassName the entity class name
     * @param filters         the filters
     * @return the select query
     */
    private String getSelectQuery(String entityClassName, Map<String, Object> filters) {
        String selectQuery = null;
        if (filters != null && !filters.isEmpty()) {
            PaginationConfiguration searchConfig = new PaginationConfiguration(filters);
            searchConfig.setFetchFields(Arrays.asList("MIN(id)", "MAX(id)"));
            selectQuery = nativePersistenceService.getQuery(entityClassName, searchConfig, null).getQueryAsString();
            selectQuery = selectQuery.replaceAll("MIN\\(id\\)", "MIN(a.id)");
            selectQuery = selectQuery.replaceAll("MAX\\(id\\)", "MAX(a.id)");
        }
        return selectQuery;
    }

    /**
     * Add new filters to original filters
     *
     * @param originalFilters Original filters
     * @return all filters (new + original)
     */
    private Map<String, Object> addFilters(Map<String, Object> originalFilters) {
        Set<String> statusTobeExcluded = Stream.of(WalletOperationStatusEnum.TO_RERATE.name(), WalletOperationStatusEnum.RERATED.name(),
                        WalletOperationStatusEnum.CANCELED.name())
                .collect(Collectors.toCollection(HashSet::new));
        Map<String, Object> filters = new HashMap<>();
        if (originalFilters != null) {
            filters.putAll(originalFilters);
        }
        if (filters.get("not-inList status") != null) {
            if (filters.get("not-inList status") instanceof Collection) {
                statusTobeExcluded.addAll(((Collection<String>) filters.get("not-inList status")).stream().map(val -> val != null ? val.toUpperCase() : val)
                        .collect(Collectors.toSet()));
            } else {
                statusTobeExcluded.add((String) filters.get("not-inList status"));
            }
        }
        filters.put("not-inList status", statusTobeExcluded);
        Map<String, Object> filter = new LinkedHashMap();
        filter.put("$operator", "OR");
        filter.put("ne ratedTransaction.status", RatedTransactionStatusEnum.BILLED.name());
        filter.put("ne ratedTransaction.invoiceLine.status", InvoiceLineStatusEnum.BILLED.name());
        Stream.of(filters.keySet().toArray())
                .filter(key -> {
                    String keyObject = (String) key;
                    if (keyObject.matches("\\$filter[0-9]+$")) {
                        return filter.equals(filters.get(key));
                    }
                    return false;
                }).forEach(key -> filters.remove(key));
        filter.put("inList status", Set.of(WalletOperationStatusEnum.OPEN.name(), WalletOperationStatusEnum.F_TO_RERATE.name(),
                WalletOperationStatusEnum.REJECTED.name()));
        filters.put("$filter0101", filter);
        return filters;
    }

    /**
     * Mark a multiple Wallet operations to rerate
     *
     * @param updateQuery the update query which mark Wallet operations to rerate
     * @param ids         the ids of Wallet operations to be marked
     * @return the number of updated Wallet operations
     */
    public int markWoToRerate(StringBuilder updateQuery, List<Long> ids) {
        return nativePersistenceService.update(updateQuery, ids);
    }

    /**
     * Send Email to the creator
     *
     * @param batchEntity        Batch entity
     * @param emailTemplateId    Email template id
     * @param jobExecutionResult Job execution result
     */
    private void sendEmail(BatchEntity batchEntity, Long emailTemplateId, JobExecutionResultImpl jobExecutionResult) {
        String from = DEFAULT_EMAIL_ADDRESS;
        Provider provider = providerService.getProvider();
        if (provider != null && StringUtils.isNotBlank(provider.getEmail())) {
            from = provider.getEmail();
        }
        String username = batchEntity.getAuditable().getCreator();
        User user = userService.findByUsername(username, false);
        if (user == null) {
            log.warn("No user with username {} was found", username);
            return;
        }
        String to = user.getEmail();
        if (StringUtils.isBlank(to)) {
            log.warn("Cannot send batch entity notification message to the creator {}, because he doesn't have an email", username);
            return;
        }

        EmailTemplate emailTemplate = ofNullable(emailTemplateService.findById(emailTemplateId))
                .orElseThrow(() -> new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateId));
        String localeAttribute = userService.getUserAttributeValue(username, "locale");
        Locale locale = !StringUtils.isBlank(localeAttribute) ? new Locale(localeAttribute) : new Locale("en");
        String languageCode = locale.getISO3Language().toUpperCase();

        String emailSubject = internationalSettingsService.resolveSubject(emailTemplate, languageCode);
        String emailContent = internationalSettingsService.resolveEmailContent(emailTemplate, languageCode);
        String htmlContent = internationalSettingsService.resolveHtmlContent(emailTemplate, languageCode);


        Map<Object, Object> params = new HashMap<>();
        params.put("batchEntityId", batchEntity.getId());
        params.put("batchEntityStatus", batchEntity.getStatus());
        params.put("batchEntityDescription", StringUtils.isNotBlank(batchEntity.getDescription()) ? batchEntity.getDescription() : "");
        params.put("jobExecutionId", jobExecutionResult.getId() != null ? jobExecutionResult.getId() : "");
        params.put("jobInstanceCode", batchEntity.getJobInstance() != null ? batchEntity.getJobInstance().getCode() : "");

        String subject = StringUtils.isNotBlank(emailTemplate.getSubject()) ? evaluateExpression(emailSubject, params, String.class) : "";
        String content = StringUtils.isNotBlank(emailTemplate.getTextContent()) ? evaluateExpression(emailContent, params, String.class) : "";
        String contentHtml = StringUtils.isNotBlank(emailTemplate.getHtmlContent()) ? evaluateExpression(htmlContent, params, String.class) : "";

        emailSender.send(from, asList(from), asList(to), subject, content, contentHtml);
    }

    /**
     * Gets the batch entity code
     *
     * @param defaultCode the default batch entity code
     * @return the batch entity code
     */
    public String getBatchEntityCode(String defaultCode) {
        String code = defaultCode;
        if (StringUtils.isBlank(code)) {
            CustomGenericEntityCode customGenericEntityCode = ofNullable(customGenericEntityCodeService.findByClass(BatchEntity.class.getName()))
                    .orElseThrow(() -> new BusinessException("Generic code does not exist for the BatchEntity class."));
            code = serviceSingleton.getGenericCode(customGenericEntityCode);
        }
        return code;
    }
}