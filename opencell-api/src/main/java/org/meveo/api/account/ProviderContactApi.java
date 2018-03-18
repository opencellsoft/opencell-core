package org.meveo.api.account;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.shared.Address;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.crm.impl.ProviderContactService;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 * @lastModifiedVersion 5.0
 * @since Jun 3, 2016 1:28:17 AM
 *
 */
@Stateless
public class ProviderContactApi extends BaseApi {

    @Inject
    private ProviderContactService providerContactService;
    
    @Inject
    private  CountryService countryService;

    public void create(ProviderContactDto providerContactDto) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(providerContactDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(providerContactDto.getDescription())) {
            missingParameters.add("description");
        }
        handleMissingParameters();

        if (StringUtils.isBlank(providerContactDto.getEmail()) && StringUtils.isBlank(providerContactDto.getGenericMail()) && StringUtils.isBlank(providerContactDto.getPhone())
                && StringUtils.isBlank(providerContactDto.getMobile())) {
            throw new MeveoApiException("At least 1 of the field in Contact Information tab is required [email, genericEmail, phone, mobile].");
        }

        ProviderContact existedProviderContact = providerContactService.findByCode(providerContactDto.getCode());
        if (existedProviderContact != null) {
            throw new EntityAlreadyExistsException(ProviderContact.class, providerContactDto.getCode());
        }

        ProviderContact providerContact = new ProviderContact();
        providerContact.setCode(providerContactDto.getCode());
        providerContact.setDescription(providerContactDto.getDescription());
        providerContact.setFirstName(providerContactDto.getFirstName());
        providerContact.setLastName(providerContactDto.getLastName());
        providerContact.setEmail(providerContactDto.getEmail());
        providerContact.setPhone(providerContactDto.getPhone());
        providerContact.setMobile(providerContactDto.getMobile());
        providerContact.setFax(providerContactDto.getFax());
        providerContact.setGenericMail(providerContactDto.getGenericMail());
        if (providerContactDto.getAddressDto() != null) {
            if (providerContact.getAddress() == null) {
                providerContact.setAddress(new Address());
            }
            Address address = providerContact.getAddress();
            AddressDto addressDto = providerContactDto.getAddressDto();
            address.setAddress1(addressDto.getAddress1());
            address.setAddress2(addressDto.getAddress2());
            address.setAddress3(addressDto.getAddress3());
            address.setZipCode(addressDto.getZipCode());
            address.setCity(addressDto.getCity());
            address.setCountry(countryService.findByCode(addressDto.getCountry()));
            address.setState(addressDto.getState());
        }
        providerContactService.create(providerContact);
    }

    public void update(ProviderContactDto providerContactDto) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(providerContactDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(providerContactDto.getDescription())) {
            missingParameters.add("description");
        }
        handleMissingParameters();

        if (StringUtils.isBlank(providerContactDto.getEmail()) && StringUtils.isBlank(providerContactDto.getGenericMail()) && StringUtils.isBlank(providerContactDto.getPhone())
                && StringUtils.isBlank(providerContactDto.getMobile())) {
            throw new MeveoApiException("At least 1 of the field in Contact Information tab is required [email, genericEmail, phone, mobile].");
        }

        ProviderContact providerContact = providerContactService.findByCode(providerContactDto.getCode());
        if (providerContact == null) {
            throw new EntityDoesNotExistsException(ProviderContact.class, providerContactDto.getCode());
        }
        providerContact.setDescription(providerContactDto.getDescription());
        providerContact.setFirstName(providerContactDto.getFirstName());
        providerContact.setLastName(providerContactDto.getLastName());
        providerContact.setEmail(providerContactDto.getEmail());
        providerContact.setPhone(providerContactDto.getPhone());
        providerContact.setMobile(providerContactDto.getMobile());
        providerContact.setFax(providerContactDto.getFax());
        providerContact.setGenericMail(providerContactDto.getGenericMail());

        if (providerContactDto.getAddressDto() != null) {
            if (providerContact.getAddress() == null) {
                providerContact.setAddress(new Address());
            }
            Address address = providerContact.getAddress();
            AddressDto addressDto = providerContactDto.getAddressDto();
            address.setAddress1(addressDto.getAddress1());
            address.setAddress2(addressDto.getAddress2());
            address.setAddress3(addressDto.getAddress3());
            address.setZipCode(addressDto.getZipCode());
            address.setCity(addressDto.getCity());
            address.setCountry(countryService.findByCode(addressDto.getCountry()));
            address.setState(addressDto.getState());
        }
        providerContactService.update(providerContact);
    }

    public ProviderContactDto find(String providerContactCode) throws MeveoApiException {
        if (StringUtils.isBlank(providerContactCode)) {
            missingParameters.add("providerContactCode");
        }
        handleMissingParameters();
        ProviderContact providerContact = providerContactService.findByCode(providerContactCode);
        if (providerContact == null) {
            throw new EntityDoesNotExistsException(ProviderContact.class, providerContactCode);
        }
        return new ProviderContactDto(providerContact);
    }

    public void remove(String providerContactCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(providerContactCode)) {
            missingParameters.add("providerContactCode");
            handleMissingParameters();
        }
        ProviderContact providerContact = providerContactService.findByCode(providerContactCode);
        if (providerContact == null) {
            throw new EntityDoesNotExistsException(ProviderContact.class, providerContactCode);
        }
        providerContactService.remove(providerContact);

    }

    public List<ProviderContactDto> list() throws MeveoApiException {
        List<ProviderContactDto> result = new ArrayList<ProviderContactDto>();
        List<ProviderContact> providerContacts = providerContactService.list();
        if (providerContacts != null) {
            for (ProviderContact providerContact : providerContacts) {
                result.add(new ProviderContactDto(providerContact));
            }
        }
        return result;
    }

    public void createOrUpdate(ProviderContactDto providerContactDto) throws MeveoApiException, BusinessException {
        ProviderContact providerContact = providerContactService.findByCode(providerContactDto.getCode());
        if (providerContact == null) {
            create(providerContactDto);
        } else {
            update(providerContactDto);
        }
    }
}
