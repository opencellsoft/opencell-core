package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.service.catalog.impl.InvoiceCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceCategoryApi extends BaseApi {

    @Inject
    private InvoiceCategoryService invoiceCategoryService;


    public void create(InvoiceCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        if (invoiceCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceCategory.class, postData.getCode());
        }

        InvoiceCategory invoiceCategory = new InvoiceCategory();
        invoiceCategory.setCode(postData.getCode());
        invoiceCategory.setDescription(postData.getDescription());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceCategory, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        invoiceCategoryService.create(invoiceCategory);

    }

    public void update(InvoiceCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getCode());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getCode());
        }
        invoiceCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        invoiceCategory.setDescription(postData.getDescription());

        if (postData.getLanguageDescriptions() != null) {
            invoiceCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), invoiceCategory.getDescriptionI18n()));
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceCategory, false, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceCategory = invoiceCategoryService.update(invoiceCategory);
    }

    public InvoiceCategoryDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceCategoryCode");
            handleMissingParameters();
        }

        InvoiceCategoryDto result = new InvoiceCategoryDto();

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(code);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, code);
        }

        result = new InvoiceCategoryDto(invoiceCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceCategory, true));

        return result;
    }

    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceCategoryCode");
            handleMissingParameters();
        }

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(code);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, code);
        }

        invoiceCategoryService.remove(invoiceCategory);
    }

    /**
     * Creates or updates invoice category based on the code. If passed invoice category is not yet existing, it will be created else will be updated.
     * 
     * @param postData posted data.
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(InvoiceCategoryDto postData) throws MeveoApiException, BusinessException {
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getCode());

        if (invoiceCategory == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
}