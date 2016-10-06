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
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
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

    public void create(InvoiceSubCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        
        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        if (invoiceSubCategoryService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(InvoiceSubCategory.class, postData.getCode());
        }

        // check if invoice cat exists
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getInvoiceCategory(), provider);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategory());
        }

        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setCode(postData.getCode());
        invoiceSubCategory.setDescription(postData.getDescription());
        invoiceSubCategory.setAccountingCode(postData.getAccountingCode());

        if (provider.getTradingLanguages() != null) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : provider.getTradingLanguages()) {
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

        invoiceSubCategoryService.create(invoiceSubCategory, currentUser);

        // create cat messages
        if (postData.getLanguageDescriptions() != null) {
            for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                CatMessages catMsg = new CatMessages(InvoiceSubCategory.class.getSimpleName(), invoiceSubCategory.getCode(), ld.getLanguageCode(), ld.getDescription());

                catMessagesService.create(catMsg, currentUser);
            }
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, true, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }       
    }

    public void update(InvoiceSubCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getInvoiceCategory())) {
            missingParameters.add("invoiceCategory");
        }

        
        handleMissingParameters();
        

        Provider provider = currentUser.getProvider();

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getCode(), provider);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getCode());
        }

        // check if invoice cat exists
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getInvoiceCategory(), provider);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategory());
        }

        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setDescription(postData.getDescription());
        invoiceSubCategory.setAccountingCode(postData.getAccountingCode());

        if (provider.getTradingLanguages() != null) {
            if (postData.getLanguageDescriptions() != null) {
                for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                    boolean match = false;

                    for (TradingLanguage tl : provider.getTradingLanguages()) {
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
                    CatMessages catMsg = catMessagesService.getCatMessages( invoiceSubCategory.getCode(),InvoiceSubCategory.class.getSimpleName(), ld.getLanguageCode(),provider);
                    
                    if (catMsg != null) {
                        catMsg.setDescription(ld.getDescription());
                        catMessagesService.update(catMsg, currentUser);
                    } else {
                        CatMessages catMessages = new CatMessages(InvoiceSubCategory.class.getSimpleName() , invoiceSubCategory.getCode(), ld.getLanguageCode(),
                            ld.getDescription());
                        catMessagesService.create(catMessages, currentUser);
                    }
                }
            }
        }
        
        invoiceSubCategoryService.update(invoiceSubCategory, currentUser);
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceSubCategory, false, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public InvoiceSubCategoryDto find(String code, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceSubCategoryCode");
            handleMissingParameters();
        }

        InvoiceSubCategoryDto result = new InvoiceSubCategoryDto();

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(code, provider, Arrays.asList("invoiceCategory"));
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, code);
        }

        result = new InvoiceSubCategoryDto(invoiceSubCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceSubCategory));

        List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
        for (CatMessages msg : catMessagesService.getCatMessagesList(InvoiceSubCategory.class.getSimpleName() , invoiceSubCategory.getCode(),provider)) {
            languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
        }

        result.setLanguageDescriptions(languageDescriptions);

        return result;
    }

    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceSubCategoryCode");
            handleMissingParameters();
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(code, currentUser.getProvider());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, code);
        }

        invoiceSubCategoryService.remove(invoiceSubCategory, currentUser);

    }

    /**
     * Create or update invoice subcategory based on code.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(InvoiceSubCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (invoiceSubCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            update(postData, currentUser);
        } else {
            create(postData, currentUser);
        }
    }
}