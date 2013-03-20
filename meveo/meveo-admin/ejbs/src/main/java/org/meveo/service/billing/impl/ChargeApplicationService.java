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
import java.util.ResourceBundle;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.commons.utils.DateUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ApplicationChgStatusEnum;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.local.BillingAccountServiceLocal;
import org.meveo.service.billing.local.ChargeApplicationServiceLocal;
import org.meveo.service.billing.local.InvoiceSubCategoryCountryServiceLocal;
import org.meveo.service.catalog.local.OneShotChargeTemplateServiceLocal;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
@Name("chargeApplicationService")
@AutoCreate
public class ChargeApplicationService extends BusinessService<ChargeApplication> implements
        ChargeApplicationServiceLocal {

    @In
    private InvoiceSubCategoryCountryServiceLocal invoiceSubCategoryCountryService;

    private DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String str_tooPerceived = ResourceBundle.getBundle("messages").getString("str_tooPerceived");

    @In
    private BillingAccountServiceLocal billingAccountService;
    
    @In
    private OneShotChargeTemplateServiceLocal oneShotChargeTemplateService;

    public void oneShotChargeApplication(Subscription subscription, OneShotChargeInstance chargeInstance,
            Integer quantity, Date applicationDate, User creator) throws BusinessException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        if (applicationDate == null) {
            applicationDate = new Date();
        }

        log.debug("ChargeApplicationService.oneShotChargeApplication subscriptionCode=#0,quantity=#1,"
                + "applicationDate=#2,chargeInstance.getId=#3", subscription.getId(), quantity, applicationDate,
                chargeInstance.getId());
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        if (chargeTemplate == null) {
            throw new IncorrectChargeTemplateException("chargeTemplate is null for chargeInstance id="
                    + chargeInstance.getId() + ", code=" + chargeInstance.getCode());
        }
        InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                    + chargeTemplate.getCode());
        }
        
        InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(chargeTemplate.getInvoiceSubCategory().getId(), subscription.getUserAccount().getBillingAccount().getCountryCode());
        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                    + invoiceSubCategory.getCode());
        }
        ChargeApplication chargeApplication = new ChargeApplication(chargeTemplate.getCode(), chargeInstance
                .getDescription(), subscription, chargeInstance, chargeTemplate.getCode(),
                ApplicationChgStatusEnum.WAITING, ApplicationTypeEnum.PUNCTUAL, applicationDate, chargeInstance
                        .getAmountWithoutTax(), chargeInstance.getAmount2(), quantity, tax.getCode(), tax.getPercent(),
                null, null, invoiceSubCategory, null, null, null, null, chargeInstance.getCriteria1(), chargeInstance
                        .getCriteria2(), chargeInstance.getCriteria3());

        create(chargeApplication, creator, chargeTemplate.getProvider());
        OneShotChargeTemplate oneShotChargeTemplate = null;
        if(chargeTemplate instanceof OneShotChargeTemplate){
        	 oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
             
        }else{
        	oneShotChargeTemplate= oneShotChargeTemplateService.findById(chargeTemplate.getId());
        }
       
        
        Boolean immediateInvoicing=oneShotChargeTemplate!=null?oneShotChargeTemplate.getImmediateInvoicing():false;
        if (immediateInvoicing!=null && immediateInvoicing) {
            BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
            int delay = billingAccount.getBillingCycle().getInvoiceDateDelay();
            Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(), -delay);
            nextInvoiceDate = DateUtils.parseDateWithPattern(nextInvoiceDate, "dd/MM/yyyy");
            applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");
            if (applicationDate.after(nextInvoiceDate)) {
                billingAccount.setNextInvoiceDate(applicationDate);
                billingAccountService.update(billingAccount, creator);
            }
        }

    }

    public void recurringChargeApplication(Subscription subscription, RecurringChargeInstance chargeInstance,
            Integer quantity, Date applicationDate, User creator) throws BusinessException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        log.debug("ChargeApplicationService.recurringChargeApplication subscriptionCode=#0,quantity=#1,"
                + "applicationDate=#2,chargeInstance.getId=#3", subscription.getId(), quantity, applicationDate,
                chargeInstance.getId());
        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        if (chargeTemplate == null) {
            throw new IncorrectChargeTemplateException("chargeTemplate is null for chargeInstance id="
                    + chargeInstance.getId() + ", code=" + chargeInstance.getCode());
        }
        InvoiceSubCategory invoiceSubCategory = chargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                    + chargeTemplate.getCode());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(chargeTemplate.getInvoiceSubCategory().getId(), subscription.getUserAccount().getBillingAccount().getCountryCode());
        Tax tax = invoiceSubcategoryCountry.getTax();

        ChargeApplication chargeApplication = new ChargeApplication(chargeTemplate.getCode(), chargeInstance
                .getDescription(), subscription, chargeInstance, chargeTemplate.getCode(),
                ApplicationChgStatusEnum.WAITING, ApplicationTypeEnum.PUNCTUAL, applicationDate, chargeInstance
                        .getAmountWithoutTax(), chargeInstance.getAmount2(), quantity, tax.getCode(), tax.getPercent(),
                null, null, invoiceSubCategory, null, null, null, null, chargeInstance.getCriteria1(), chargeInstance
                        .getCriteria2(), chargeInstance.getCriteria3());

        create(chargeApplication, creator, chargeTemplate.getProvider());
    }

    public void chargeSubscription(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        log.debug("ChargeApplicationService.chargeSubscription subscriptionCode=#0,chargeCode=#1,quantity=#2,"
                + "applicationDate=#3,chargeInstance.getId=#4", chargeInstance.getServiceInstance().getSubscription()
                .getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(), chargeInstance
                .getSubscriptionDate(), chargeInstance.getId());

        Date applicationDate = chargeInstance.getSubscriptionDate();
        applicationDate = DateUtils.parseDateWithPattern(chargeInstance.getSubscriptionDate(), "dd/MM/yyyy");

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        if (recurringChargeTemplate.getCalendar() == null) {
            throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code="
                    + recurringChargeTemplate.getCode());
        }

        ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
        Calendar durationTermCalendar = null;
        Date nextDurationDate = null;
        try {
            durationTermCalendar = serviceTemplate.getDurationTermCalendar();
            nextDurationDate = durationTermCalendar != null ? durationTermCalendar.nextCalendarDate(applicationDate)
                    : null;
            log.debug("nextDurationDate=" + nextDurationDate);
        } catch (Exception e) {
            log.info("Cannot find duration term calendar for serviceTemplate.id=#0", serviceTemplate.getId());
        }
        Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
        nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
        chargeInstance.setChargeDate(applicationDate);
        if (recurringChargeTemplate.getApplyInAdvance()) {

        Date previousapplicationDate = recurringChargeTemplate.getCalendar().previousCalendarDate(applicationDate);
        previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
            log.debug("chargeSubscription applicationDate=#0, nextapplicationDate=#1,previousapplicationDate=#2",
                    applicationDate, nextapplicationDate, previousapplicationDate);

        String param1 = "0";
        String param2 = null;// used in invoice description
        String param3 = "0";
        if (Boolean.TRUE.equals(recurringChargeTemplate.getSubscriptionProrata())) {
            param3 = "1";// for prorata
        }

        Date periodStart = applicationDate;
        if (!recurringChargeTemplate.getSubscriptionProrata()) {
            param1 = "0";
            param3 = "1";
        } else {
            double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
            double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);
            if (part2 > 0) {
                param1 = Double.toString(part1 / part2);
            } else {
                log.error("Error in calendar dates : nextapplicationDate=#0, previousapplicationDate=#1",
                        nextapplicationDate, previousapplicationDate);
            }
            
            
            
                log.debug("chargeSubscription part1=#0, part2=#1, param1=#2", part1, part2, param1);
        }

        param2 = " " + sdf.format(applicationDate) + " au "
                + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
        log.debug("param2=#0", param2);

        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                    + recurringChargeTemplate.getCode());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCountryCode());
        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                    + invoiceSubCategory.getCode());
        }
        if (!recurringChargeTemplate.getApplyInAdvance()) {
            applicationDate = nextapplicationDate;
        }
        ChargeApplication chargeApplication = new ChargeApplication(chargeInstance.getCode(), chargeInstance
                .getDescription(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
                    chargeInstance.getCode(), ApplicationChgStatusEnum.WAITING,
                    ApplicationTypeEnum.PRORATA_SUBSCRIPTION, applicationDate, chargeInstance.getAmountWithoutTax(),
                    chargeInstance.getAmount2(), chargeInstance.getServiceInstance().getQuantity(), tax.getCode(), tax
                            .getPercent(), null, nextapplicationDate, recurringChargeTemplate.getInvoiceSubCategory(),
                    param1, param2, param3, null, chargeInstance.getCriteria1(), chargeInstance.getCriteria2(),
                    chargeInstance.getCriteria3());
            // one customer want the charge subrscription date to be the date the charge
            // was
        // activated
        chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

        create(chargeApplication, creator, chargeInstance.getProvider());

        chargeInstance.setNextChargeDate(nextapplicationDate);

            // If there is a durationTermCalendar then we apply all
            // necessary
            // missing periods

            if (nextDurationDate != null && nextDurationDate.getTime() > nextapplicationDate.getTime()) {
                applyReccuringCharge(chargeInstance, false, recurringChargeTemplate, creator);
            }

        } else {

            if (nextDurationDate != null && nextDurationDate.getTime() > nextapplicationDate.getTime()) {
                chargeInstance.setNextChargeDate(nextDurationDate);
            } else {
                chargeInstance.setNextChargeDate(nextapplicationDate);
            }
        }

    }

    public void applyReimbursment(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {
        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        log.debug("applyReimbursment subscriptionCode=#0,chargeCode=#1,quantity=#2,"
                + "applicationDate=#3,chargeInstance.getId=#4,NextChargeDate=#5", chargeInstance.getServiceInstance()
                .getSubscription().getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance()
                .getQuantity(), chargeInstance.getSubscriptionDate(), chargeInstance.getId(), chargeInstance
                .getNextChargeDate());

        Date applicationDate = chargeInstance.getTerminationDate();
        applicationDate = DateUtils.addDaysToDate(applicationDate, 1);
        applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

        String param1 = "1";// for prorata
        String param2 = null;// used in invoice description
        String param3 = "0";

        Date nextapplicationDate = null;

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        if (recurringChargeTemplate.getCalendar() == null) {
            throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code="
                    + recurringChargeTemplate.getCode());
        }

        if (Boolean.TRUE.equals(recurringChargeTemplate.getTerminationProrata())) {
            param3 = "1";// for prorata
        }
        nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
        nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
        Date previousapplicationDate = recurringChargeTemplate.getCalendar().previousCalendarDate(applicationDate);
        previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
        log.debug("applicationDate=#0, nextapplicationDate=#1,previousapplicationDate=#2", applicationDate,
                nextapplicationDate, previousapplicationDate);

        Date periodStart = applicationDate;
        if (recurringChargeTemplate.getTerminationProrata()) {

        	double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
        	double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate); 
             
            if (part2 > 0) {
                param1 = Double.toString((-1) * part1 / part2);
            } else {
                log.error("Error in calendar dates : nextapplicationDate=#0, previousapplicationDate=#1",
                        nextapplicationDate, previousapplicationDate);
            }
            param2 = " " + str_tooPerceived + " " + sdf.format(periodStart) + " / "
                    + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
            log.debug("part1=#0, part2=#1, param1=#2, param2=#3", part1, part2, param1, param2);

            InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
            if (invoiceSubCategory == null) {
                throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                        + recurringChargeTemplate.getCode());
            }

            InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCountryCode());
            Tax tax = invoiceSubcategoryCountry.getTax();
            if (tax == null) {
                throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                        + invoiceSubCategory.getCode());
            }

            ChargeApplication chargeApplication = new ChargeApplication(chargeInstance.getCode(), chargeInstance
                    .getDescription(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
                    chargeInstance.getCode(), ApplicationChgStatusEnum.WAITING,
                    ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate, chargeInstance.getAmountWithoutTax(),
                    chargeInstance.getAmount2(), chargeInstance.getServiceInstance().getQuantity(), tax.getCode(), tax
                            .getPercent(), null, nextapplicationDate, invoiceSubCategory, param1, param2, param3, null,
                    chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3());
            chargeApplication.setApplicationMode(ChargeApplicationModeEnum.REIMBURSMENT);
            create(chargeApplication, creator, chargeInstance.getProvider());

        }

        if (recurringChargeTemplate.getApplyInAdvance()) {
            Date nextChargeDate = chargeInstance.getNextChargeDate();
            log.debug("reimbursment-applyInAdvance applicationDate=#0, nextapplicationDate=#1,nextChargeDate=#2",
                    applicationDate, nextapplicationDate, nextChargeDate);
            if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
                applyReccuringCharge(chargeInstance, true, recurringChargeTemplate, creator);
            }
        } else {
            Date nextChargeDate = chargeInstance.getChargeDate();
            log.debug("reimbursment-applyInAdvance applicationDate=#0, nextapplicationDate=#1,nextChargeDate=#2",
                    applicationDate, nextapplicationDate, nextChargeDate);
            if (nextChargeDate != null && nextChargeDate.getTime() > nextapplicationDate.getTime()) {
                applyNotAppliedinAdvanceReccuringCharge(chargeInstance, true, recurringChargeTemplate, creator);
            }
        }
    }

    public void applyReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
            RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

        // we apply the charge at its nextChargeDate

        Date applicationDate = chargeInstance.getNextChargeDate();

        if (reimbursement) {
            applicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
                    chargeInstance.getTerminationDate());
        }

        if (applicationDate == null) {
            throw new IncorrectChargeInstanceException("nextChargeDate is null.");
        }

        // first we get the serviceInstance and check if there is an associated
        // Calendar
        ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
        Calendar durationTermCalendar = null;
        Date nextDurationDate = null;
        try {
            durationTermCalendar = serviceTemplate.getDurationTermCalendar();
            nextDurationDate = reimbursement ? chargeInstance.getNextChargeDate() : durationTermCalendar
                    .nextCalendarDate(applicationDate);
            log.debug("reimbursement=#0,nextDurationDate=#1,applicationDate=#2", reimbursement, nextDurationDate,
                    applicationDate);
        } catch (Exception e) {
            log.error("Cannot find duration term calendar for serviceTemplate.id=#0", serviceTemplate.getId());
        }
        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                    + recurringChargeTemplate.getCode());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCountryCode());
        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                    + invoiceSubCategory.getCode());
        }
        while (applicationDate.getTime() < nextDurationDate.getTime()) {
            Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
            log.debug("next step for #0, applicationDate=#1, nextApplicationDate=#2,nextApplicationDate=#3",
                    chargeInstance.getId(), applicationDate, nextapplicationDate, nextDurationDate);

            String param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate)
                    + (reimbursement ? " / " : " au ") + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
            log.debug("applyReccuringCharge : nextapplicationDate=#0, param2=#1", nextapplicationDate, param2);

            ChargeApplication chargeApplication = new ChargeApplication(chargeInstance.getCode(), chargeInstance
                    .getDescription(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
                    chargeInstance.getCode(), ApplicationChgStatusEnum.WAITING,
                    reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : ApplicationTypeEnum.RECURRENT,
                    applicationDate, chargeInstance.getAmountWithoutTax(), chargeInstance.getAmount2(), chargeInstance
                            .getServiceInstance().getQuantity(), tax.getCode(), tax.getPercent(), null,
                    nextapplicationDate, invoiceSubCategory, reimbursement ? "-1" : null, param2, null, null,
                    chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3());
            chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());
            if (reimbursement) {
                chargeApplication.setApplicationMode(ChargeApplicationModeEnum.REIMBURSMENT);
            } else {
                chargeApplication.setApplicationMode(ChargeApplicationModeEnum.SUBSCRIPTION);
            }

            create(chargeApplication, creator, chargeInstance.getProvider());
            chargeInstance.setChargeDate(applicationDate);
            applicationDate = nextapplicationDate;
        }
        chargeInstance.setNextChargeDate(nextDurationDate);
    }

    public void applyNotAppliedinAdvanceReccuringCharge(RecurringChargeInstance chargeInstance, boolean reimbursement,
            RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

        Date applicationDate = chargeInstance.getChargeDate();

        if (reimbursement) {
            applicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(
                    chargeInstance.getTerminationDate());
        }

        if (applicationDate == null) {
            throw new IncorrectChargeInstanceException("ChargeDate is null.");
        }

        // first we get the serviceInstance and check if there is an associated
        // Calendar
        ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
        Calendar durationTermCalendar = null;
        Date nextChargeDate = reimbursement ? chargeInstance.getChargeDate() : chargeInstance.getNextChargeDate();
        try {
            durationTermCalendar = serviceTemplate.getDurationTermCalendar();
            log.debug(" applyNotAppliedinAdvanceReccuringCharge nextChargeDate=#1,applicationDate=#2", nextChargeDate,
                    applicationDate);
        } catch (Exception e) {
            log
                    .error(
                            " applyNotAppliedinAdvanceReccuringCharge Cannot find duration term calendar for serviceTemplate.id=#0",
                            serviceTemplate.getId());
        }
        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                    + recurringChargeTemplate.getCode());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCountryCode());
        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                    + invoiceSubCategory.getCode());
        }

        while (applicationDate.getTime() < nextChargeDate.getTime()) {
            Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
            log
                    .debug(
                            "applyNotAppliedinAdvanceReccuringCharge next step for #0, applicationDate=#1, nextApplicationDate=#2,nextApplicationDate=#3",
                            chargeInstance.getId(), applicationDate, nextapplicationDate, nextChargeDate);

            Date previousapplicationDate = recurringChargeTemplate.getCalendar().previousCalendarDate(applicationDate);
            previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
            log
                    .debug(
                            " applyNotAppliedinAdvanceReccuringCharge applicationDate=#0, nextapplicationDate=#1,previousapplicationDate=#2",
                            applicationDate, nextapplicationDate, previousapplicationDate);

            String param1 = "0";
            String param2 = null;// used in invoice description
            String param3 = "0";
            if (Boolean.TRUE.equals(recurringChargeTemplate.getSubscriptionProrata())) {
                param3 = "1";// for prorata
            }
            ApplicationTypeEnum applicationTypeEnum = ApplicationTypeEnum.RECURRENT;
            Date periodStart = applicationDate;
            // n'appliquer le prorata que dans le cas de la 1ere application de
            // charges �chues
            log.debug(" applyNotAppliedinAdvanceReccuringCharge chargeInstance.getChargeApplications().size()=#0",
                    chargeInstance.getChargeApplications().size());
            if (chargeInstance.getChargeApplications().size() > 0 || !recurringChargeTemplate.getSubscriptionProrata()) {
                param1 = "0";
                param3 = null;
            } else {

                applicationTypeEnum = ApplicationTypeEnum.PRORATA_SUBSCRIPTION;
                double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
                double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);
                
                if (part2 > 0) {
                    param1 = Double.toString(part1 / part2);
                } else {
                    log
                            .error(
                                    "applyNotAppliedinAdvanceReccuringCharge Error in calendar dates : nextapplicationDate=#0, previousapplicationDate=#1",
                                    nextapplicationDate, previousapplicationDate);
                }
                log.debug("part1=#0, part2=#1, param1=#2", part1, part2, param1);
            }

            param2 = (reimbursement ? str_tooPerceived + " " : " ") + sdf.format(applicationDate)
                    + (reimbursement ? " / " : " au ") + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));

            log.debug("param2=#0", param2);

            log.debug("applyNotAppliedinAdvanceReccuringCharge : nextapplicationDate=#0, param2=#1",
                    nextapplicationDate, param2);

            ChargeApplication chargeApplication = new ChargeApplication(chargeInstance.getCode(), chargeInstance
                    .getDescription(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
                    chargeInstance.getCode(), ApplicationChgStatusEnum.WAITING,
                    reimbursement ? ApplicationTypeEnum.PRORATA_TERMINATION : applicationTypeEnum, applicationDate,
                    chargeInstance.getAmountWithoutTax(), chargeInstance.getAmount2(), chargeInstance
                            .getServiceInstance().getQuantity(), tax.getCode(), tax.getPercent(), null,
                    nextapplicationDate, invoiceSubCategory, reimbursement ? "-1" : param1, param2, param3, null,
                    chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3());
            chargeApplication.setSubscriptionDate(chargeInstance.getServiceInstance().getSubscriptionDate());

            if (reimbursement) {
                chargeApplication.setApplicationMode(ChargeApplicationModeEnum.REIMBURSMENT);
            } else {
                chargeApplication.setApplicationMode(ChargeApplicationModeEnum.SUBSCRIPTION);
            }

            create(chargeApplication, creator, chargeInstance.getProvider());
            em.flush();
            em.refresh(chargeInstance);
            chargeInstance.setChargeDate(applicationDate);
            applicationDate = nextapplicationDate;
        }

        if (durationTermCalendar != null) {
            Date nextNextDurationDate = durationTermCalendar.nextCalendarDate(applicationDate);
            chargeInstance.setNextChargeDate(durationTermCalendar != null ? nextNextDurationDate : applicationDate);
            chargeInstance.setChargeDate(nextChargeDate);
        } else {
            Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
            chargeInstance.setNextChargeDate(nextapplicationDate);
            chargeInstance.setChargeDate(applicationDate);
        }

    }

    public void applyChargeAgreement(RecurringChargeInstance chargeInstance,
            RecurringChargeTemplate recurringChargeTemplate, User creator) throws BusinessException {

        // we apply the charge at its nextChargeDate
        Date applicationDate = chargeInstance.getNextChargeDate();
        if (applicationDate == null) {
            throw new IncorrectChargeInstanceException("nextChargeDate is null.");
        }

        Date endAgreementDate = chargeInstance.getServiceInstance().getEndAgrementDate();

        if (endAgreementDate == null) {
            return;
        }

        InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
        if (invoiceSubCategory == null) {
            throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                    + recurringChargeTemplate.getCode());
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCountryCode());
        Tax tax = invoiceSubcategoryCountry.getTax();
        if (tax == null) {
            throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                    + invoiceSubCategory.getCode());
        }
        while (applicationDate.getTime() < endAgreementDate.getTime()) {
            Date nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
            log.debug("agreement next step for #0, applicationDate=#1, nextApplicationDate=#2", recurringChargeTemplate
                    .getCode(), applicationDate, nextapplicationDate);
            String param1 = null;
            ApplicationTypeEnum type = ApplicationTypeEnum.RECURRENT;
            Date endDate = DateUtils.addDaysToDate(nextapplicationDate, -1);
            if (nextapplicationDate.getTime() > endAgreementDate.getTime()
                    && applicationDate.getTime() < endAgreementDate.getTime()) {
                Date endAgreementDateModified = DateUtils.addDaysToDate(endAgreementDate, 1);

                double part1 = endAgreementDateModified.getTime() - applicationDate.getTime();
                double part2 = nextapplicationDate.getTime() - applicationDate.getTime();
                if (part2 > 0) {
                    param1 = Double.toString(part1 / part2);
                }

                nextapplicationDate = endAgreementDate;
                endDate = nextapplicationDate;
                if (recurringChargeTemplate.getTerminationProrata()) {
                    type = ApplicationTypeEnum.PRORATA_TERMINATION;
                }
            }
            String param2 = sdf.format(applicationDate) + " au " + sdf.format(endDate);
            log.debug("applyReccuringCharge : nextapplicationDate=#0, param2=#1", nextapplicationDate, param2);

            ChargeApplication chargeApplication = new ChargeApplication(chargeInstance.getCode(), chargeInstance
                    .getDescription(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
                    chargeInstance.getCode(), ApplicationChgStatusEnum.WAITING, type, applicationDate, chargeInstance
                            .getAmountWithoutTax(), chargeInstance.getAmount2(), chargeInstance.getServiceInstance()
                            .getQuantity(), tax.getCode(), tax.getPercent(), null, nextapplicationDate,
                    invoiceSubCategory, param1, param2, null, null, chargeInstance.getCriteria1(), chargeInstance
                            .getCriteria2(), chargeInstance.getCriteria3());
            chargeApplication.setApplicationMode(ChargeApplicationModeEnum.AGREEMENT);
            create(chargeApplication, creator, chargeInstance.getProvider());
            chargeInstance.setChargeDate(applicationDate);
            applicationDate = nextapplicationDate;
        }
    }

    @SuppressWarnings("unchecked")
    public void cancelChargeApplications(Long chargeInstanceId, ChargeApplicationModeEnum mode, User updater)
            throws BusinessException {
        log.info("cancelChargeApplications chargeInstanceId=#0 ,mode=#1", chargeInstanceId, mode);
        List<ChargeApplication> chargeApplications = em.createQuery(
                "from ChargeApplication where chargeInstance.id=:id and applicationMode=:mode").setParameter("id",
                chargeInstanceId).setParameter("mode", mode).getResultList();
        for (ChargeApplication chargeApplication : chargeApplications) {
            cancelChgApplicationsAndTransactions(chargeApplication, updater);
        }

    }

    public void cancelOneShotChargeApplications(OneShotChargeInstance chargeInstance,
            OneShotChargeTemplateTypeEnum templateTypeEnum, User updater) throws BusinessException {
        log.info("cancelOneShotChargeApplications chargeInstanceId=#0 ,templateTypeEnum=#1", chargeInstance.getId(),
                templateTypeEnum);
        OneShotChargeTemplate oneShotChargeTemplate = null;
        if(chargeInstance.getChargeTemplate() instanceof OneShotChargeTemplate){
        	 oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
             
        }else{
        	oneShotChargeTemplate= oneShotChargeTemplateService.findById(chargeInstance.getChargeTemplate().getId());
        }
        if (oneShotChargeTemplate!=null && oneShotChargeTemplate.getOneShotChargeTemplateType() == templateTypeEnum) {
            for (ChargeApplication chargeApplication : chargeInstance.getChargeApplications()) {
                cancelChgApplicationsAndTransactions(chargeApplication, updater);
                update(chargeApplication, updater);
            }
        }

    }

    public void cancelChgApplicationsAndTransactions(ChargeApplication chargeApplication, User updater)
            throws BusinessException {

        if (chargeApplication.getStatus() != ApplicationChgStatusEnum.TREATED) {
            chargeApplication.setStatus(ApplicationChgStatusEnum.CANCELED);
        }
        for (RatedTransaction ratedTransaction : chargeApplication.getRatedTransactions()) {
            if (ratedTransaction.getBillingRun() == null
                    || (ratedTransaction.getBillingRun() != null && ratedTransaction.getBillingRun().getStatus() == BillingRunStatusEnum.CANCELED)) {
                ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
                chargeApplication.setStatus(ApplicationChgStatusEnum.CANCELED);
                update(chargeApplication, updater);
            }
        }

    }

    @Deprecated
    public void chargeTermination(RecurringChargeInstance chargeInstance, User creator) throws BusinessException {
        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        log.debug("ChargeApplicationService.chargeTermination subscriptionCode=#0,chargeCode=#1,quantity=#2,"
                + "applicationDate=#3,chargeInstance.getId=#4", chargeInstance.getServiceInstance().getSubscription()
                .getCode(), chargeInstance.getCode(), chargeInstance.getServiceInstance().getQuantity(), chargeInstance
                .getSubscriptionDate(), chargeInstance.getId());

        Date applicationDate = chargeInstance.getTerminationDate();
        applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

        String param1 = "1";// for prorata
        String param2 = null;// used in invoice description
        String param3 = "0";

        Date nextapplicationDate = null;

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        if (recurringChargeTemplate.getCalendar() == null) {
            throw new IncorrectChargeTemplateException("Recurring charge template has no calendar: code="
                    + recurringChargeTemplate.getCode());
        }
        Date endAgrementDate = chargeInstance.getServiceInstance().getEndAgrementDate();
        if (endAgrementDate != null && chargeInstance.getTerminationDate().before(endAgrementDate)) {
            applyChargeAgreement(chargeInstance, recurringChargeTemplate, creator);
            return;
        }

        if (Boolean.TRUE.equals(recurringChargeTemplate.getTerminationProrata())) {
            param3 = "1";// for prorata
        }
        nextapplicationDate = recurringChargeTemplate.getCalendar().nextCalendarDate(applicationDate);
        nextapplicationDate = DateUtils.parseDateWithPattern(nextapplicationDate, "dd/MM/yyyy");
        Date previousapplicationDate = recurringChargeTemplate.getCalendar().previousCalendarDate(applicationDate);
        previousapplicationDate = DateUtils.parseDateWithPattern(previousapplicationDate, "dd/MM/yyyy");
        log.debug("applicationDate=#0, nextapplicationDate=#1,previousapplicationDate=#2", applicationDate,
                nextapplicationDate, previousapplicationDate);

        Date periodStart = applicationDate;
        if (recurringChargeTemplate.getTerminationProrata()) {
        	double part1 = DateUtils.daysBetween(periodStart, nextapplicationDate);
        	double part2 = DateUtils.daysBetween(previousapplicationDate, nextapplicationDate);
            if (part2 > 0) {
                param1 = Double.toString((-1) * part1 / part2);
            } else {
                log.error("Error in calendar dates : nextapplicationDate=#0, previousapplicationDate=#1",
                        nextapplicationDate, previousapplicationDate);
            }
            param2 = " " + str_tooPerceived + " " + sdf.format(periodStart) + " / "
                    + sdf.format(DateUtils.addDaysToDate(nextapplicationDate, -1));
            log.debug("part1=#0, part2=#1, param1=#2, param2=#3", part1, part2, param1, param2);

            InvoiceSubCategory invoiceSubCategory = recurringChargeTemplate.getInvoiceSubCategory();
            if (invoiceSubCategory == null) {
                throw new IncorrectChargeTemplateException("invoiceSubCategory is null for chargeTemplate code="
                        + recurringChargeTemplate.getCode());
            }

            InvoiceSubcategoryCountry invoiceSubcategoryCountry= invoiceSubCategoryCountryService.findInvoiceSubCategoryCountry(invoiceSubCategory.getId(), chargeInstance.getSubscription().getUserAccount().getBillingAccount().getCountryCode());
            Tax tax = invoiceSubcategoryCountry.getTax();
            if (tax == null) {
                throw new IncorrectChargeTemplateException("tax is null for invoiceSubCategory code="
                        + invoiceSubCategory.getCode());
            }

            ChargeApplication chargeApplication = new ChargeApplication(chargeInstance.getCode(), chargeInstance
                    .getDescription(), chargeInstance.getServiceInstance().getSubscription(), chargeInstance,
                    chargeInstance.getCode(), ApplicationChgStatusEnum.WAITING,
                    ApplicationTypeEnum.PRORATA_TERMINATION, applicationDate, chargeInstance.getAmountWithoutTax(),
                    chargeInstance.getAmount2(), chargeInstance.getServiceInstance().getQuantity(), tax.getCode(), tax
                            .getPercent(), null, nextapplicationDate, invoiceSubCategory, param1, param2, param3, null,
                    chargeInstance.getCriteria1(), chargeInstance.getCriteria2(), chargeInstance.getCriteria3());
            create(chargeApplication, creator, chargeInstance.getProvider());

        }

        chargeInstance.setChargeDate(applicationDate);
        chargeInstance.setNextChargeDate(nextapplicationDate);

        if (recurringChargeTemplate.getApplyInAdvance()) {
            // If there is a durationTermCalendar then we reimburse all
            // necessary
            // missing periods
            ServiceTemplate serviceTemplate = chargeInstance.getServiceInstance().getServiceTemplate();
            Calendar durationTermCalendar = null;
            Date nextDurationDate = null;
            try {
                durationTermCalendar = serviceTemplate.getDurationTermCalendar();
                nextDurationDate = durationTermCalendar.nextCalendarDate(applicationDate);
                log.debug("nextDurationDate=" + nextDurationDate);
            } catch (Exception e) {
                log.error("Cannot find duration term calendar for serviceTemplate.id=#0", serviceTemplate.getId());
            }

            if (nextDurationDate != null && nextDurationDate.getTime() > nextapplicationDate.getTime()) {
                applyReccuringCharge(chargeInstance, true, recurringChargeTemplate, creator);
            }
        }
    }

}
