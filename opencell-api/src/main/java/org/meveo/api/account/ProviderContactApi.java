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
 * @since Jun 3, 2016 1:28:17 AM
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ProviderContactApi extends BaseApi {

    @Inject
    private ProviderContactService providerContactService;
    
    @Inject
    private  CountryService countryService;

    public ProviderContact create(ProviderContactDto providerContactDto) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(providerContactDto.getCode())) {
            addGenericCodeIfAssociated(ProviderContact.class.getName(), providerContactDto);
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
        return providerContact;
    }

    public ProviderContact update(ProviderContactDto providerContactDto) throws MeveoApiException, BusinessException {
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
        return providerContact;
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

    public ProviderContact createOrUpdate(ProviderContactDto providerContactDto) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(providerContactDto.getCode())
                && providerContactService.findByCode(providerContactDto.getCode()) != null) {
            return update(providerContactDto);
        } else {
            return create(providerContactDto);
        }
    }
}
