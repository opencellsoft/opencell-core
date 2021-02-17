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

package org.meveo.admin.job.dwh;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.time.DateUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.slf4j.Logger;

@Stateless
public class DWHQueryBean {

    @Inject
    private MeasurableQuantityService mqService;

    @Inject
    private MeasuredValueService mvService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    private Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    // iso 8601 date and datetime format
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    public void executeQuery(JobExecutionResultImpl result, String parameter) throws BusinessException {

        String measurableQuantityCode = parameter;
        Date toDate = new Date();

        if (!StringUtils.isBlank(parameter)) {
            if (parameter.indexOf("to=") > 0) {
                String s = parameter.substring(parameter.indexOf("to=") + 3);
                if (s.indexOf(";") > 0) {
                    measurableQuantityCode = parameter.substring(parameter.indexOf(";") + 1);
                    Date parsedDate = org.meveo.model.shared.DateUtils.guessDate(s.substring(0, s.indexOf(";")), "yyyy-MM-dd");
                    if (parsedDate != null) {
                        toDate = parsedDate;
                    }
                } else {
                    if (parameter.indexOf(";") > 0) {
                        measurableQuantityCode = parameter.substring(0, parameter.indexOf(";"));
                    } else {
                        measurableQuantityCode = null;
                    }
                }

            }
        }
        log.debug("measurableQuantityCode={}, toDate={}", measurableQuantityCode, toDate);

        List<MeasurableQuantity> mqList = new ArrayList<>();
        if (StringUtils.isBlank(measurableQuantityCode)) {
            mqList = mqService.listToBeExecuted(new Date());
        } else {
            MeasurableQuantity mq = mqService.findByCode(measurableQuantityCode);
            if (mq == null) {
                jobExecutionService.registerError(result, "Cannot find measurable quantity with code " + measurableQuantityCode);
                return;
            }
            mqList.add(mq);
        }
        result.setNbItemsToProcess(mqList.size());
        jobExecutionService.initCounterElementsRemaining(result, mqList.size());
        
        
        EntityManager em = emWrapper.getEntityManager();
        
        int ji = 0;
        for (MeasurableQuantity mq : mqList) {
            ji++;
            if (ji % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            if (StringUtils.isBlank(mq.getSqlQuery())) {
                jobExecutionService.registerError(result, "Measurable quantity with code " + measurableQuantityCode + " has no SQL query set.");
                log.info("Measurable quantity with code {} has no SQL query set.", measurableQuantityCode);
                continue;
            }

            try {
                if (mq.getLastMeasureDate() == null) {
                    mq.setLastMeasureDate(mq.getPreviousDate(toDate));
                }

                while (mq.getNextMeasureDate().before(toDate)) {
                    log.debug("resolve query:{}, nextMeasureDate={}, lastMeasureDate={}", mq.getSqlQuery(), mq.getNextMeasureDate(), mq.getLastMeasureDate());
                    String queryStr = mq.getSqlQuery().replaceAll("#\\{date\\}", df.format(mq.getLastMeasureDate()));
                    queryStr = queryStr.replaceAll("#\\{dateTime\\}", tf.format(mq.getLastMeasureDate()));
                    queryStr = queryStr.replaceAll("#\\{nextDate\\}", df.format(mq.getNextMeasureDate()));
                    queryStr = queryStr.replaceAll("#\\{nextDateTime\\}", tf.format(mq.getNextMeasureDate()));
                    log.debug("execute query:{}", queryStr);
                    Query query = em.createNativeQuery(queryStr);
                    @SuppressWarnings("unchecked")
                    List<Object> results = query.getResultList();
                    for (Object res : results) {
                        MeasurementPeriodEnum mve = (mq.getMeasurementPeriod() != null) ? mq.getMeasurementPeriod() : MeasurementPeriodEnum.DAILY;
                        BigDecimal value = BigDecimal.ZERO;
                        Date date = mq.getLastMeasureDate();
                        String dimension1 = mq.getDimension1();
                        String dimension2 = mq.getDimension2();
                        String dimension3 = mq.getDimension3();
                        String dimension4 = mq.getDimension4();
                        if (res instanceof Object[]) {
                            Object[] resTab = (Object[]) res;
                            value = new BigDecimal("" + resTab[0]);
                            int i = 1;
                            if (resTab.length > i) {
                                try {
                                    date = (Date) resTab[1];
                                    i++;
                                } catch (Exception e) {
                                }
                                if (resTab.length > i) {
                                    dimension1 = resTab[i] == null ? "" : resTab[i].toString();
                                    i++;
                                    if (resTab.length > i) {
                                        dimension2 = resTab[i] == null ? "" : resTab[i].toString();
                                        i++;
                                        if (resTab.length > i) {
                                            dimension3 = resTab[i] == null ? "" : resTab[i].toString();
                                            i++;
                                            if (resTab.length > i) {
                                                dimension4 = resTab[i] == null ? "" : resTab[i].toString();
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            value = new BigDecimal("" + res);
                        }
                        date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
                        MeasuredValue mv = mvService.getByDate(date, mve, mq);
                        if (mv == null) {
                            mv = new MeasuredValue();
                        }
                        mv.setMeasurableQuantity(mq);
                        mv.setMeasurementPeriod(mve);
                        mv.setValue(value);
                        mv.setDate(date);
                        mv.setDimension1(dimension1);
                        mv.setDimension2(dimension2);
                        mv.setDimension3(dimension3);
                        mv.setDimension4(dimension4);
                        if (mv.getId() == null) {
                            mvService.create(mv);
                        }
                    }
                    mq.increaseMeasureDate();
                    jobExecutionService.registerSucces(result);
                }
            } catch (Exception e) {
                jobExecutionService.registerError(result, "Measurable quantity with code " + measurableQuantityCode + " contain invalid SQL query: " + e.getMessage());
                log.error("Measurable quantity with code " + measurableQuantityCode + " contain invalid SQL query", e);
            }

            jobExecutionService.decCounterElementsRemaining(result);
        }
    }
}
