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

import static java.util.Optional.of;
import static org.meveo.model.accounting.CustomLockOption.AFTER_END_OF_SUB_AP_PERIOD;
import static org.meveo.model.accounting.RegularUserLockOption.CUSTOM;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.CustomLockOption;
import org.meveo.model.accounting.RegularUserLockOption;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.accounting.impl.AccountingPeriodService;
import org.meveo.service.accounting.impl.SubAccountingPeriodService;

/**
 * Job implementation to close sub accounting periods
 * 
 * @author Amine BEN AICHA
 * @lastModifiedVersion 12.0
 */
@Stateless
public class AccountingPeriodClosingJobBean extends IteratorBasedJobBean<SubAccountingPeriod> {

    private static final long serialVersionUID = 5265725825495549750L;

    @Inject
    private AccountingPeriodService accountingPeriodService;

    @Inject
    private SubAccountingPeriodService subAccountingPeriodService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, null, this::closeSubAccountingPeriods, null, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of SubAccountingPeriod
     */
    private Optional<Iterator<SubAccountingPeriod>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        Date endDate = new Date();
        AccountingPeriod accountingPeriod = accountingPeriodService.findOpenAccountingPeriodByDate(endDate);
        if (accountingPeriod == null) {
            log.warn("No accounting period has been defined for date : {}", endDate);
            return of(new SynchronizedIterator<>(new ArrayList<>()));
        }

        RegularUserLockOption regularUserLockOption = accountingPeriod.getRegularUserLockOption();
        if (regularUserLockOption != null && regularUserLockOption == CUSTOM) {
            Integer days = accountingPeriod.getCustomLockNumberDays();
            CustomLockOption customLockOption = accountingPeriod.getCustomLockOption();
            if (customLockOption != null) {
                if (customLockOption == AFTER_END_OF_SUB_AP_PERIOD) {
                    days *= -1;
                }
                endDate = addDaysToDate(endDate, days);
            }
        }

        List<SubAccountingPeriod> subAccountingPeriods = subAccountingPeriodService.findByAccountingPeriodAndEndDate(accountingPeriod, endDate);
        return of(new SynchronizedIterator<>(subAccountingPeriods));
    }

    /**
     * Close subAccounting periods
     * 
     * @param subAccountingPeriods
     * @param jobExecutionResult Job execution result
     */
    private void closeSubAccountingPeriods(List<SubAccountingPeriod> subAccountingPeriods, JobExecutionResultImpl jobExecutionResult) {
        AccountingPeriod accountingPeriod = subAccountingPeriods.get(0).getAccountingPeriod();
        List<Long> ids = subAccountingPeriods.stream().map(SubAccountingPeriod::getId).collect(Collectors.toList());
        subAccountingPeriodService.closeSubAccountingPeriods(ids);
        subAccountingPeriodService.resetSequenceIfIsTheLastPeriode(accountingPeriod, ids);
    }
}