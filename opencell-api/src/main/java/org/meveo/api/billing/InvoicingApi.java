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

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.account.AccountHierarchyApi;
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
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.invoicing.impl.BillingService;
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
    BillingService billingService;

    @Inject
    BillingCycleService billingCycleService;

    @Inject
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    @MeveoParamBean
    private ParamBean paramBean;

    public long createBillingRun(CreateBillingRunDto createBillingRunDto) throws MeveoApiException, BusinessApiException, MissingParameterException, EntityDoesNotExistsException, BusinessException {

        String allowManyInvoicing = paramBean.getProperty("billingRun.allowManyInvoicing", "true");
        boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
        
        if (billingRunService.isActiveBillingRunsExist() && !isAllowed) {
            throw new BusinessApiException("error.invoicing.alreadyLunched");
        }

        if (StringUtils.isBlank(createBillingRunDto.getBillingCycleCode())) {
            missingParameters.add("billingCycleCode");
        }
        if (createBillingRunDto.getBillingRunTypeEnum() == null) {
            missingParameters.add("billingRunType");
        }

        handleMissingParameters();

        BillingCycle billingCycleInput = billingCycleService.findByCode(createBillingRunDto.getBillingCycleCode());
        if (billingCycleInput == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, createBillingRunDto.getBillingCycleCode());
        }
        BillingRun billingRunEntity = new BillingRun();
        billingRunEntity.setBillingCycle(billingCycleInput);
        billingRunEntity.setProcessType(createBillingRunDto.getBillingRunTypeEnum());
        billingRunEntity.setStartDate(createBillingRunDto.getStartDate());
        billingRunEntity.setEndDate(createBillingRunDto.getEndDate());
        billingRunEntity.setProcessDate(new Date());
        billingRunEntity.setInvoiceDate(createBillingRunDto.getInvoiceDate());
        if (createBillingRunDto.getInvoiceDate() == null) {
            if (billingCycleInput.getInvoiceDateProductionDelay() != null) {
                billingRunEntity.setInvoiceDate(DateUtils.addDaysToDate(billingRunEntity.getProcessDate(), billingCycleInput.getInvoiceDateProductionDelay()));
            } else {
                billingRunEntity.setInvoiceDate(billingRunEntity.getProcessDate());
            }
        }
        billingRunEntity.setLastTransactionDate(createBillingRunDto.getLastTransactionDate());
        if (createBillingRunDto.getLastTransactionDate() == null) {
            if (billingCycleInput.getTransactionDateDelay() != null) {
                billingRunEntity.setLastTransactionDate(DateUtils.addDaysToDate(billingRunEntity.getProcessDate(), billingCycleInput.getTransactionDateDelay()));
            } else {
                billingRunEntity.setLastTransactionDate(DateUtils.addDaysToDate(billingRunEntity.getProcessDate(), 1));
            }
        }
        billingRunEntity.setReferenceDate(createBillingRunDto.getReferenceDate());
        billingRunEntity.setStatus(BillingRunStatusEnum.NEW);
        billingRunService.create(billingRunEntity);
        
        // populate customFields
        try {
            populateCustomFields(createBillingRunDto.getCustomFields(), billingRunEntity, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        
        return billingRunEntity.getId();
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

    public PreInvoicingReportsDTO getPreInvoicingReport(Long billingRunId) throws MissingParameterException, BusinessApiException, EntityDoesNotExistsException,
            BusinessException {
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

    public PostInvoicingReportsDTO getPostInvoicingReport(Long billingRunId) throws MissingParameterException, BusinessApiException,
            EntityDoesNotExistsException, BusinessException {
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void validateBillingRun(Long billingRunId) throws MissingParameterException, BusinessException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
        if("true".equals(paramBean.getProperty("invoicing.useRefactoredInvoicingJob", "true"))) {
        	billingService.forceValidate(billingRunId);
        }else {
        	billingRunService.forceValidate(billingRunId);
        }
       
    }
   
    public void cancelBillingRun(Long billingRunId) throws MissingParameterException, EntityDoesNotExistsException, BusinessApiException, BusinessException {
        if (billingRunId == null || billingRunId.longValue() == 0) {
            missingParameters.add("billingRunId");
            handleMissingParameters();
        }
    	BillingRun billingRun = getBillingRun(billingRunId);
        if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())) {
            throw new BusinessApiException("Cannot cancel a POSTVALIDATED billingRun");
        }
        if (BillingRunStatusEnum.VALIDATED.equals(billingRun.getStatus())) {
            throw new BusinessApiException("Cannot cancel a VALIDATED billingRun");
        }
        
        billingRun.setStatus(BillingRunStatusEnum.CANCELLING);
        billingRunService.cancelAsync(billingRun.getId());
    }

}
