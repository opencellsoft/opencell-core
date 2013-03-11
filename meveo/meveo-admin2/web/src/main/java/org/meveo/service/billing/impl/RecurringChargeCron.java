/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.service.billing.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.commons.utils.DateUtils;
import org.meveo.model.billing.ApplicationChgStatusEnum;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.billing.local.ChargeApplicationServiceLocal;
import org.meveo.service.billing.local.RecurringChargeInstanceServiceLocal;

/**
 * @author R.AITYAAZZA
 * @created 23 d�c. 10
 */
@Name("recurringChargeCron")
@AutoCreate
public class RecurringChargeCron {

    @In
    private RecurringChargeInstanceServiceLocal recurringChargeInstanceService;

    @In
    private ChargeApplicationServiceLocal chargeApplicationService;

    @Logger
    protected Log log;

    public void recurringChargeApplication() {
        log.debug("start recurringChargeApplication....");
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        try {
            // TODO: ajouter le filtre sur nextapplicationDate<today+1
            List<RecurringChargeInstance> activeRecurringChargeInstances = recurringChargeInstanceService.findByStatus(
                    InstanceStatusEnum.ACTIVE, DateUtils.addDaysToDate(new Date(), 1));

            for (RecurringChargeInstance activeRecurringChargeInstance : activeRecurringChargeInstances) {
                RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance
                        .getRecurringChargeTemplate();
                if (recurringChargeTemplate.getCalendar() == null) {
                    // FIXME : should not stop the method execution
                    throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code="
                            + recurringChargeTemplate.getCode());
                }
                Date applicationDate = null;
                if (recurringChargeTemplate.getApplyInAdvance()) {
                    applicationDate = activeRecurringChargeInstance.getNextChargeDate();
                } else {
                    applicationDate = activeRecurringChargeInstance.getChargeDate();
                }

                log.debug("nextapplicationDate=" + applicationDate);

                applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

                ServiceTemplate serviceTemplate = activeRecurringChargeInstance.getServiceInstance()
                        .getServiceTemplate();
                Calendar durationTermCalendar = null;
                Date nextDurationDate = null;
                try {
                    durationTermCalendar = serviceTemplate.getDurationTermCalendar();
                    nextDurationDate = durationTermCalendar.nextCalendarDate(applicationDate);
                    log.debug("nextDurationDate=" + nextDurationDate);
                } catch (Exception e) {
                    log.error("Cannot find duration term calendar for serviceTemplate.id=#0", serviceTemplate.getId());
                }
                if (!recurringChargeTemplate.getApplyInAdvance()) {
                    chargeApplicationService.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance,
                            false, recurringChargeTemplate, null);
                } else if (nextDurationDate != null && nextDurationDate.getTime() >= applicationDate.getTime()) {
                    chargeApplicationService.applyReccuringCharge(activeRecurringChargeInstance, false,
                            recurringChargeTemplate, null);

                } else {
                    Date previousapplicationDate = recurringChargeTemplate.getCalendar().previousCalendarDate(
                            DateUtils.addDaysToDate(applicationDate, -1));
                    InvoiceSubCategory invoiceSubCat = activeRecurringChargeInstance.getRecurringChargeTemplate()
                            .getInvoiceSubCategory();
                    Tax tax = invoiceSubCat.getTax();

                    String param2 = "du " + sdf.format(previousapplicationDate) + " au "
                            + sdf.format(DateUtils.addDaysToDate(applicationDate, -1));

                    ChargeApplication chargeApplication = new ChargeApplication(
                            activeRecurringChargeInstance.getCode(), activeRecurringChargeInstance.getDescription(),
                            activeRecurringChargeInstance.getServiceInstance().getSubscription(),
                            activeRecurringChargeInstance, activeRecurringChargeInstance.getCode(),
                            ApplicationChgStatusEnum.WAITING, ApplicationTypeEnum.RECURRENT, previousapplicationDate,
                            activeRecurringChargeInstance.getAmountWithoutTax(), activeRecurringChargeInstance
                                    .getAmount2(), activeRecurringChargeInstance.getServiceInstance().getQuantity(),
                            tax.getCode(), tax.getPercent(), null, applicationDate, invoiceSubCat, "1", param2, null,
                            null, activeRecurringChargeInstance.getCriteria1(), activeRecurringChargeInstance
                                    .getCriteria2(), activeRecurringChargeInstance.getCriteria3());
                    log.info("set application date to "
                            + activeRecurringChargeInstance.getServiceInstance().getSubscriptionDate());
                    chargeApplication.setSubscriptionDate(activeRecurringChargeInstance.getServiceInstance()
                            .getSubscriptionDate());
                    chargeApplicationService.create(chargeApplication, null, activeRecurringChargeInstance
                            .getRecurringChargeTemplate().getProvider());

                    Date nextApplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
                    activeRecurringChargeInstance.setChargeDate(applicationDate);
                    activeRecurringChargeInstance.setNextChargeDate(nextApplicationDate);
                    recurringChargeInstanceService.update(activeRecurringChargeInstance);
                }

            }

        } catch (Exception e) {
            log.error("recurringChargeApplication  technical error :#0", e);
        }
    }
}
