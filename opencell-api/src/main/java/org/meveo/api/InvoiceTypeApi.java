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
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.admin.impl.SellerService;
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
        invoiceType.setOccTemplateCodeEl(postData.getOccTemplateCodeEl());
        invoiceType.setOccTemplateNegativeCodeEl(postData.getOccTemplateNegativeCodeEl());
        invoiceType.setAppliesTo(invoiceTypesToApplies);
        invoiceType.setInvoiceSequence(postData.getSequenceDto() == null ? null : postData.getSequenceDto().fromDto());
        invoiceType.setUseSelfSequence(postData.isUseSelfSequence());
        if (postData.getSellerSequences() != null) {
            for (Entry<String, SequenceDto> entry : postData.getSellerSequences().entrySet()) {
                Seller seller = sellerService.findByCode(entry.getKey());
                if (seller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, entry.getKey());
                }
                SequenceDto value = entry.getValue();
                if (value == null || value.getSequenceSize().intValue() < 0) {
                    throw new MeveoApiException("sequence size value must be positive");
                }

                if (value == null || value.getCurrentInvoiceNb().intValue() < 0) {
                    throw new MeveoApiException("current invoice number value must be positive");
                }

                invoiceType.getSellerSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, value.fromDto(), value.getPrefixEL()));
            }
        }
        invoiceType.setMatchingAuto(postData.isMatchingAuto());
        invoiceType.setBillingTemplateName(postData.getBillingTemplateName());
        invoiceType.setBillingTemplateNameEL(postData.getBillingTemplateNameEL());
        invoiceType.setPdfFilenameEL(postData.getPdfFilenameEL());
        invoiceType.setXmlFilenameEL(postData.getXmlFilenameEL());
        invoiceTypeService.create(invoiceType);
        return result;
    }

    /**
     * Update the InvoiceType.
     *
     * @param invoiceTypeDto the invoice type dto
     * @return the action status
     * @throws MeveoApiException the meveo api exception
     * @throws BusinessException the business exception
     */
    public ActionStatus update(InvoiceTypeDto invoiceTypeDto) throws MeveoApiException, BusinessException {

        handleParameters(invoiceTypeDto);
        ActionStatus result = new ActionStatus();

        // check if invoiceType exists
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeDto.getCode());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeDto.getCode());
        }
        invoiceType.setCode(StringUtils.isBlank(invoiceTypeDto.getUpdatedCode()) ? invoiceTypeDto.getCode() : invoiceTypeDto.getUpdatedCode());
        //TODO
        /*
        if (invoiceTypeDto.getSequenceDto() != null && invoiceTypeDto.getSequenceDto().getCurrentInvoiceNb() != null) {
            if (invoiceTypeDto.getSequenceDto().getCurrentInvoiceNb().longValue() < invoiceTypeService.getMaxCurrentInvoiceNumber(invoiceTypeDto.getCode()).longValue()) {
                throw new MeveoApiException("Not able to update, check the current number");
            }

        }*/
        if (invoiceTypeDto.getSequenceDto() != null) {
            invoiceType.setInvoiceSequence(invoiceTypeDto.getSequenceDto().fromDto());
        }
        OCCTemplate occTemplate = occTemplateService.findByCode(invoiceTypeDto.getOccTemplateCode());
        if (occTemplate == null) {
            throw new EntityDoesNotExistsException(OCCTemplate.class, invoiceTypeDto.getOccTemplateCode());
        }

        OCCTemplate occTemplateNegative = null;
        if (!StringUtils.isBlank(invoiceTypeDto.getOccTemplateNegativeCode())) {
            occTemplateNegative = occTemplateService.findByCode(invoiceTypeDto.getOccTemplateNegativeCode());
            if (occTemplateNegative == null) {
                throw new EntityDoesNotExistsException(OCCTemplate.class, invoiceTypeDto.getOccTemplateNegativeCode());
            }
        }
        invoiceType.setOccTemplateNegative(occTemplateNegative);
        invoiceType.setOccTemplate(occTemplate);
        
        if (invoiceTypeDto.getOccTemplateCodeEl() != null) {
            invoiceType.setOccTemplateCodeEl(invoiceTypeDto.getOccTemplateCodeEl());
        }
        if (invoiceTypeDto.getOccTemplateNegativeCodeEl() != null) {
            invoiceType.setOccTemplateNegativeCodeEl(invoiceTypeDto.getOccTemplateNegativeCodeEl());
        }
        
        if (!StringUtils.isBlank(invoiceTypeDto.getDescription())) {
            invoiceType.setDescription(invoiceTypeDto.getDescription());
        }

        if (!StringUtils.isBlank(invoiceTypeDto.isMatchingAuto())) {
            invoiceType.setMatchingAuto(invoiceTypeDto.isMatchingAuto());
        }
        List<InvoiceType> invoiceTypesToApplies = new ArrayList<InvoiceType>();
        if (invoiceTypeDto.getAppliesTo() != null) {
            for (String invoiceTypeCode : invoiceTypeDto.getAppliesTo()) {
                InvoiceType tmpInvoiceType = null;
                tmpInvoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
                if (tmpInvoiceType == null) {
                    throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
                }
                invoiceTypesToApplies.add(tmpInvoiceType);
            }
        }
        invoiceType.setAppliesTo(invoiceTypesToApplies);

        if (invoiceTypeDto.getSellerSequences() != null) {
            for (Entry<String, SequenceDto> entry : invoiceTypeDto.getSellerSequences().entrySet()) {
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
                	//TODO
                    invoiceType.getSellerSequenceByType(seller).setInvoiceSequence(entry.getValue().updateFromDto(invoiceType.getSellerSequenceByType(seller).getInvoiceSequence()));
                } else {
                    invoiceType.getSellerSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, entry.getValue().fromDto(), entry.getValue().getPrefixEL()));
                }
            }
        }
        if (invoiceTypeDto.getBillingTemplateName() != null) {
            invoiceType.setBillingTemplateName(invoiceTypeDto.getBillingTemplateName());
        }
        if (invoiceTypeDto.getBillingTemplateNameEL() != null) {
            invoiceType.setBillingTemplateNameEL(invoiceTypeDto.getBillingTemplateNameEL());
        }
        if (invoiceTypeDto.getPdfFilenameEL() != null) {
            invoiceType.setPdfFilenameEL(invoiceTypeDto.getPdfFilenameEL());
        }
        if (invoiceTypeDto.getXmlFilenameEL() != null) {
            invoiceType.setXmlFilenameEL(invoiceTypeDto.getXmlFilenameEL());
        }
        
        invoiceType.setUseSelfSequence(invoiceTypeDto.isUseSelfSequence());
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
        result = new InvoiceTypeDto(invoiceType);
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
                InvoiceTypeDto invoiceTypeDto = new InvoiceTypeDto(t);
                invoiceTypeesDto.getInvoiceTypes().add(invoiceTypeDto);
            }
        }

        return invoiceTypeesDto;
    }
}