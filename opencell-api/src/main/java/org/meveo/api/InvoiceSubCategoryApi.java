package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.CatMessagesService;
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

    @Inject
    private CatMessagesService catMessagesService;
    
    @Inject
    private TradingLanguageService tradingLanguageService;

    public void create(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        
        handleMissingParameters();
        

        

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

        List<TradingLanguage> tradingLanguages = tradingLanguageService.list();
        if (!tradingLanguages.isEmpty()) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : tradingLanguages) {
                        if (tl.getLanguageCode().equals(ld.getLanguageCode())) {
                            match = true;
                            break;
                        }
                    }

                    if (!match) {
                        throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
                    }
                }
            }
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, true, true);

        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }        

        invoiceSubCategoryService.create(invoiceSubCategory);

        // create cat messages
        if (postData.getLanguageDescriptions() != null) {
            for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                CatMessages catMsg = new CatMessages(InvoiceSubCategory.class.getSimpleName(), invoiceSubCategory.getCode(), ld.getLanguageCode(), ld.getDescription());

                catMessagesService.create(catMsg);
            }
        }        
    }

    public void update(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        
        handleMissingParameters();
        

        

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

        List<TradingLanguage> tradingLanguages = tradingLanguageService.list();
        if (!tradingLanguages.isEmpty()) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : tradingLanguages) {
                        if (tl.getLanguageCode().equals(ld.getLanguageCode())) {
                            match = true;
                            break;
                        }
                    }

                    if (!match) {
                        throw new MeveoApiException(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, "Language " + ld.getLanguageCode() + " is not supported by the provider.");
                    }
                }

                // create cat messages
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {                   
                    CatMessages catMsg = catMessagesService.getCatMessages( invoiceSubCategory.getCode(),InvoiceSubCategory.class.getSimpleName(), ld.getLanguageCode());
                    
                    if (catMsg != null) {
                        catMsg.setDescription(ld.getDescription());
                        catMessagesService.update(catMsg);
                    } else {
                        CatMessages catMessages = new CatMessages(InvoiceSubCategory.class.getSimpleName() , invoiceSubCategory.getCode(), ld.getLanguageCode(),
                            ld.getDescription());
                        catMessagesService.create(catMessages);
                    }
                }
            }
        }
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, false, true);

        } catch (MissingParameterException e) {
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

        result = new InvoiceSubCategoryDto(invoiceSubCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceSubCategory));

        List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
        for (CatMessages msg : catMessagesService.getCatMessagesList(InvoiceSubCategory.class.getSimpleName() , invoiceSubCategory.getCode())) {
            languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
        }

        result.setLanguageDescriptions(languageDescriptions);

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
     * @param postData

     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(InvoiceSubCategoryDto postData) throws MeveoApiException, BusinessException {
        if (invoiceSubCategoryService.findByCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }
}