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

package org.meveo.admin.job.importexport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.CustomFields;
import org.meveo.model.jaxb.subscription.Accesses;
import org.meveo.model.jaxb.subscription.Services;
import org.meveo.model.jaxb.subscription.Status;
import org.meveo.model.jaxb.subscription.Subscription;
import org.meveo.model.jaxb.subscription.Subscriptions;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ExportSubscriptionsJobBean {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");

    @Inject
    private Logger log;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;

    private Subscriptions subscriptions;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String parameter) {
        ParamBean param = paramBeanFactory.getInstance();
        String exportDir = paramBeanFactory.getChrootDir() + File.separator + "exports" + File.separator + "subscriptions" + File.separator;
        log.info("exportDir=" + exportDir);
        File dir = new File(exportDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = sdf.format(new Date());
        List<org.meveo.model.billing.Subscription> subs = subscriptionService.list();
        subscriptions = subscriptionsToDto(subs, param.getProperty("connectorCRM.dateFormat", "yyyy-MM-dd"), result.getJobInstance());
        int nbItems = subscriptions.getSubscription() != null ? subscriptions.getSubscription().size() : 0;
        result.setNbItemsToProcess(nbItems);
        try {
            JAXBUtils.marshaller(subscriptions, new File(dir + File.separator + "SUB_" + timestamp + ".xml"));
            result.setNbItemsCorrectlyProcessed(nbItems);
            logResult();
        } catch (JAXBException e) {
            log.error("Failed to export subscriptions job", e);
            result.getErrors().add(e.getMessage());
            result.setReport(e.getMessage());
            result.setNbItemsProcessedWithError(nbItems);
        }

    }

    private void logResult() {
        if (subscriptions.getSubscription() != null) {
            int nbItems;
            for (Subscription subscription : subscriptions.getSubscription()) {
                nbItems = subscription.getAccesses() != null && subscription.getAccesses().getAccess() != null ? subscription.getAccesses().getAccess().size() : 0;
                log.info("Number of processed accessePoints for the subscription {} in ExportSubscriptionsJob is : {}", subscription.getCode(), nbItems);
            }
        }
    }

    private Subscriptions subscriptionsToDto(List<org.meveo.model.billing.Subscription> subs, String dateFormat, JobInstance jobInstance) {
        Subscriptions dto = new Subscriptions();
        if (subs != null) {
            int i = 0;
            int checkJobStatusEveryNr = jobInstance.getJobSpeed().getCheckNb();
            
            for (org.meveo.model.billing.Subscription sub : subs) {
                if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
                    break;
                }
                dto.getSubscription().add(subscriptionToDto(sub, dateFormat));
                i++;
            }
        }
        return dto;
    }

    private Subscription subscriptionToDto(org.meveo.model.billing.Subscription sub, String dateFormat) {
        Subscription dto = new Subscription();
        if (sub != null) {
            dto.setSubscriptionDate(sub.getSubscriptionDate() == null ? null : DateUtils.formatDateWithPattern(sub.getSubscriptionDate(), dateFormat));
            dto.setEndAgreementDate(sub.getEndAgreementDate() == null ? null : DateUtils.formatDateWithPattern(sub.getEndAgreementDate(), dateFormat));
            dto.setDescription(sub.getDescription());
            if (sub.getCfValues() != null) {
                dto.setCustomFields(CustomFields.toDTO(sub.getCfValues().getValuesByCode()));
            }
            dto.setCode(sub.getCode());
            dto.setUserAccountId(sub.getUserAccount() == null ? null : sub.getUserAccount().getCode());
            dto.setOfferCode(sub.getOffer() == null ? null : sub.getOffer().getCode());
            dto.setStatus(new Status(sub, dateFormat));
            dto.setServices(new Services(sub, dateFormat));
            dto.setAccesses(new Accesses(sub, dateFormat));
        }
        return dto;
    }
}
