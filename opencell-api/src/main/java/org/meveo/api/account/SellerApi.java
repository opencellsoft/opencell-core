/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.account;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.SellersDto;
import org.meveo.api.dto.SequenceDto;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.exception.*;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.InvoiceSequenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.IsoIcdService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map.Entry;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Khalid HORRI
 * @author Said Ramli
 * @lastModifiedVersion 5.3.2
 **/
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class SellerApi extends AccountEntityApi {

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

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    private IsoIcdService isoIcdService;
    
    public Seller create(SellerDto postData) throws MeveoApiException, BusinessException {
        return create(postData, true);
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

        handleMissingParameters(postData);

        if (sellerService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Seller.class, postData.getCode());
        }

        Seller seller = new Seller();
        seller = this.sellerDtoToSeller(seller, postData);

        if (StringUtils.isBlank(postData.getCode())) {
            seller.setCode(customGenericEntityCodeService.getGenericEntityCode(seller));
        }

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

        // Contact informations
        this.updateContactInformation(seller, postData.getContactInformation());
        // Address
        this.updateAddress(seller, postData.getAddress());
        // Trading Currency
        this.updateTradingCurrency(seller, postData.getCurrencyCode());
        // Trading Country
        this.updateTradingCountry(seller, postData.getCountryCode());
        // Trading Language
        this.updateTradingLanguage(seller, postData.getLanguageCode());
        // Parent seller
        this.updateParentSeller(seller, postData.getParentSeller());

        if (businessAccountModel != null) {
            seller.setBusinessAccountModel(businessAccountModel);
        }

        // populate customFields
        this.populateCustomFields(postData, checkCustomField, seller, true);

        sellerService.create(seller);
        return seller;
    }

    /**
     * Update the seller's address by the given addressDto.
     *
     * @param seller the seller
     * @param addressDto the address dto
     * @throws EntityDoesNotExistsException the entity does not exists exception
     */
    private void updateAddress(Seller seller, AddressDto addressDto) throws EntityDoesNotExistsException {
        if (addressDto != null) {
            Address address = seller.getAddress();
            if (address == null) {
                address = new Address();
                seller.setAddress(address);
            }
            final String address1 = addressDto.getAddress1();
            if (address1 != null) {
                address.setAddress1(address1);
            }
            final String address2 = addressDto.getAddress2();
            if (address2 != null) {
                address.setAddress2(address2);
            }
            final String address3 = addressDto.getAddress3();
            if (address3 != null) {
                address.setAddress3(address3);
            }
            final String address4 = addressDto.getAddress4();
            if (address4 != null) {
                address.setAddress4(address4);
            }
            final String address5 = addressDto.getAddress5();
            if (address5 != null) {
                address.setAddress5(address5);
            }
            final String city = addressDto.getCity();
            if (city != null) {
                address.setCity(city);
            }
            final String countryCode = addressDto.getCountry();
            if (countryCode != null) {
                if (isNotBlank(countryCode)) {
                    Country country = countryService.findByCode(countryCode);
                    if (country == null) {
                        throw new EntityDoesNotExistsException(Country.class, countryCode);
                    }
                    address.setCountry(country);
                } else {
                    address.setCountry(null);
                }
            }
            final String state = addressDto.getState();
            if (state != null) {
                address.setState(state);
            }
            final String zipCpde = addressDto.getZipCode();
            if (zipCpde != null) {
                address.setZipCode(zipCpde);
            }
        }
    }

    private void populateCustomFields(SellerDto postData, boolean checkCustomField, Seller seller, boolean isNewEntity) throws MeveoApiException {
        try {
            super.populateCustomFields(postData.getCustomFields(), seller, isNewEntity, checkCustomField);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    /**
     * Map the data from SellerDto to Seller
     * 
     * @param seller
     * @param postData
     * @return the seller with new data
     */
    private Seller sellerDtoToSeller(Seller seller, SellerDto postData) {

        seller.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        final String description = postData.getDescription();
        if (description != null) {
            seller.setDescription(description);
        }
        if (postData.getVatNo() != null) {
            seller.setVatNo(postData.getVatNo());
        }
        if (postData.getIsoICDCode() != null) {            
            IsoIcd isoIcd = isoIcdService.findByCode(postData.getIsoICDCode());
            if (isoIcd == null) {
                throw new EntityDoesNotExistsException(IsoIcd.class, postData.getIsoICDCode());
            }           
            seller.setIcdId(isoIcd);
        }
        if (postData.getRegistrationNo() != null) {
            seller.setRegistrationNo(postData.getRegistrationNo());
        }
        
        if (org.apache.commons.lang3.StringUtils.isEmpty(seller.getRegistrationNo()) 
            && org.apache.commons.lang3.StringUtils.isEmpty(postData.getIsoICDCode())) {
            throw new MeveoApiException("the registrationNo is blank, isoICDCode should not be empty");
        }
        
        if (postData.getLegalText() != null) {
            seller.setLegalText(postData.getLegalText());
        }
        if (postData.getLegalType() != null) {
            seller.setLegalType(postData.getLegalType());
        }
        return seller;

    }

    /**
     * Update the seller contact information by the given ContactInformationDto
     *
     * @param seller the seller
     * @param contactInformationDto the contact information dto
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private void updateContactInformation(Seller seller, ContactInformationDto contactInformationDto) {
        if (contactInformationDto != null) {
            ContactInformation contactInformation = seller.getContactInformation();
            if (contactInformation == null) {
                contactInformation = new ContactInformation();
                seller.setContactInformation(contactInformation);
            }

            final String email = contactInformationDto.getEmail();
            if (email != null) {
                contactInformation.setEmail(email);
            }

            final String phone = contactInformationDto.getPhone();
            if (phone != null) {
                contactInformation.setPhone(phone);
            }

            final String mobile = contactInformationDto.getMobile();
            if (mobile != null) {
                contactInformation.setMobile(mobile);
            }

            final String fax = contactInformationDto.getFax();
            if (fax != null) {
                contactInformation.setFax(fax);
            }
        }
    }

    public Seller update(SellerDto postData) throws MeveoApiException, BusinessException {
        return update(postData, true);
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

        // Contact informations
        this.updateContactInformation(seller, postData.getContactInformation());
        // Address
        this.updateAddress(seller, postData.getAddress());
        // Trading Currency
        this.updateTradingCurrency(seller, postData.getCurrencyCode());
        // Trading Country
        this.updateTradingCountry(seller, postData.getCountryCode());
        // Trading Language
        this.updateTradingLanguage(seller, postData.getLanguageCode());
        // Parent seller
        this.updateParentSeller(seller, postData.getParentSeller());

        if (businessAccountModel != null) {
            seller.setBusinessAccountModel(businessAccountModel);
        }

        // populate customFields
        this.populateCustomFields(postData, checkCustomField, seller, false);
        seller = sellerService.update(seller);

        return seller;
    }

    private void updateParentSeller(Seller seller, final String parentSellerCode) throws EntityDoesNotExistsException {
        if (parentSellerCode != null) {
            if (isNotBlank(parentSellerCode)) {
                Seller parentSeller = sellerService.findByCode(parentSellerCode);
                if (parentSeller == null) {
                    throw new EntityDoesNotExistsException(Seller.class, parentSellerCode);
                }

                seller.setSeller(parentSeller);
            } else {
                seller.setSeller(null);
            }

        }
    }

    private void updateTradingLanguage(Seller seller, final String languageCode) throws EntityDoesNotExistsException {
        if (languageCode != null) {
            if (isNotBlank(languageCode)) {
                TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(languageCode);
                if (tradingLanguage == null) {
                    throw new EntityDoesNotExistsException(TradingLanguage.class, languageCode);
                }

                seller.setTradingLanguage(tradingLanguage);
            } else {
                seller.setTradingLanguage(null);
            }

        }
    }

    private void updateTradingCountry(Seller seller, String countryCode) throws EntityDoesNotExistsException {
        if (countryCode != null) {
            if (isNotBlank(countryCode)) {
                TradingCountry tradingCountry = tradingCountryService.findByCode(countryCode);
                if (tradingCountry == null) {
                    throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
                }

                seller.setTradingCountry(tradingCountry);
            } else {
                seller.setTradingCountry(null);
            }
        }
    }

    private void updateTradingCurrency(Seller seller, String currencyCode) throws EntityDoesNotExistsException {
        if (currencyCode != null) {
            if (isNotBlank(currencyCode)) {
                TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currencyCode);
                if (tradingCurrency == null) {
                    throw new EntityDoesNotExistsException(TradingCurrency.class, currencyCode);
                }
                seller.setTradingCurrency(tradingCurrency);
            } else {
                seller.setTradingCurrency(null);
            }
        }
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

        Seller seller = sellerService.findByCode(sellerCode);
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

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "seller", itemPropertiesToFilter = {@FilterProperty(property = "code", entityClass = Seller.class) })
    public SellersDto list() {
        SellersDto result = new SellersDto();

        List<Seller> sellers = sellerService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
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
     * @return the seller
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public Seller createOrUpdate(SellerDto postData) throws MeveoApiException, BusinessException {
        Seller seller = sellerService.findByCode(postData.getCode());
        if (seller == null) {
            seller = create(postData);
        } else {
            seller = update(postData);
        }
        return seller;
    }
}
