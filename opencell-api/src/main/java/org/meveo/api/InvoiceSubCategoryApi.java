package org.meveo.api;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceSubCategoryApi extends BaseApi {

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    public void create(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        handleMissingParametersAndValidate(postData);

        if (invoiceSubCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(InvoiceSubCategory.class, postData.getCode());
        }

        // check if invoice cat exists
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getInvoiceCategory());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategory());
        }

        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setCode(postData.getCode());
        invoiceSubCategory.setDescription(postData.getDescription());
        invoiceSubCategory.setAccountingCode(postData.getAccountingCode());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceSubCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        invoiceSubCategoryService.create(invoiceSubCategory);
    }

    public void update(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        handleMissingParametersAndValidate(postData);

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getCode());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getCode());
        }

        // check if invoice cat exists
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getInvoiceCategory());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategory());
        }
        invoiceSubCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setDescription(postData.getDescription());
        invoiceSubCategory.setAccountingCode(postData.getAccountingCode());

        if (postData.getLanguageDescriptions() != null) {
            invoiceSubCategory.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), invoiceSubCategory.getDescriptionI18n()));
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, false, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoiceSubCategory = invoiceSubCategoryService.update(invoiceSubCategory);
    }

    public InvoiceSubCategoryDto find(String code) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceSubCategoryCode");
            handleMissingParameters();
        }

        InvoiceSubCategoryDto result = new InvoiceSubCategoryDto();

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(code, Arrays.asList("invoiceCategory"));
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, code);
        }

        result = new InvoiceSubCategoryDto(invoiceSubCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceSubCategory, true));

        return result;
    }

    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceSubCategoryCode");
            handleMissingParameters();
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(code);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, code);
        }

        invoiceSubCategoryService.remove(invoiceSubCategory);

    }

    /**
     * Create or update invoice subcategory based on code.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {
        if (invoiceSubCategoryService.findByCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }
}