package org.meveo.api.account;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.SellersDto;
import org.meveo.api.dto.SequenceDto;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.UserParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class SellerApi extends BaseApi {

    @Inject
    private SellerService sellerService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingLanguageService tradingLanguageService;
    
    @Inject
    private InvoiceTypeService invoiceTypeService;

    public void create(SellerDto postData, User currentUser) throws MeveoApiException, BusinessException {
        create(postData, currentUser, true);
    }

    public Seller create(SellerDto postData, User currentUser, boolean checkCustomField) throws MeveoApiException, BusinessException {
        return create(postData, currentUser, checkCustomField, null);
    }

    public Seller create(SellerDto postData, User currentUser, boolean checkCustomField, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (sellerService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(Seller.class, postData.getCode());
        }

        Seller seller = new Seller();
        seller.setCode(postData.getCode());
        seller.setDescription(postData.getDescription());
        seller.setProvider(provider);       
        if(postData.getInvoiceTypeSequences() != null){
        	for(Entry<String, SequenceDto> entry : postData.getInvoiceTypeSequences().entrySet() ){
        		InvoiceType invoiceType = invoiceTypeService.findByCode(entry.getKey(), currentUser.getProvider());
        		if(invoiceType == null){
        			 throw new EntityDoesNotExistsException(InvoiceType.class, entry.getKey());
        		}
        		seller.getInvoiceTypeSequence().put(invoiceType, entry.getValue().fromDto());
        	}
        }
        
        // check trading entities
        if (!StringUtils.isBlank(postData.getCurrencyCode())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode(), provider);
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrencyCode());
            }

            seller.setTradingCurrency(tradingCurrency);
        }

        if (!StringUtils.isBlank(postData.getCountryCode())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), provider);
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
            }

            seller.setTradingCountry(tradingCountry);
        }

        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguageCode(), provider);
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguageCode());
            }

            seller.setTradingLanguage(tradingLanguage);
        }

        // check parent seller
        if (!StringUtils.isBlank(postData.getParentSeller())) {
            Seller parentSeller = sellerService.findByCode(postData.getParentSeller(), provider);
            if (parentSeller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getParentSeller());
            }

            seller.setSeller(parentSeller);
        }

        if(businessAccountModel != null) {
            seller.setBusinessAccountModel(businessAccountModel);
        }

        sellerService.create(seller, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), seller, true, currentUser, checkCustomField);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        return seller;
    }

    public void update(SellerDto postData, User currentUser) throws MeveoApiException, BusinessException {
        update(postData, currentUser, true);
    }

    public Seller update(SellerDto postData, User currentUser, boolean checkCustomField) throws MeveoApiException, BusinessException {
        return update(postData, currentUser, checkCustomField, null);
    }

    public Seller update(SellerDto postData, User currentUser, boolean checkCustomField, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        Seller seller = sellerService.findByCode(postData.getCode(), provider);
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, postData.getCode());
        }

        seller.setDescription(postData.getDescription());
        if(postData.getInvoiceTypeSequences() != null){
        	for(Entry<String, SequenceDto> entry : postData.getInvoiceTypeSequences().entrySet() ){
        		InvoiceType invoiceType = invoiceTypeService.findByCode(entry.getKey(), currentUser.getProvider());
        		if(invoiceType == null){
        			 throw new EntityDoesNotExistsException(InvoiceType.class, entry.getKey());
        		}
        		
        		if(entry.getValue().getCurrentInvoiceNb().longValue() 
        				< invoiceTypeService.getMaxCurrentInvoiceNumber(currentUser.getProvider(), invoiceType.getCode()).longValue()) {
                	throw new MeveoApiException("Not able to update, check the current number");
                }
        		
        		seller.getInvoiceTypeSequence().put(invoiceType, entry.getValue().fromDto());
        	}
        }
        // check trading entities
        if (!StringUtils.isBlank(postData.getCurrencyCode())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode(), provider);
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrencyCode());
            }

            seller.setTradingCurrency(tradingCurrency);

        } else {
            seller.setTradingCurrency(null);
        }

        if (!StringUtils.isBlank(postData.getCountryCode())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), provider);
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
            }

            seller.setTradingCountry(tradingCountry);

        } else {
            seller.setTradingCountry(null);
        }

        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguageCode(), provider);
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguageCode());
            }

            seller.setTradingLanguage(tradingLanguage);

        } else {
            seller.setTradingLanguage(null);
        }

        // check parent seller
        if (!StringUtils.isBlank(postData.getParentSeller())) {
            Seller parentSeller = sellerService.findByCode(postData.getParentSeller(), provider);
            if (parentSeller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getParentSeller());
            }

            seller.setSeller(parentSeller);
        }

        if(businessAccountModel != null) {
            seller.setBusinessAccountModel(businessAccountModel);
        }

        seller = sellerService.update(seller, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), seller, false, currentUser, checkCustomField);

        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        return seller;
    }

    @SecuredBusinessEntityMethod(
			validate = @SecureMethodParameter, 
			user = @SecureMethodParameter(index = 1, parser = UserParser.class))
    public SellerDto find(String sellerCode, User currentUser) throws MeveoApiException {

    	Provider provider = currentUser.getProvider();
        if (StringUtils.isBlank(sellerCode)) {
            missingParameters.add("sellerCode");
            handleMissingParameters();
        }

        SellerDto result = new SellerDto();

        Seller seller = sellerService.findByCode(sellerCode, provider, Arrays.asList("tradingCountry", "tradingCurrency", "tradingLanguage"));
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, sellerCode);
        }

        result = new SellerDto(seller, entityToDtoConverter.getCustomFieldsDTO(seller));

        return result;
    }

    public void remove(String sellerCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(sellerCode)) {
            missingParameters.add("sellerCode");
            handleMissingParameters();
        }

        Seller seller = sellerService.findByCode(sellerCode, currentUser.getProvider());
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, sellerCode);
        }
        try {
            sellerService.remove(seller, currentUser);
            sellerService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(Seller.class, sellerCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    public SellersDto list(Provider provider) {
        SellersDto result = new SellersDto();

        List<Seller> sellers = sellerService.list(provider);
        if (sellers != null) {
            for (Seller seller : sellers) {
                result.getSeller().add(new SellerDto(seller, entityToDtoConverter.getCustomFieldsDTO(seller)));
            }
        }

        return result;
    }

    public SellerCodesResponseDto listSellerCodes(Provider provider) {
        SellerCodesResponseDto result = new SellerCodesResponseDto();

        List<Seller> sellers = sellerService.list(provider);
        if (sellers != null) {
            for (Seller seller : sellers) {
                result.getSellerCodes().add(seller.getCode());
            }
        }

        return result;
    }

    /**
     * creates or updates seller based on the seller code. If seller is not existing based on the seller code, it will be created else, will be updated.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(SellerDto postData, User currentUser) throws MeveoApiException, BusinessException {
        Seller seller = sellerService.findByCode(postData.getCode(), currentUser.getProvider());
        if (seller == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}
