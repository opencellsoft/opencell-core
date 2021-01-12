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
        billingRun.setProcessType(dto.getBillingRunTypeEnum());
        billingRun.setStartDate(dto.getStartDate());
        billingRun.setEndDate(dto.getEndDate());
        billingRun.setProcessDate(new Date());
        billingRun.setReferenceDate(dto.getReferenceDate());
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setInvoiceDate(dto.getInvoiceDate());
        billingRun.setLastTransactionDate(dto.getLastTransactionDate());
        billingRun.setSkipValidationScript(dto.getSkipValidationScript());
        billingRun.setRejectAutoAction(dto.getRejectAutoAction());
        billingRun.setSuspectAutoAction(dto.getSuspectAutoAction());
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
	 * @param invoices
	 */
	public void moveInvoice(Long billingRunId, List<Long> invoices) {
		invoiceService.moveInvoices(billingRunId, invoices);
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
