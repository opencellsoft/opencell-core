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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.Country;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.TitleService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 **/
@Stateless
public class AccountEntityApi extends BaseApi {

    @Inject
    private TitleService titleService;

    @Inject
    private CountryService countryService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    public void updateAccount(AccountEntity accountEntity, AccountDto postData) throws MeveoApiException {
        updateAccount(accountEntity, postData, true);
    }

    public void updateAccount(AccountEntity accountEntity, AccountDto postData, boolean checkCustomFields) throws MeveoApiException {

        boolean isNew = accountEntity.getId() == null;
        if (isNew) {
            accountEntity.setCode(postData.getCode());
        } else if (!StringUtils.isBlank(postData.getUpdatedCode())) {
            accountEntity.setCode(postData.getUpdatedCode());
        }

        if (postData.getAddress() != null) {
            Address address = accountEntity.getAddress() == null ? new Address() : accountEntity.getAddress();

            if (postData.getAddress().getAddress1() != null) {
                address.setAddress1(StringUtils.isEmpty(postData.getAddress().getAddress1()) ? null : postData.getAddress().getAddress1());
            }
            if (postData.getAddress().getAddress2() != null) {
                address.setAddress2(StringUtils.isEmpty(postData.getAddress().getAddress2()) ? null : postData.getAddress().getAddress2());
            }
            if (postData.getAddress().getAddress3() != null) {
                address.setAddress3(StringUtils.isEmpty(postData.getAddress().getAddress3()) ? null : postData.getAddress().getAddress3());
            }
            if (postData.getAddress().getZipCode() != null) {
                address.setZipCode(StringUtils.isEmpty(postData.getAddress().getZipCode()) ? null : postData.getAddress().getZipCode());
            }
            if (postData.getAddress().getCity() != null) {
                address.setCity(StringUtils.isEmpty(postData.getAddress().getCity()) ? null : postData.getAddress().getCity());
            }

            if (postData.getAddress().getCountry() != null) {
                if (StringUtils.isBlank(postData.getAddress().getCountry())) {
                    address.setCountry(null);
                } else {
                    Country country = countryService.findByCode(postData.getAddress().getCountry());
                    if (country == null) {
                        throw new EntityDoesNotExistsException(Country.class, postData.getAddress().getCountry());
                    } else {
                        address.setCountry(country);
                    }
                }
            }

            if (postData.getAddress().getState() != null) {
                address.setState(StringUtils.isEmpty(postData.getAddress().getState()) ? null : postData.getAddress().getState());
            }

            accountEntity.setAddress(address);
        }

        if (postData.getName() != null) {

            // All name attributes are empty - remove name field alltogether
            if ((postData.getName().getTitle() != null && StringUtils.isEmpty(postData.getName().getTitle())) && (postData.getName().getFirstName() != null && StringUtils.isEmpty(postData.getName().getFirstName()))
                    && (postData.getName().getLastName() != null && StringUtils.isEmpty(postData.getName().getLastName()))) {
                accountEntity.setName(null);

            } else {
                Name name = accountEntity.getName() == null ? new Name() : accountEntity.getName();

                if (postData.getName().getFirstName() != null) {
                    name.setFirstName(StringUtils.isEmpty(postData.getName().getFirstName()) ? null : postData.getName().getFirstName());
                }
                if (postData.getName().getLastName() != null) {
                    name.setLastName(StringUtils.isEmpty(postData.getName().getLastName()) ? null : postData.getName().getLastName());
                }
                if (postData.getName().getTitle() != null) {
                    if (StringUtils.isBlank(postData.getName().getTitle())) {
                        name.setTitle(null);
                    } else {
                        Title title = titleService.findByCode(postData.getName().getTitle());
                        if (title == null) {
                            throw new EntityDoesNotExistsException(Title.class, postData.getName().getTitle());
                        } else {
                            name.setTitle(title);
                        }
                    }
                }

                accountEntity.setName(name);
            }
        }

        if (postData.getDescription() != null) {
            accountEntity.setDescription(StringUtils.isEmpty(postData.getDescription()) ? null : postData.getDescription());
        }
        if (postData.getExternalRef1() != null) {
            accountEntity.setExternalRef1(StringUtils.isEmpty(postData.getExternalRef1()) ? null : postData.getExternalRef1());
        }
        if (postData.getExternalRef2() != null) {
            accountEntity.setExternalRef2(StringUtils.isEmpty(postData.getExternalRef2()) ? null : postData.getExternalRef2());
        }
        if (postData.getJobTitle() != null) {
            accountEntity.setJobTitle(StringUtils.isEmpty(postData.getJobTitle()) ? null : postData.getJobTitle());
        }
        if (postData.getVatNo() != null) {
            accountEntity.setVatNo(StringUtils.isEmpty(postData.getVatNo()) ? null : postData.getVatNo());
        }
        if (postData.getRegistrationNo() != null) {
            accountEntity.setRegistrationNo(StringUtils.isEmpty(postData.getRegistrationNo()) ? null : postData.getRegistrationNo());
        }

        if (postData.getContactInformation() != null) {
            if (accountEntity.getContactInformation() == null) {
                accountEntity.setContactInformation(new ContactInformation());
            }
            if (postData.getContactInformation().getEmail() != null) {
                accountEntity.getContactInformation().setEmail(StringUtils.isEmpty(postData.getContactInformation().getEmail()) ? null : postData.getContactInformation().getEmail());
            }
            if (postData.getContactInformation().getPhone() != null) {
                accountEntity.getContactInformation().setPhone(StringUtils.isEmpty(postData.getContactInformation().getPhone()) ? null : postData.getContactInformation().getPhone());
            }
            if (postData.getContactInformation().getMobile() != null) {
                accountEntity.getContactInformation().setMobile(StringUtils.isEmpty(postData.getContactInformation().getMobile()) ? null : postData.getContactInformation().getMobile());
            }
            if (postData.getContactInformation().getFax() != null) {
                accountEntity.getContactInformation().setFax(StringUtils.isEmpty(postData.getContactInformation().getFax()) ? null : postData.getContactInformation().getFax());
            }

        }
        setMinimumAmountElSubscription(postData, accountEntity);

    }

    private void setMinimumAmountElSubscription(AccountDto postData, AccountEntity accountEntity) {

        if (postData.getMinimumAmountEl() != null) {
            accountEntity.setMinimumAmountEl(StringUtils.isEmpty(postData.getMinimumAmountEl()) ? null : postData.getMinimumAmountEl());
        }
        if (postData.getMinimumLabelEl() != null) {
            accountEntity.setMinimumLabelEl(StringUtils.isEmpty(postData.getMinimumLabelEl()) ? null : postData.getMinimumLabelEl());
        }
        if (postData.getMinimumAmountElSpark() != null) {
            accountEntity.setMinimumAmountElSpark(StringUtils.isEmpty(postData.getMinimumAmountElSpark()) ? null : postData.getMinimumAmountElSpark());
        }
        if (postData.getMinimumLabelElSpark() != null) {
            accountEntity.setMinimumLabelElSpark(StringUtils.isEmpty(postData.getMinimumLabelElSpark()) ? null : postData.getMinimumLabelElSpark());
        }

        if (postData.getMinimumChargeTemplate() != null) {
            if (StringUtils.isBlank(postData.getMinimumChargeTemplate())) {
                accountEntity.setMinimumChargeTemplate(null);
            } else {
                OneShotChargeTemplate minimumChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getMinimumChargeTemplate());
                if (minimumChargeTemplate == null) {
                    throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getMinimumChargeTemplate());
                } else {
                    accountEntity.setMinimumChargeTemplate(minimumChargeTemplate);
                }
            }
        }
    }
}