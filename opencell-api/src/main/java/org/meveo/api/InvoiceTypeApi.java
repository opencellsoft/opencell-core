package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.SequenceDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.billing.InvoiceTypesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.payments.impl.OCCTemplateService;

/**
 * The CRUD Api for InvoiceType Entity.
 *
 * @author anasseh
 */
@Stateless
public class InvoiceTypeApi extends BaseApi {

    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;

    /** The occ template service. */
    @Inject
    private OCCTemplateService occTemplateService;

    /** The seller service. */
    @Inject
    private SellerService sellerService;
    
    @Inject
    protected EntityToDtoConverter entityToDtoConverter;

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
        if (StringUtils.isBlank(postData.getOccTemplateCode())) {
            missingParameters.add("occTemplateCode");
        }
        handleMissingParametersAndValidate(postData);
    }

    /**
     * Creates the InvoiceType.
     *
     * @param postData the post data
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public ActionStatus create(InvoiceTypeDto postData) throws MeveoApiException, BusinessException {
        handleParameters(postData);
        ActionStatus result = new ActionStatus();

        if (invoiceTypeService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceType.class, postData.getCode());
        }
        OCCTemplate occTemplate = occTemplateService.findByCode(postData.getOccTemplateCode());
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateCode());
        }

        OCCTemplate occTemplateNegative = null;
        if (!StringUtils.isBlank(postData.getOccTemplateNegativeCode())) {
            occTemplateNegative = occTemplateService.findByCode(postData.getOccTemplateNegativeCode());
            if (occTemplateNegative == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateNegativeCode());
            }
        }

        List<InvoiceType> invoiceTypesToApplies = new ArrayList<InvoiceType>();
        if (postData.getAppliesTo() != null) {
            for (String invoiceTypeCode : postData.getAppliesTo()) {
                InvoiceType tmpInvoiceType = null;
                tmpInvoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
                if (tmpInvoiceType == null) {
                    throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
                }
                invoiceTypesToApplies.add(tmpInvoiceType);
            }
        }
        InvoiceType invoiceType = new InvoiceType();
        invoiceType.setCode(postData.getCode());
        invoiceType.setDescription(postData.getDescription());
        invoiceType.setOccTemplate(occTemplate);
        invoiceType.setOccTemplateNegative(occTemplateNegative);
        invoiceType.setAppliesTo(invoiceTypesToApplies);
        invoiceType.setSequence(postData.getSequenceDto() == null ? null : postData.getSequenceDto().fromDto());
        if (postData.getSellerSequences() != null) {
            for (Entry<String, SequenceDto> entry : postData.getSellerSequences().entrySet()) {
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
                    invoiceType.getSellerSequence().remove(seller);
                } else {
                    invoiceType.getSellerSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, entry.getValue().fromDto()));
                }
            }
        }
        invoiceType.setMatchingAuto(postData.isMatchingAuto());
        invoiceType.setBillingTemplateName(postData.getBillingTemplateName());
        invoiceType.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        invoiceType.setPdfFilenameEL(postData.getPdfFilenameEL());
        invoiceType.setXmlFilenameEL(postData.getXmlFilenameEL());
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceType, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        invoiceTypeService.create(invoiceType);
        return result;
    }

    /**
     * Update the InvoiceType.
     *
     * @param postData the invoice type dto
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public ActionStatus update(InvoiceTypeDto postData) throws MeveoApiException, BusinessException {

        handleParameters(postData);
        ActionStatus result = new ActionStatus();

        // check if invoiceType exists
        InvoiceType invoiceType = invoiceTypeService.findByCode(postData.getCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, postData.getCode());
        }
        invoiceType.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        if (postData.getSequenceDto() != null && postData.getSequenceDto().getCurrentInvoiceNb() != null) {
            if (postData.getSequenceDto().getCurrentInvoiceNb().longValue() < invoiceTypeService.getMaxCurrentInvoiceNumber(postData.getCode()).longValue()) {
                throw new MeveoApiException("Not able to update, check the current number");
            }

        }
        if (postData.getSequenceDto() != null) {
            if (invoiceType.getSequence() != null) {
                invoiceType.setSequence(postData.getSequenceDto().updateFromDto(invoiceType.getSequence()));
            } else {
                invoiceType.setSequence(postData.getSequenceDto().fromDto());
            }
        }
        OCCTemplate occTemplate = occTemplateService.findByCode(postData.getOccTemplateCode());
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateCode());
        }

        OCCTemplate occTemplateNegative = null;
        if (!StringUtils.isBlank(postData.getOccTemplateNegativeCode())) {
            occTemplateNegative = occTemplateService.findByCode(postData.getOccTemplateNegativeCode());
            if (occTemplateNegative == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, postData.getOccTemplateNegativeCode());
            }
        }
        invoiceType.setOccTemplateNegative(occTemplateNegative);
        invoiceType.setOccTemplate(occTemplate);
        if (!StringUtils.isBlank(postData.getDescription())) {
            invoiceType.setDescription(postData.getDescription());
        }

        if (!StringUtils.isBlank(postData.isMatchingAuto())) {
            invoiceType.setMatchingAuto(postData.isMatchingAuto());
        }
        List<InvoiceType> invoiceTypesToApplies = new ArrayList<InvoiceType>();
        if (postData.getAppliesTo() != null) {
            for (String invoiceTypeCode : postData.getAppliesTo()) {
                InvoiceType tmpInvoiceType = null;
                tmpInvoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
                if (tmpInvoiceType == null) {
                    throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
                }
                invoiceTypesToApplies.add(tmpInvoiceType);
            }
        }
        invoiceType.setAppliesTo(invoiceTypesToApplies);

        if (postData.getSellerSequences() != null) {
            for (Entry<String, SequenceDto> entry : postData.getSellerSequences().entrySet()) {
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
                    invoiceType.getSellerSequence().remove(seller);
                } else if (invoiceType.isContainsSellerSequence(seller)) {
                    invoiceType.getSellerSequenceByType(seller).setSequence(entry.getValue().updateFromDto(invoiceType.getSellerSequenceByType(seller).getSequence()));
                } else {
                    invoiceType.getSellerSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, entry.getValue().fromDto()));
                }
            }
        }
        if (postData.getBillingTemplateName() != null) {
            invoiceType.setBillingTemplateName(postData.getBillingTemplateName());
        }
        if (postData.getBillingTemplateNameEL() != null) {
            invoiceType.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        }
        if (postData.getPdfFilenameEL() != null) {
            invoiceType.setPdfFilenameEL(postData.getPdfFilenameEL());
        }
        if (postData.getXmlFilenameEL() != null) {
            invoiceType.setXmlFilenameEL(postData.getXmlFilenameEL());
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceType, false, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        invoiceTypeService.update(invoiceType);
        return result;
    }

    /**
     * Find the InvoiceType.
     *
     * @param invoiceTypeCode the invoice type code
     * @return the invoice type dto
     * @throws MeveoApiException the meveo api exception
     */
    public InvoiceTypeDto find(String invoiceTypeCode) throws MeveoApiException {

        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        InvoiceTypeDto result = new InvoiceTypeDto();

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
        }
        result = new InvoiceTypeDto(invoiceType, entityToDtoConverter.getCustomFieldsDTO(invoiceType, true));
        return result;
    }

    /**
     * Removes the InvoiceType.
     *
     * @param invoiceTypeCode the invoice type code
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public ActionStatus remove(String invoiceTypeCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        ActionStatus result = new ActionStatus();
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
        }
        invoiceTypeService.remove(invoiceType);
        return result;
    }

    /**
     * Creates the or update the InvoiceType.
     *
     * @param invoiceTypeDto the invoice type dto
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public void createOrUpdate(InvoiceTypeDto invoiceTypeDto) throws MeveoApiException, BusinessException {
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeDto.getCode());

        if (invoiceType == null) {
            create(invoiceTypeDto);
        } else {
            update(invoiceTypeDto);
        }
    }

    /**
     * List InvoiceTypes.
     *
     * @return the invoice types dto
     * @throws MeveoApiException the meveo api exception
     */
    public InvoiceTypesDto list() throws MeveoApiException {
        InvoiceTypesDto invoiceTypeesDto = new InvoiceTypesDto();

        List<InvoiceType> invoiceTypees = invoiceTypeService.list();
        if (invoiceTypees != null && !invoiceTypees.isEmpty()) {
            for (InvoiceType t : invoiceTypees) {
                InvoiceTypeDto invoiceTypeDto = new InvoiceTypeDto(t, entityToDtoConverter.getCustomFieldsDTO(t, true));
                invoiceTypeesDto.getInvoiceTypes().add(invoiceTypeDto);
            }
        }

        return invoiceTypeesDto;
    }
}