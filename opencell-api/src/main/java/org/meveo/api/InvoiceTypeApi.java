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

package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.SequenceDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.billing.impl.InvoiceSequenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * The CRUD Api for InvoiceType Entity.
 *
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 */
@Stateless
public class InvoiceTypeApi extends BaseCrudApi<InvoiceType, InvoiceTypeDto> {

    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;

    /** The invoice sequence service. */
    @Inject
    private InvoiceSequenceService invoiceSequenceService;

    /** The occ template service. */
    @Inject
    private OCCTemplateService occTemplateService;

    /** The seller service. */
    @Inject
    private SellerService sellerService;

    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EmailTemplateService emailTemplateService;

    /**
     * Handle parameters.
     *
     * @param postData the post data
     * @throws MeveoApiException the meveo api exception
     */
    private void handleParameters(InvoiceTypeDto postData) throws MeveoApiException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);
    }

    /**
     * Creates the InvoiceType.
     *
     * @param dto the post data
     * @return Invoice type entity
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public InvoiceType create(InvoiceTypeDto dto) throws MeveoApiException, BusinessException {
        handleParameters(dto);

        if (invoiceTypeService.findByCode(dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceType.class, dto.getCode());
        }

        InvoiceType entity = new InvoiceType();

        dtoToEntity(entity, dto);
        invoiceTypeService.create(entity);

        return entity;
    }

    /**
     * Update the InvoiceType.
     *
     * @param dto the invoice type dto
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public InvoiceType update(InvoiceTypeDto dto) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(dto);

        // check if invoiceType exists
        InvoiceType invoiceType = invoiceTypeService.findByCode(dto.getCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, dto.getCode());
        }

        if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            if (invoiceTypeService.findByCode(dto.getUpdatedCode()) != null) {
                throw new EntityAlreadyExistsException(TaxCategory.class, dto.getUpdatedCode());
            }
        }

        dtoToEntity(invoiceType, dto);

        invoiceType = invoiceTypeService.update(invoiceType);
        return invoiceType;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param entity Entity to populate
     * @param dto DTO entity object to populate from
     **/
    private void dtoToEntity(InvoiceType entity, InvoiceTypeDto dto) {

        boolean isNew = entity.getId() == null;
        if (isNew) {
            entity.setCode(dto.getCode());
        } else if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            entity.setCode(dto.getUpdatedCode());
        }
        
        if(!StringUtils.isBlank(dto.getInvoiceValidationScriptCode())) {
        	ScriptInstance invoiceValidationScript = scriptInstanceService.findByCode(dto.getInvoiceValidationScriptCode());
        	if (invoiceValidationScript == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getInvoiceValidationScriptCode());
            }
        	entity.setInvoiceValidationScript(invoiceValidationScript);
        }

        if (dto.getOccTemplateCode() != null) {
            if (!StringUtils.isBlank(dto.getOccTemplateCode())) {
                OCCTemplate occTemplate = occTemplateService.findByCode(dto.getOccTemplateCode());
                if (occTemplate == null) {
                    throw new EntityDoesNotExistsException(OCCTemplate.class, dto.getOccTemplateCode());
                }
                entity.setOccTemplate(occTemplate);
            } else {
                entity.setOccTemplate(null);
            }
        }

        if (dto.getOccTemplateNegativeCode() != null) {
            if (!StringUtils.isBlank(dto.getOccTemplateNegativeCode())) {
                OCCTemplate occTemplateNegative = occTemplateService.findByCode(dto.getOccTemplateNegativeCode());
                if (occTemplateNegative == null) {
                    throw new EntityDoesNotExistsException(OCCTemplate.class, dto.getOccTemplateNegativeCode());
                }
                entity.setOccTemplateNegative(occTemplateNegative);
            } else {
                entity.setOccTemplateNegative(null);
            }
        }

        if (dto.getCustomInvoiceXmlScriptInstanceCode() != null) {
            if (!StringUtils.isBlank(dto.getCustomInvoiceXmlScriptInstanceCode())) {
                ScriptInstance customInvoiceXmlScriptInstance =
                        scriptInstanceService.findByCode(dto.getCustomInvoiceXmlScriptInstanceCode());
                if (customInvoiceXmlScriptInstance == null) {
                    throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getCustomInvoiceXmlScriptInstanceCode());
                }
                entity.setCustomInvoiceXmlScriptInstance(customInvoiceXmlScriptInstance);
            } else {
                entity.setCustomInvoiceXmlScriptInstance(null);
            }
        }

        List<InvoiceType> invoiceTypesToApplies = new ArrayList<>();
        if (dto.getAppliesTo() != null) {
            for (String invoiceTypeCode : dto.getAppliesTo()) {
                InvoiceType tmpInvoiceType = null;
                tmpInvoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
                if (tmpInvoiceType == null) {
                    throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
                }
                invoiceTypesToApplies.add(tmpInvoiceType);
            }
            entity.setAppliesTo(invoiceTypesToApplies);
        }
        // Checking electronic billing fields
        if (dto.getMailingType() != null) {
            if (!StringUtils.isBlank(dto.getMailingType())) {
                MailingTypeEnum mailingType = MailingTypeEnum.getByLabel(dto.getMailingType());
                EmailTemplate emailTemplate = emailTemplateService.findByCode(dto.getEmailTemplateCode());
                if (mailingType != null && emailTemplate == null) {
                    throw new EntityDoesNotExistsException(EmailTemplate.class, dto.getEmailTemplateCode());
                }
                entity.setMailingType(mailingType);
                entity.setEmailTemplate(emailTemplate);
            } else {
                entity.setMailingType(null);
                entity.setEmailTemplate(null);
            }
        }

        if (dto.getSequenceDto() != null) {
            if (StringUtils.isBlank(dto.getSequenceDto().getInvoiceSequenceCode())) {
                InvoiceSequence invoiceSequence = entity.getInvoiceSequence();
                if (invoiceSequence != null) {
                    InvoiceSequence invoiceSequenceFromDto = dto.getSequenceDto().fromDto();
                    if (dto.getSequenceDto() != null && invoiceSequenceFromDto.getCurrentNumber() != null) {
                        if (invoiceSequenceFromDto.getCurrentNumber().longValue()
                                < invoiceSequenceService.getMaxCurrentInvoiceNumber(invoiceSequence.getCode()).longValue()) {
                            throw new MeveoApiException("Not able to update, check the current number");
                        }
                    }
                    if (invoiceSequenceFromDto.getCurrentNumber() != null) {
                        invoiceSequence.setCurrentNumber(invoiceSequenceFromDto.getCurrentNumber());
                    }
                    if (invoiceSequenceFromDto.getSequenceSize() != null) {
                        invoiceSequence.setSequenceSize(invoiceSequenceFromDto.getSequenceSize());
                    }
                    invoiceSequence.setCode(entity.getCode());
                    invoiceSequenceService.update(invoiceSequence);
                } else {
                    InvoiceSequence newInvoiceSequence = dto.getSequenceDto().fromDto();
                    newInvoiceSequence.setCode(entity.getCode());
                    invoiceSequenceService.create(newInvoiceSequence);
                    entity.setInvoiceSequence(newInvoiceSequence);
                }
            } else {
                InvoiceSequence invoiceSequence = invoiceSequenceService.findByCode(dto.getSequenceDto().getInvoiceSequenceCode());
                if (invoiceSequence == null) {
                    throw new EntityDoesNotExistsException(InvoiceSequence.class, dto.getSequenceDto().getInvoiceSequenceCode());
                }
                entity.setInvoiceSequence(invoiceSequence);
            }
            entity.setPrefixEL(dto.getSequenceDto().getPrefixEL());
        }

        if (dto.getDescription() != null) {
            entity.setDescription(StringUtils.isEmpty(dto.getDescription()) ? null : dto.getDescription());
        }
        if (dto.getOccTemplateCodeEl() != null) {
            entity.setOccTemplateCodeEl(StringUtils.isEmpty(dto.getOccTemplateCodeEl()) ? null : dto.getOccTemplateCodeEl());
        }
        if (dto.getOccTemplateNegativeCodeEl() != null) {
            entity.setOccTemplateNegativeCodeEl(StringUtils.isEmpty(dto.getOccTemplateNegativeCodeEl()) ? null : dto.getOccTemplateNegativeCodeEl());
        }

        if (isNew && dto.isUseSelfSequence() == null) {
            dto.setUseSelfSequence(true);
        }
        if (dto.isUseSelfSequence() != null) {
            entity.setUseSelfSequence(dto.isUseSelfSequence());
        }

        if (dto.getSellerSequences() != null) {
            for (Entry<String, SequenceDto> entry : dto.getSellerSequences().entrySet()) {
                Seller seller = sellerService.findByCode(entry.getKey());
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, entry.getKey());
                }
                if (entry.getValue().getSequenceSize().intValue() < 0) {
                    throw new MeveoApiException("sequence size value must be positive");
                }
                if (entry.getValue().getCurrentInvoiceNb().intValue() < 0) {
                    throw new MeveoApiException("current invoice number value must be positive");
                }

                if (entry.getValue() == null) {
                    entity.getSellerSequence().remove(seller);
                } else if (entity.isContainsSellerSequence(seller)) {
                    InvoiceSequence invoiceSequenceInvoiceTypeSeller = entity.getSellerSequenceByType(seller).getInvoiceSequence();
                    invoiceSequenceInvoiceTypeSeller = entry.getValue().updateFromDto(invoiceSequenceInvoiceTypeSeller);
                    invoiceSequenceService.update(invoiceSequenceInvoiceTypeSeller);
                    entity.getSellerSequenceByType(seller).setInvoiceSequence(invoiceSequenceInvoiceTypeSeller);
                    entity.getSellerSequenceByType(seller).setPrefixEL(entry.getValue().getPrefixEL());
                } else {
                    InvoiceSequence invoiceSequenceInvoiceTypeSeller = entry.getValue().fromDto();
                    invoiceSequenceInvoiceTypeSeller.setCode(entity.getCode() + "_" + seller.getCode());
                    invoiceSequenceService.create(invoiceSequenceInvoiceTypeSeller);
                    entity.getSellerSequence().add(new InvoiceTypeSellerSequence(entity, seller,
                            invoiceSequenceInvoiceTypeSeller, entry.getValue().getPrefixEL()));
                }
            }
        }

        if (dto.isInvoiceAccountable() != null) {
            entity.setInvoiceAccountable(dto.isInvoiceAccountable());
        } // else default value = true

        if (!StringUtils.isBlank(dto.getBillingTemplateName()) && StringUtils.isBlank(dto.getBillingTemplateNameEL())) {
            dto.setBillingTemplateNameEL(dto.getBillingTemplateName());
        }
        if (dto.getBillingTemplateNameEL() != null) {
            entity.setBillingTemplateNameEL(StringUtils.isEmpty(dto.getBillingTemplateNameEL()) ? null : dto.getBillingTemplateNameEL());
        }

        if (isNew && dto.isMatchingAuto() == null) {
            dto.setMatchingAuto(false);
        }
        if (dto.isMatchingAuto() != null) {
            entity.setMatchingAuto(dto.isMatchingAuto());
        }

        if (dto.getPdfFilenameEL() != null) {
            entity.setPdfFilenameEL(StringUtils.isEmpty(dto.getPdfFilenameEL()) ? null : dto.getPdfFilenameEL());
        }
        if (dto.getXmlFilenameEL() != null) {
            entity.setXmlFilenameEL(StringUtils.isEmpty(dto.getXmlFilenameEL()) ? null : dto.getXmlFilenameEL());
        }

        // populate customFields
        try {
            populateCustomFields(dto.getCustomFields(), entity, isNew, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    @Override
    protected BiFunction<InvoiceType, CustomFieldsDto, InvoiceTypeDto> getEntityToDtoFunction() {
        return InvoiceTypeDto::new;
    }
}