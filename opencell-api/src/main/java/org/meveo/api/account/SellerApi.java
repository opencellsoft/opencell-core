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
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSequenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Khalid HORRI
 * @lastModifiedVersion 5.3
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

    @Inject
    private InvoiceSequenceService invoiceSequenceService;

    @Inject
    private CountryService countryService;

    public void create(SellerDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public Seller create(SellerDto postData, boolean checkCustomField) throws MeveoApiException, BusinessException {
        return create(postData, checkCustomField, null);
    }

    /**
     * Create Seller v5.0: Added ContactInformation and Address
     * 
     * @param postData postData SellerDto
     * @param checkCustomField checkCustomField
     * @param businessAccountModel businessAccountModel
     * @return Seller created seller
     * @throws BusinessException business exception
     * @throws MeveoApiException MeveoApi exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public Seller create(SellerDto postData, boolean checkCustomField, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        if (sellerService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Seller.class, postData.getCode());
        }


        Seller seller = new Seller();
        seller = this.sellerDtoToSeller(seller, postData);
        if (postData.getInvoiceTypeSequences() != null) {
            for (Entry<String, SequenceDto> entry : postData.getInvoiceTypeSequences().entrySet()) {
                InvoiceType invoiceType = invoiceTypeService.findByCode(entry.getKey());
                if (invoiceType == null) {
                    throw new EntityDoesNotExistsException(InvoiceType.class, entry.getKey());
                }
                
                if (StringUtils.isBlank(entry.getValue().getInvoiceSequenceCode())) {
                	// v5.2 : code for API backward compatibility call, invoice sequence code must be mandatory in future versions
                	InvoiceSequence invoiceSequenceInvoiceTypeSeller = entry.getValue().fromDto();
                    invoiceSequenceInvoiceTypeSeller.setCode(invoiceType.getCode() + "_" + seller.getCode());
                    invoiceSequenceService.create(invoiceSequenceInvoiceTypeSeller);
                    seller.getInvoiceTypeSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, invoiceSequenceInvoiceTypeSeller, entry.getValue().getPrefixEL()));
                } else {
                    InvoiceSequence invoiceSequenceInvoiceTypeSeller = invoiceSequenceService.findByCode(entry.getValue().getInvoiceSequenceCode());
                    if (invoiceSequenceInvoiceTypeSeller == null) {
                        throw new EntityDoesNotExistsException(InvoiceTypeSellerSequence.class, entry.getValue().getInvoiceSequenceCode());
                    }
                    seller.getInvoiceTypeSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, invoiceSequenceInvoiceTypeSeller, entry.getValue().getPrefixEL()));
                }
            }
        }

        if (postData.getContactInformation() != null) {
            seller.setContactInformation(toContactInformation(postData.getContactInformation()));
        }

        if (postData.getAddress() != null) {
            seller.setAddress(toAddress(postData.getAddress()));
        }

        // check trading entities
        if (!StringUtils.isBlank(postData.getCurrencyCode())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrencyCode());
            }

            seller.setTradingCurrency(tradingCurrency);
        }

        if (!StringUtils.isBlank(postData.getCountryCode())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
            }

            seller.setTradingCountry(tradingCountry);
        }

        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguageCode());
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguageCode());
            }

            seller.setTradingLanguage(tradingLanguage);
        }

        // check parent seller
        if (!StringUtils.isBlank(postData.getParentSeller())) {
            Seller parentSeller = sellerService.findByCode(postData.getParentSeller());
            if (parentSeller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getParentSeller());
            }

            seller.setSeller(parentSeller);
        }

        if (businessAccountModel != null) {
            seller.setBusinessAccountModel(businessAccountModel);
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), seller, true, checkCustomField);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        sellerService.create(seller);

        return seller;
    }


    /**
     *  Map the data from SellerDto to Seller
     * @param seller
     * @param postData
     * @return the seller with new data
     */
    private Seller sellerDtoToSeller(Seller seller, SellerDto postData) {

        seller.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        seller.setDescription(postData.getDescription());
        if (!StringUtils.isBlank(postData.getVatNo())) {
            seller.setVatNo(postData.getVatNo());
        }
        if (!StringUtils.isBlank(postData.getRegistrationNo())) {
            seller.setRegistrationNo(postData.getRegistrationNo());
        }
        if (!StringUtils.isBlank(postData.getLegalText())) {
            seller.setLegalText(postData.getLegalText());
        }
        if (!StringUtils.isBlank(postData.getLegalType())) {
            seller.setLegalType(postData.getLegalType());
        }
        return seller;

    }

    /**
     * ContactInformationDto to ContactInformation
     * 
     * @param ContactInformationDto contactInformationDto
     * @return ContactInformation contactInformation
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private ContactInformation toContactInformation(ContactInformationDto contactInformationDto) {
        ContactInformation contactInformation = new ContactInformation();
        contactInformation.setEmail(contactInformationDto.getEmail());
        contactInformation.setPhone(contactInformationDto.getPhone());
        contactInformation.setMobile(contactInformationDto.getMobile());
        contactInformation.setFax(contactInformationDto.getFax());
        return contactInformation;
    }

    /**
     * AddressDto to Address
     * 
     * @param AddressDto addressDto
     * @return Address address
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private Address toAddress(AddressDto addressDto) {
        Address address = new Address();
        address.setAddress1(addressDto.getAddress1());
        address.setAddress2(addressDto.getAddress2());
        address.setAddress3(addressDto.getAddress3());
        address.setCity(addressDto.getCity());
        if (!StringUtils.isBlank(addressDto.getCountry())) {
            address.setCountry(countryService.findByCode(addressDto.getCountry()));
        }
        address.setState(addressDto.getState());
        address.setZipCode(addressDto.getZipCode());
        return address;
    }

    public void update(SellerDto postData) throws MeveoApiException, BusinessException {
        update(postData, true);
    }

    public Seller update(SellerDto postData, boolean checkCustomField) throws MeveoApiException, BusinessException {
        return update(postData, checkCustomField, null);
    }

    /**
     * Update Seller v5.0: Added ContactInformation and Address
     * 
     * @param postData postData Seller Dto
     * @param checkCustomField checkCustomField
     * @param businessAccountModel businessAccountModel
     * @return Seller created seller
     * @throws BusinessException business exception
     * @throws MeveoApiException MeveoApi exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public Seller update(SellerDto postData, boolean checkCustomField, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        Seller seller = sellerService.findByCode(postData.getCode());
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, postData.getCode());
        }
        seller = this.sellerDtoToSeller(seller, postData);
        if (postData.getInvoiceTypeSequences() != null) {
            for (Entry<String, SequenceDto> entry : postData.getInvoiceTypeSequences().entrySet()) {
                InvoiceType invoiceType = invoiceTypeService.findByCode(entry.getKey());
                if (invoiceType == null) {
                    throw new EntityDoesNotExistsException(InvoiceType.class, entry.getKey());
                }

                if (seller.isContainsInvoiceTypeSequence(invoiceType)) {
                    InvoiceSequence invoiceSequenceInvoiceTypeSeller = seller.getInvoiceTypeSequenceByType(invoiceType).getInvoiceSequence();
                    if (entry.getValue().getCurrentInvoiceNb() != null) {
                        if (entry.getValue().getCurrentInvoiceNb().longValue() < invoiceSequenceService.getMaxCurrentInvoiceNumber(invoiceSequenceInvoiceTypeSeller.getCode())
                            .longValue()) {
                            throw new MeveoApiException("Not able to update, check the current number");
                        }
                    }
                    invoiceSequenceInvoiceTypeSeller = entry.getValue().updateFromDto(invoiceSequenceInvoiceTypeSeller);
                    invoiceSequenceService.update(invoiceSequenceInvoiceTypeSeller);
                    seller.getInvoiceTypeSequenceByType(invoiceType).setInvoiceSequence(invoiceSequenceInvoiceTypeSeller);
                    seller.getInvoiceTypeSequenceByType(invoiceType).setPrefixEL(entry.getValue().getPrefixEL());
                } else {
                    InvoiceSequence invoiceSequenceInvoiceTypeSeller = entry.getValue().fromDto();
                    invoiceSequenceInvoiceTypeSeller.setCode(invoiceType.getCode() + "_" + seller.getCode());
                    invoiceSequenceService.create(invoiceSequenceInvoiceTypeSeller);
                    seller.getInvoiceTypeSequence().add(new InvoiceTypeSellerSequence(invoiceType, seller, invoiceSequenceInvoiceTypeSeller, entry.getValue().getPrefixEL()));
                }
            }
        }

        if (postData.getContactInformation() != null) {
            seller.setContactInformation(toContactInformation(postData.getContactInformation()));
        }

        if (postData.getAddress() != null) {
            seller.setAddress(toAddress(postData.getAddress()));
        }

        // check trading entities
        if (!StringUtils.isBlank(postData.getCurrencyCode())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrencyCode());
            }

            seller.setTradingCurrency(tradingCurrency);

        } else {
            seller.setTradingCurrency(null);
        }

        if (!StringUtils.isBlank(postData.getCountryCode())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
            }

            seller.setTradingCountry(tradingCountry);

        } else {
            seller.setTradingCountry(null);
        }

        if (!StringUtils.isBlank(postData.getLanguageCode())) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguageCode());
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguageCode());
            }

            seller.setTradingLanguage(tradingLanguage);

        } else {
            seller.setTradingLanguage(null);
        }

        // check parent seller
        if (!StringUtils.isBlank(postData.getParentSeller())) {
            Seller parentSeller = sellerService.findByCode(postData.getParentSeller());
            if (parentSeller == null) {
                throw new EntityDoesNotExistsException(Seller.class, postData.getParentSeller());
            }

            seller.setSeller(parentSeller);
        }

        if (businessAccountModel != null) {
            seller.setBusinessAccountModel(businessAccountModel);
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), seller, false, checkCustomField);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        seller = sellerService.update(seller);

        return seller;
    }

    /**
     * Retrieve seller information by its code
     *
     * @param sellerCode Seller's code
     * @return Seller information
     * @throws MeveoApiException
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Seller.class))
    public SellerDto find(String sellerCode) throws MeveoApiException {
        return find(sellerCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    /**
     * Retrieve seller information by its code
     *
     * @param sellerCode Seller's code
     * @param inheritCF Should inherited custom field values be retrieved
     * @return Seller information
     * @throws MeveoApiException
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Seller.class))
    public SellerDto find(String sellerCode, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (StringUtils.isBlank(sellerCode)) {
            missingParameters.add("sellerCode");
        }
        handleMissingParameters();

        SellerDto result = new SellerDto();

        Seller seller = sellerService.findByCode(sellerCode, Arrays.asList("tradingCountry", "tradingCurrency", "tradingLanguage"));
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, sellerCode);
        }

        result = new SellerDto(seller, entityToDtoConverter.getCustomFieldsDTO(seller, inheritCF));

        return result;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = Seller.class))
    public void remove(String sellerCode) throws MeveoApiException {

        if (StringUtils.isBlank(sellerCode)) {
            missingParameters.add("sellerCode");
            handleMissingParameters();
        }

        Seller seller = sellerService.findByCode(sellerCode);
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, sellerCode);
        }
        try {
            sellerService.remove(seller);
            sellerService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(Seller.class, sellerCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    public SellersDto list() {
        SellersDto result = new SellersDto();

        List<Seller> sellers = sellerService.list();
        if (sellers != null) {
            for (Seller seller : sellers) {
                result.getSeller().add(new SellerDto(seller, entityToDtoConverter.getCustomFieldsDTO(seller, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        return result;
    }

    public SellerCodesResponseDto listSellerCodes() {
        SellerCodesResponseDto result = new SellerCodesResponseDto();

        List<Seller> sellers = sellerService.list();
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
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(SellerDto postData) throws MeveoApiException, BusinessException {
        Seller seller = sellerService.findByCode(postData.getCode());
        if (seller == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
}
