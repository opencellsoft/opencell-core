package org.meveo.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CatMessagesDto;
import org.meveo.api.dto.response.CatMessagesListDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

public class CatMessagesApi extends BaseApi {

    @Inject
    private CatMessagesService catMessagesService;
    @Inject
    private TitleService titleService;
    @Inject
    private TaxService taxService;
    @Inject
    private InvoiceCategoryService invoiceCategoryService;
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;
    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;
    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;
    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;
    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    @Inject
    private TradingLanguageService tradingLanguageService;

    private Map<String, String> result = new HashMap<String, String>();

    /**
     * Creates new CatMessages.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void create(CatMessagesDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getObjectType())) {
            missingParameters.add("objectType");
        }
        if (StringUtils.isBlank(postData.getLanguageCode())) {
            missingParameters.add("languageCode");
        }
        if (StringUtils.isBlank(postData.getEntityCode())) {
            missingParameters.add("entityCode");
        }
        if (StringUtils.isBlank(postData.getDescriptionTranslation())) {
            missingParameters.add("descriptionTranslation");
        }
        
        handleMissingParameters();
        

        String objectType = postData.getObjectType();
        String languageCode = postData.getLanguageCode();
        String descriptionTranslation = postData.getDescriptionTranslation();
        String messageCode = null;

        // check if object type is supported
        if (!getObjectTypes().keySet().contains(objectType)) {
            throw new BusinessApiException("Objectype " + objectType + " is not currently supported");
        }

        String objectTypeCode = postData.getEntityCode();

        // check if language code is existing
        if (tradingLanguageService.findByTradingLanguageCode(languageCode, currentUser.getProvider()) == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, languageCode);
        }

        // check if businessEntity exist
        if (objectType.equals("Price plans")) {
            PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(objectTypeCode, currentUser.getProvider());
            if (pricePlanMatrix != null) {
                messageCode = pricePlanMatrix.getClass().getSimpleName() + "_" + pricePlanMatrix.getId();
            } else {
                throw new EntityDoesNotExistsException(PricePlanMatrix.class, objectTypeCode);
            }
        }

        if (objectType.equals("Charges")) {
            UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService.findByCode(objectTypeCode, currentUser.getProvider());
            if (usageChargeTemplate != null) {
                messageCode = usageChargeTemplate.getClass().getSimpleName() + "_" + usageChargeTemplate.getId();
            }

            RecurringChargeTemplate recurringChargeTemplate = recurringChargeTemplateService.findByCode(objectTypeCode, currentUser.getProvider());
            if (recurringChargeTemplate != null) {
                messageCode = recurringChargeTemplate.getClass().getSimpleName() + "_" + recurringChargeTemplate.getId();
            }
            OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(objectTypeCode, currentUser.getProvider());
            if (oneShotChargeTemplate != null) {
                messageCode = oneShotChargeTemplate.getClass().getSimpleName() + "_" + oneShotChargeTemplate.getId();
            }

            if (messageCode == null) {
                throw new EntityDoesNotExistsException(ChargeTemplate.class, objectTypeCode);
            }
        }
        if (objectType.equals("Titles and civilities")) {
            Title title = titleService.findByCode(currentUser.getProvider(), objectTypeCode);
            if (title != null) {
                messageCode = title.getClass().getSimpleName() + "_" + title.getId();
            } else {
                throw new EntityDoesNotExistsException(Title.class, objectTypeCode);
            }
        }
        if (objectType.equals("Taxes")) {
            Tax tax = taxService.findByCode(objectTypeCode, currentUser.getProvider());
            if (tax != null) {
                messageCode = tax.getClass().getSimpleName() + "_" + tax.getId();
            } else {
                throw new EntityDoesNotExistsException(Tax.class, objectTypeCode);
            }
        }
        if (objectType.equals("Invoice categories")) {
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(objectTypeCode, currentUser.getProvider());
            if (invoiceCategory != null) {
                messageCode = invoiceCategory.getClass().getSimpleName() + "_" + invoiceCategory.getId();
            } else {
                throw new EntityDoesNotExistsException(InvoiceCategory.class, objectTypeCode);
            }
        }
        if (objectType.equals("Invoice subcategories")) {
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(objectTypeCode, currentUser.getProvider());
            if (invoiceSubCategory != null) {
                messageCode = invoiceSubCategory.getClass().getSimpleName() + "_" + invoiceSubCategory.getId();
            } else {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, objectTypeCode);
            }
        }

        if (messageCode != null) {
            CatMessages existingEntity = catMessagesService.findByCodeAndLanguage(messageCode, languageCode, currentUser.getProvider());
            if (existingEntity != null) {
                throw new EntityAlreadyExistsException(CatMessages.class, messageCode);
            } else {
                CatMessages catMessages = new CatMessages();
                catMessages.setMessageCode(messageCode);
                catMessages.setLanguageCode(languageCode);
                catMessages.setDescription(descriptionTranslation);
                catMessagesService.create(catMessages, currentUser, currentUser.getProvider());
            }
        }
    }

    /**
     * Retrieves a CatMessages by code.
     * 
     * @param catMessagesCode
     * @param languageCode
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public CatMessagesDto find(String catMessagesCode, String languageCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(catMessagesCode)) {
            missingParameters.add("catMessagesCode");
        }

        if (StringUtils.isBlank(languageCode)) {
            missingParameters.add("languageCode");
        }
        
        handleMissingParameters();
        

        CatMessagesDto catMessagesDto = null;

        CatMessages catMessages = catMessagesService.findByCodeAndLanguage(catMessagesCode, languageCode, provider);

        if (catMessages == null) {
            throw new EntityDoesNotExistsException(CatMessages.class, catMessagesCode);
        }

        catMessagesDto = new CatMessagesDto();
        catMessagesDto.setCatMessagesCode(catMessages.getMessageCode());
        catMessagesDto.setObjectType(catMessages.getObjectType());
        catMessagesDto.setLanguageCode(catMessages.getLanguageCode());
        catMessagesDto.setEntityCode(catMessages.getEntityCode());
        catMessagesDto.setBasicDescription(catMessages.getEntityDescription());
        catMessagesDto.setDescriptionTranslation(catMessages.getDescription());

        return catMessagesDto;
    }

    public void update(CatMessagesDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getObjectType())) {
            missingParameters.add("objectType");
        }
        if (StringUtils.isBlank(postData.getLanguageCode())) {
            missingParameters.add("languageCode");
        }
        if (StringUtils.isBlank(postData.getEntityCode())) {
            missingParameters.add("entityCode");
        }
        if (StringUtils.isBlank(postData.getDescriptionTranslation())) {
            missingParameters.add("descriptionTranslation");
        }
        
        handleMissingParameters();
        

        CatMessages catMessages = catMessagesService.findByCodeAndLanguage(postData.getCatMessagesCode(), postData.getLanguageCode(), currentUser.getProvider());

        if (catMessages == null) {
            throw new EntityDoesNotExistsException(CatMessages.class, postData.getCatMessagesCode());
        }

        catMessages.setDescription(postData.getDescriptionTranslation());

        catMessagesService.update(catMessages);
    }

    public void remove(String catMessagesCode, String languageCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(catMessagesCode)) {
            missingParameters.add("catMessagesCode");
        }

        if (StringUtils.isBlank(languageCode)) {
            missingParameters.add("languageCode");
        }
        
        handleMissingParameters();
        

        CatMessages catMessages = catMessagesService.findByCodeAndLanguage(catMessagesCode, languageCode, provider);

        if (catMessages == null) {
            throw new EntityDoesNotExistsException(CatMessages.class, catMessagesCode);
        }

        catMessagesService.remove(catMessages);
    }

    public void createOrUpdate(CatMessagesDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCatMessagesCode())) {
            missingParameters.add("catMessagesCode");
        }
        if (StringUtils.isBlank(postData.getLanguageCode())) {
            missingParameters.add("languageCode");
        }
                
        handleMissingParameters();
        

        String catMessagesCode = postData.getCatMessagesCode();
        String languageCode = postData.getLanguageCode();

        CatMessages c = catMessagesService.findByCodeAndLanguage(catMessagesCode, languageCode, currentUser.getProvider());

        if (c == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    public CatMessagesListDto list(Provider provider) {
        CatMessagesListDto catMessagesListDto = new CatMessagesListDto();
        List<CatMessages> catMessagesList = catMessagesService.list();

        if (catMessagesList != null && !catMessagesList.isEmpty()) {
            for (CatMessages cm : catMessagesList) {
                CatMessagesDto cmd = new CatMessagesDto();
                cmd.setBasicDescription(cm.getDescription());
                cmd.setCatMessagesCode(cm.getMessageCode());
                cmd.setDescriptionTranslation(cm.getEntityDescription());
                cmd.setEntityCode(cm.getEntityCode());
                cmd.setLanguageCode(cm.getLanguageCode());
                cmd.setObjectType(cm.getObjectType());
                catMessagesListDto.getCatMessage().add(cmd);
            }
        }

        return catMessagesListDto;
    }

    protected Map<String, String> getObjectTypes() {
        if (result.isEmpty()) {
            result.put("Titles and civilities", "Title_*");
            result.put("Taxes", "Tax_*");
            result.put("Invoice categories", "InvoiceCategory_*");
            result.put("Invoice subcategories", "InvoiceSubCategory_*");
            result.put("Charges", "*ChargeTemplate_*");
            result.put("Price plans", "PricePlanMatrix_*");
        }
        return result;
    }
}
