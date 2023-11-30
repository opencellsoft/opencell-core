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

package org.meveo.api.billing;

import static java.util.Optional.ofNullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.account.AccountHierarchyApi;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.billing.BillingRunDto;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunAutomaticActionEnum;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunReportService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.util.MeveoParamBean;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class InvoicingApi extends BaseApi {

    @Inject
    BillingRunService billingRunService;

    @Inject
    InvoiceService invoiceService;

    @Inject
    BillingCycleService billingCycleService;

    @Inject
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    private BillingRunReportService billingRunReportService;

    @Inject
    @MeveoParamBean
    private ParamBean paramBean;

    public long createBillingRun(CreateBillingRunDto dto) throws MeveoApiException, BusinessApiException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        String allowManyInvoicing = paramBean.getProperty("billingRun.allowManyInvoicing", "true");
        boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);

        if (billingRunService.isActiveBillingRunsExist() && !isAllowed) {
            throw new BusinessApiException("error.invoicing.alreadyLunched");
        }

        if (StringUtils.isBlank(dto.getBillingCycleCode())) {
            missingParameters.add("billingCycleCode");
        }
        if (dto.getBillingRunTypeEnum() == null) {
            missingParameters.add("billingRunType");
        }

        handleMissingParameters();

        BillingCycle billingCycle = billingCycleService.findByCode(dto.getBillingCycleCode());
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, dto.getBillingCycleCode());
        }
        BillingRun billingRun = new BillingRun();
        billingRun.setBillingCycle(billingCycle);
        billingRun.setAggregateUnitAmounts(billingCycle.isAggregateUnitAmounts());
        billingRun.setDateAggregation(billingCycle.getDateAggregation());
        billingRun.setUseAccountingArticleLabel(billingCycle.isUseAccountingArticleLabel());
        billingRun.setIgnoreOrders(billingCycle.isIgnoreOrders());
        billingRun.setIgnoreSubscriptions(billingCycle.isIgnoreSubscriptions());
        billingRun.setIgnoreUserAccounts(billingCycle.isIgnoreUserAccounts());
        billingRun.setProcessType(dto.getBillingRunTypeEnum());
        billingRun.setStartDate(dto.getStartDate());
        billingRun.setEndDate(dto.getEndDate());
        billingRun.setProcessDate(new Date());
        billingRun.setReferenceDate(dto.getReferenceDate());
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setInvoiceDate(dto.getInvoiceDate());
        billingRun.setLastTransactionDate(dto.getLastTransactionDate());
        billingRun.setSkipValidationScript(dto.getSkipValidationScript());
        billingRun.setPreReportAutoOnCreate(ofNullable(dto.getPreReportAutoOnCreate())
                .orElse(billingRun.getBillingCycle().getReportConfigPreReportAutoOnCreate()));
        billingRun.setPreReportAutoOnInvoiceLinesJob(ofNullable(dto.getPreReportAutoOnInvoiceLinesJob())
                .orElse(billingRun.getBillingCycle().getReportConfigPreReportAutoOnInvoiceLinesJob()));
        billingRun.setApplicationEl(dto.getApplicationEl());
        if (dto.getApplicationEl() != null) { // PO expected that the value shall be different from null, if it is empty, then we store the empty value (see Business rules INTRD-17689)
            billingRun.setApplicationEl(dto.getApplicationEl());
        } else {
            billingRun.setApplicationEl(billingCycle.getApplicationEl());
        }
        if(dto.getRejectAutoAction() == null) {
            billingRun.setRejectAutoAction(BillingRunAutomaticActionEnum.MANUAL_ACTION);
        }
        else {
            billingRun.setRejectAutoAction(dto.getRejectAutoAction());
        }        
        if(dto.getSuspectAutoAction() == null) {
            billingRun.setSuspectAutoAction(BillingRunAutomaticActionEnum.AUTOMATIC_VALIDATION);
        }
        else {
            billingRun.setSuspectAutoAction(dto.getSuspectAutoAction());
        }
        if (dto.getInvoiceDate() == null) {
            if (billingCycle.getInvoiceDateProductionDelayEL() != null) {
                billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), InvoiceService.resolveInvoiceDateDelay(billingCycle.getInvoiceDateProductionDelayEL(), billingRun)));
            } else {
                billingRun.setInvoiceDate(billingRun.getProcessDate());
            }
        }
        if (dto.getLastTransactionDate() == null) {
            if (billingCycle.getLastTransactionDateEL() != null) {
                billingRun.setLastTransactionDate(BillingRunService.resolveLastTransactionDate(billingCycle.getLastTransactionDateEL(), billingRun));
            } else if (billingCycle.getLastTransactionDateDelayEL() != null) {
                billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), BillingRunService.resolveLastTransactionDateDelay(billingCycle.getLastTransactionDateDelayEL(), billingRun)));
            } else {
                billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), 1));
            }
        }
        billingRun.setCollectionDate(dto.getCollectionDate());
        if (dto.isComputeDatesAtValidation() != null) {
            billingRun.setComputeDatesAtValidation(dto.isComputeDatesAtValidation());
        }
        
        if(dto.getDescriptionsTranslated() != null && !dto.getDescriptionsTranslated().isEmpty()){
        	billingRun.setDescriptionI18n(convertMultiLanguageToMapOfValues(dto.getDescriptionsTranslated() ,null));
        }
        billingRun.setGenerateAO(ofNullable(dto.getGenerateAO()).orElse(false));

        // If value is not provided in the API call, then use value of billingCycle as default value

        if (dto.getDisableAggregation() != null) {
            billingRun.setDisableAggregation(dto.getDisableAggregation());
        } else {
            billingRun.setDisableAggregation(billingCycle.isDisableAggregation());
        }

        if (dto.getUseAccountingArticleLabel() != null) {
            billingRun.setUseAccountingArticleLabel(dto.getUseAccountingArticleLabel());
        } else {
            billingRun.setUseAccountingArticleLabel(billingCycle.isUseAccountingArticleLabel());
        }

        if (dto.getIncrementalInvoiceLines() != null) {
            billingRun.setIncrementalInvoiceLines(dto.getIncrementalInvoiceLines());
        } else {
            billingRun.setIncrementalInvoiceLines(billingCycle.isIncrementalInvoiceLines());
        }
        
        
        if (dto.getIncrementalInvoiceLines() != null) {
            billingRun.setIncrementalInvoiceLines(dto.getIncrementalInvoiceLines());
        } else {
            billingRun.setIncrementalInvoiceLines(billingCycle.isIncrementalInvoiceLines());
        }

        if (dto.getAggregateUnitAmounts() != null) {
            billingRun.setAggregateUnitAmounts(dto.getAggregateUnitAmounts());
        } else {
            billingRun.setAggregateUnitAmounts(billingCycle.isAggregateUnitAmounts());
        }

        if (dto.getDateAggregation() != null) {
            billingRun.setDateAggregation(dto.getDateAggregation());
        } else {
            billingRun.setDateAggregation(billingCycle.getDateAggregation());
        }

        if (dto.getDiscountAggregation() != null) {
            billingRun.setDiscountAggregation(dto.getDiscountAggregation());
        } else {
            billingRun.setDiscountAggregation(billingCycle.getDiscountAggregation());
        }

        if (dto.getIgnoreOrders() != null) {
            billingRun.setIgnoreOrders(dto.getIgnoreOrders());
        } else {
            billingRun.setIgnoreOrders(billingCycle.isIgnoreOrders());
        }

        if (dto.getIgnoreSubscriptions() != null) {
            billingRun.setIgnoreSubscriptions(dto.getIgnoreSubscriptions());
        } else {
            billingRun.setIgnoreSubscriptions(billingCycle.isIgnoreSubscriptions());
        }

        billingRunService.create(billingRun);

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), billingRun, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        if(dto.getDescriptionsTranslated() == null || dto.getDescriptionsTranslated().isEmpty()){
        	LanguageDescriptionDto languageDescriptionEn = new LanguageDescriptionDto("ENG", "Billing run (id="+billingRun.getId()+"; billing cycle="+billingCycle.getDescription()+"; invoice date="+(billingRun.getInvoiceDate()!=null ? new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(billingRun.getInvoiceDate()) : "")+")"); 
        	LanguageDescriptionDto languageDescriptionFr = new LanguageDescriptionDto("FRA", "Run de facturation (id="+billingRun.getId()+"; billing cycle="+billingCycle.getDescription()+"; invoice date="+(billingRun.getInvoiceDate()!=null ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(billingRun.getInvoiceDate()) : "")+")"); 
        	
        	List<LanguageDescriptionDto> descriptionsTranslated = new ArrayList<LanguageDescriptionDto>();
        	descriptionsTranslated.add(languageDescriptionEn);
        	descriptionsTranslated.add(languageDescriptionFr);

        	billingRun.setDescriptionI18n(convertMultiLanguageToMapOfValues(descriptionsTranslated ,null));
        }

        billingRunService.update(billingRun);
        billingRunReportService.generateBillingRunReport(billingRun);
        
        return billingRun.getId();
    }

    public long updateBillingRun(CreateBillingRunDto dto) throws MeveoApiException, BusinessApiException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        BillingRun billingRun = billingRunService.findById(dto.getId());
        if (billingRun == null) {
            throw new BadRequestException("Billing run Entity with id "+dto.getId()+" not found");
        }
        
        if (billingRun.getStatus() != BillingRunStatusEnum.NEW) {
        	throw new BusinessApiException("You can not update the billing run with status = " + billingRun.getStatus());
        }

        BillingCycle billingCycle = billingRun.getBillingCycle();
        if (dto.getBillingCycleCode() != null) {
            billingCycle = billingCycleService.findByCode(dto.getBillingCycleCode());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, dto.getBillingCycleCode());
            }
            billingRun.setBillingCycle(billingCycle);
            billingRun.setAggregateUnitAmounts(billingCycle.isAggregateUnitAmounts());
            billingRun.setDateAggregation(billingCycle.getDateAggregation());
            billingRun.setUseAccountingArticleLabel(billingCycle.isUseAccountingArticleLabel());
            billingRun.setIgnoreOrders(billingCycle.isIgnoreOrders());
            billingRun.setIgnoreSubscriptions(billingCycle.isIgnoreSubscriptions());
            billingRun.setIgnoreUserAccounts(billingCycle.isIgnoreUserAccounts());

            // If Billing cycle change, and no explicit values are provided, use values from the new Billing Cycle 
            if (dto.getIncrementalInvoiceLines() == null) {
                billingRun.setIncrementalInvoiceLines(billingCycle.isIncrementalInvoiceLines());
            }
            if (dto.getAggregateUnitAmounts() == null) {
                billingRun.setAggregateUnitAmounts(billingCycle.isAggregateUnitAmounts());
            }
            if (dto.getDateAggregation() == null) {
                billingRun.setDateAggregation(billingCycle.getDateAggregation());
        }
            if (dto.getDiscountAggregation() == null) {
                billingRun.setDiscountAggregation(billingCycle.getDiscountAggregation());
            }
            if (dto.getIgnoreOrders() == null) {
                billingRun.setIgnoreOrders(billingCycle.isIgnoreOrders());
            }
            if (dto.getIgnoreSubscriptions() == null) {
                billingRun.setIgnoreSubscriptions(billingCycle.isIgnoreSubscriptions());
            }
            if (dto.getDisableAggregation() == null) {
                billingRun.setDisableAggregation(billingCycle.isDisableAggregation());
            }
            if (dto.getUseAccountingArticleLabel() == null) {
                billingRun.setUseAccountingArticleLabel(billingCycle.isUseAccountingArticleLabel());
            }
        }

        if (dto.getBillingRunTypeEnum() != null) {
            billingRun.setProcessType(dto.getBillingRunTypeEnum());
        }
        if(dto.getStartDate() != null) {
            billingRun.setStartDate(dto.getStartDate());
        }
        if(dto.getEndDate() != null) {
            billingRun.setEndDate(dto.getEndDate());
        }
        if(dto.getReferenceDate() != null) {
            billingRun.setReferenceDate(dto.getReferenceDate());
        }
        if(dto.getInvoiceDate() != null) {
            billingRun.setInvoiceDate(dto.getInvoiceDate());
        }
        if(dto.getLastTransactionDate() != null) {
            billingRun.setLastTransactionDate(dto.getLastTransactionDate());
        }
        if(dto.getSkipValidationScript() != null) {
            billingRun.setSkipValidationScript(dto.getSkipValidationScript());
        }
        if(dto.getRejectAutoAction() != null) {
            billingRun.setRejectAutoAction(dto.getRejectAutoAction());
        }
        if(dto.getSuspectAutoAction() != null) {
            billingRun.setSuspectAutoAction(dto.getSuspectAutoAction());
        }

        if(dto.getCollectionDate() != null) {
            billingRun.setCollectionDate(dto.getCollectionDate());
        }
        
        if (dto.isComputeDatesAtValidation() != null) {
            billingRun.setComputeDatesAtValidation(dto.isComputeDatesAtValidation());
        }

        if (dto.getIncrementalInvoiceLines() != null) {
            billingRun.setIncrementalInvoiceLines(dto.getIncrementalInvoiceLines());
        }

        if (dto.getAggregateUnitAmounts() != null) {
            billingRun.setAggregateUnitAmounts(dto.getAggregateUnitAmounts());
        }

        if (dto.getDateAggregation() != null) {
            billingRun.setDateAggregation(dto.getDateAggregation());
        }

        if (dto.getDiscountAggregation() != null) {
            billingRun.setDiscountAggregation(dto.getDiscountAggregation());
        }

        if (dto.getIgnoreOrders() != null) {
            billingRun.setIgnoreOrders(dto.getIgnoreOrders());
        }

        if (dto.getIgnoreSubscriptions() != null) {
            billingRun.setIgnoreSubscriptions(dto.getIgnoreSubscriptions());
        }
        
        if (dto.getDisableAggregation() != null) {
            billingRun.setDisableAggregation(dto.getDisableAggregation());
        }

        if (dto.getUseAccountingArticleLabel() != null) {
            billingRun.setUseAccountingArticleLabel(dto.getUseAccountingArticleLabel());
        }

        // populate customFields
        if(dto.getCustomFields() != null) {
            try {
                populateCustomFields(dto.getCustomFields(), billingRun, true);
            } catch (MissingParameterException | InvalidParameterException e) {
                log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw e;
            }
        }

        if(dto.getDescriptionsTranslated() != null && !dto.getDescriptionsTranslated().isEmpty()){
        	billingRun.setDescriptionI18n(convertMultiLanguageToMapOfValues(dto.getDescriptionsTranslated() ,null));
        }else if (billingRun.getDescriptionI18n() == null){
        	LanguageDescriptionDto languageDescriptionEn = new LanguageDescriptionDto("ENG", "Billing run (id="+billingRun.getId()+"; billing cycle="+
													        (billingCycle != null ? billingCycle.getDescription() : " ")+
													        "; invoice date="+(billingRun.getInvoiceDate()!=null ? new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(billingRun.getInvoiceDate()) : "")+")"); 
        	LanguageDescriptionDto languageDescriptionFr = new LanguageDescriptionDto("FRA", "Run de facturation (id="+billingRun.getId()+"; billing cycle="+
													        (billingCycle != null ? billingCycle.getDescription() : " ")+
													        "; invoice date="+(billingRun.getInvoiceDate()!=null ? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(billingRun.getInvoiceDate()) : "")+")"); 
        	
        	List<LanguageDescriptionDto> descriptionsTranslated = new ArrayList<LanguageDescriptionDto>();
        	descriptionsTranslated.add(languageDescriptionEn);
        	descriptionsTranslated.add(languageDescriptionFr);

        	billingRun.setDescriptionI18n(convertMultiLanguageToMapOfValues(descriptionsTranslated ,null));
        }

        if(dto.getPreReportAutoOnCreate() != null) {
            billingRun.setPreReportAutoOnCreate(dto.getPreReportAutoOnCreate());
        } else {
            billingRun.setPreReportAutoOnCreate(billingRun.getBillingCycle().getReportConfigPreReportAutoOnCreate());
        }
        if(dto.getPreReportAutoOnInvoiceLinesJob() != null) {
            billingRun.setPreReportAutoOnInvoiceLinesJob(dto.getPreReportAutoOnInvoiceLinesJob());
        } else {
            billingRun.setPreReportAutoOnInvoiceLinesJob(billingRun
                    .getBillingCycle().getReportConfigPreReportAutoOnInvoiceLinesJob());
        }

        if (dto.getApplicationEl() != null) { // On update, if applicationEL is null then don’t update the field.
            billingRun.setApplicationEl(dto.getApplicationEl());
        }

        billingRunService.update(billingRun);
        billingRunReportService.generateBillingRunReport(billingRun);

        return billingRun.getId();
    }

    public BillingRunDto getBillingRunInfo(Long billingRunId) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
        BillingRun billingRunEntity = getBillingRun(billingRunId);

        BillingRunDto billingRunDtoResult = new BillingRunDto();
        billingRunDtoResult.setFromEntity(billingRunEntity);
        billingRunDtoResult.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(billingRunEntity));

        return billingRunDtoResult;
    }

    public BillingAccountsDto getBillingAccountListInRun(Long billingRunId) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
        BillingRun billingRunEntity = getBillingRun(billingRunId);
        BillingAccountsDto billingAccountsDtoResult = new BillingAccountsDto();
        List<BillingAccount> baEntities = billingRunEntity.getBillableBillingAccounts();
        if (baEntities != null && !baEntities.isEmpty()) {
            for (BillingAccount baEntity : baEntities) {
                billingAccountsDtoResult.getBillingAccount().add(accountHierarchyApi.billingAccountToDto(baEntity));
            }
        }
        return billingAccountsDtoResult;
    }

    private BillingRun getBillingRun(Long billingRunId) throws MissingParameterException, EntityDoesNotExistsException, BusinessApiException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }

        if (billingRunId == null || billingRunId.longValue() <= 0) {
            throw new BusinessApiException("The billingRunId should be a positive value");
        }

        BillingRun billingRunEntity = billingRunService.findById(billingRunId);
        if (billingRunEntity == null) {
            throw new EntityDoesNotExistsException(BillingRun.class, billingRunId);
        }
        return billingRunEntity;
    }

    public PreInvoicingReportsDTO getPreInvoicingReport(Long billingRunId) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException, BusinessException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
        BillingRun billingRun = getBillingRun(billingRunId);
        if (!BillingRunStatusEnum.PREINVOICED.equals(billingRun.getStatus())) {
            throw new BusinessApiException("BillingRun is not at the PREINVOICED status");
        }
        PreInvoicingReportsDTO preInvoicingReportsDTO = billingRunService.generatePreInvoicingReports(billingRun);
        return preInvoicingReportsDTO;
    }

    public PostInvoicingReportsDTO getPostInvoicingReport(Long billingRunId) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException, BusinessException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
        BillingRun billingRun = getBillingRun(billingRunId);
        if (!BillingRunStatusEnum.POSTINVOICED.equals(billingRun.getStatus())) {
            throw new BusinessApiException("BillingRun is not at the POSTINVOICED status");
        }
        PostInvoicingReportsDTO postInvoicingReportsDTO = billingRunService.generatePostInvoicingReports(billingRun);
        return postInvoicingReportsDTO;
    }

    public void validateBillingRun(Long billingRunId) throws MissingParameterException, BusinessException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
        billingRunService.forceValidate(billingRunId);

    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void cancelBillingRun(Long billingRunId) throws MissingParameterException, EntityDoesNotExistsException, BusinessApiException, BusinessException {

        BillingRun billingRun = getBillingRun(billingRunId);
        if (BillingRunStatusEnum.CANCELED.equals(billingRun.getStatus())) {
            throw new InvalidParameterException("Cannot cancel a Canceled billingRun ");

        } else if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus()) || BillingRunStatusEnum.VALIDATED.equals(billingRun.getStatus())) {
            throw new InvalidParameterException("Cannot cancel a POSTVALIDATED or VALIDATED billingRun");
        }

        billingRunService.cancelAsync(billingRun.getId());
    }

	/**
	 * @param billingRunId
	 * @param invoices
	 */
	public void rebuildInvoice(Long billingRunId, List<Long> invoices) {
		invoiceService.rebuildInvoices(billingRunId, invoices);

	}

	/**
	 * @param billingRunId
	 * @param invoices
	 */
	public void rejectInvoice(Long billingRunId, List<Long> invoices) {
		invoiceService.rejectInvoices(billingRunId, invoices);

	}

	/**
	 * @param billingRunId
	 * @param invoices
	 */
	public void validateInvoice(Long billingRunId, List<Long> invoices) {
		invoiceService.validateInvoices(billingRunId, invoices);
	}
	
	/**
     * @param billingRunId
     * @param invalidateXMLInvoices
     * @param invalidatePDFInvoices
     */
    public void invalidateInvoiceDocuments(Long billingRunId, Boolean invalidateXMLInvoices, Boolean invalidatePDFInvoices) {
        invoiceService.invalidateInvoiceDocuments(billingRunId, invalidateXMLInvoices, invalidatePDFInvoices);
    }

	/**
	 * @param billingRunId
	 * @param invoices
	 * @return billingRunId the id of the new billing run.
	 */
	public Long moveInvoice(Long billingRunId, List<Long> invoices) {
		return invoiceService.moveInvoices(billingRunId, invoices);
	}

	/**
	 * @param billingRunId
	 * @param invoices
	 */
	public void cancelInvoice(Long billingRunId, List<Long> invoices, Boolean deleteCanceledInvoices) {
		invoiceService.cancelInvoices(billingRunId, invoices, deleteCanceledInvoices == null ? false : deleteCanceledInvoices);
	}

	/**
	 * Delete canceled invoices for a given billing run
	 *
	 * @param billingRunId
	 * @param invoices
	 */
	public void canceledInvoices(Long billingRunId) {
		invoiceService.deleteInvoices(billingRunId);

	}

}
