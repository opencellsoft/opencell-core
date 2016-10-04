package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceCategoryApi extends BaseApi {

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private CatMessagesService catMessagesService;

    public void create(InvoiceCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (invoiceCategoryService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(InvoiceCategory.class, postData.getCode());
        }

        InvoiceCategory invoiceCategory = new InvoiceCategory();
        invoiceCategory.setCode(postData.getCode());
        invoiceCategory.setDescription(postData.getDescription());

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

        invoiceCategoryService.create(invoiceCategory, currentUser);

        // create cat messages
        if (postData.getLanguageDescriptions() != null) {
            for (LanguageDescriptionDto ld : postData.getLanguageDescriptions()) {
                CatMessages catMsg = new CatMessages(InvoiceCategory.class.getSimpleName(),invoiceCategory.getCode(), ld.getLanguageCode(), ld.getDescription());

                catMessagesService.create(catMsg, currentUser);
            }
        }
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceCategory, true, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public void update(InvoiceCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getCode(), provider);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getCode());
        }

        invoiceCategory.setDescription(postData.getDescription());

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
                    CatMessages catMsg = catMessagesService.getCatMessages(invoiceCategory.getCode(),InvoiceCategory.class.getSimpleName(),  ld.getLanguageCode(),provider);

                    if (catMsg != null) {
                        catMsg.setDescription(ld.getDescription());
                        catMessagesService.update(catMsg, currentUser);
                    } else {
                        CatMessages catMessages = new CatMessages(InvoiceCategory.class.getSimpleName(),invoiceCategory.getCode(), ld.getLanguageCode(), ld.getDescription());
                        catMessagesService.create(catMessages, currentUser);
                    }
                }
            }
        }

        invoiceCategoryService.update(invoiceCategory, currentUser);
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), invoiceCategory, false, currentUser, true);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public InvoiceCategoryDto find(String code, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceCategoryCode");
            handleMissingParameters();
        }

        InvoiceCategoryDto result = new InvoiceCategoryDto();

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(code, provider);
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, code);
        }

        result = new InvoiceCategoryDto(invoiceCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceCategory));

        List<LanguageDescriptionDto> languageDescriptions = new ArrayList<LanguageDescriptionDto>();
        for (CatMessages msg : catMessagesService.getCatMessagesList(InvoiceCategory.class.getSimpleName() , invoiceCategory.getCode(),provider)) {
            languageDescriptions.add(new LanguageDescriptionDto(msg.getLanguageCode(), msg.getDescription()));
        }

        result.setLanguageDescriptions(languageDescriptions);

        return result;
    }

    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("invoiceCategoryCode");
            handleMissingParameters();
        }

        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(code, currentUser.getProvider());
        if (invoiceCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceCategory.class, code);
        }

        invoiceCategoryService.remove(invoiceCategory, currentUser);
    }

    /**
     * Creates or updates invoice category based on the code. If passed invoice category is not yet existing, it will be created else will be updated.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(InvoiceCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {
        InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(postData.getCode(), currentUser.getProvider());

        if (invoiceCategory == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}