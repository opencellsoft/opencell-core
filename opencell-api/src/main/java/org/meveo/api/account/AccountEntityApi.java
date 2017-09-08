package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.Country;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.catalog.impl.TitleService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountEntityApi extends BaseApi {


    @Inject
    private TitleService titleService;
    
    @Inject
    private CountryService countryService;

    public void populate(AccountDto postData, AccountEntity accountEntity) throws MeveoApiException {
        Address address = new Address();
        if (postData.getAddress() != null) {
            address.setAddress1(postData.getAddress().getAddress1());
            address.setAddress2(postData.getAddress().getAddress2());
            address.setAddress3(postData.getAddress().getAddress3());
            address.setZipCode(postData.getAddress().getZipCode());
            address.setCity(postData.getAddress().getCity());
            if(!StringUtils.isBlank(postData.getAddress().getCountry())){
            	Country country=countryService.findByCode(postData.getAddress().getCountry());
                address.setCountry(country!=null?country.getDescriptionEn():postData.getAddress().getCountry());
                
            }
            address.setState(postData.getAddress().getState());
        }

        Name name = new Name();
        if (postData.getName() != null) {
            name.setFirstName(postData.getName().getFirstName());
            name.setLastName(postData.getName().getLastName());
            if (!StringUtils.isBlank(postData.getName().getTitle())) {
                Title title = titleService.findByCode(postData.getName().getTitle());
                if (title == null) {
                    throw new EntityDoesNotExistsException(Title.class, postData.getName().getTitle());
                } else {
                    name.setTitle(title);
                }
            }
        }
        accountEntity.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());      
        accountEntity.setDescription(postData.getDescription());
        accountEntity.setExternalRef1(postData.getExternalRef1());
        accountEntity.setExternalRef2(postData.getExternalRef2());
        accountEntity.setAddress(address);
        accountEntity.setName(name);

    }

    public void updateAccount(AccountEntity accountEntity, AccountDto postData) throws MeveoApiException {
        updateAccount(accountEntity, postData, true);
    }

    public void updateAccount(AccountEntity accountEntity, AccountDto postData, boolean checkCustomFields) throws MeveoApiException {
        
        if (postData.getAddress() != null) {
            Address address = accountEntity.getAddress() == null ? new Address() : accountEntity.getAddress();
            
            if (!StringUtils.isBlank(postData.getAddress().getAddress1())) {
                address.setAddress1(postData.getAddress().getAddress1());
            }
            if (!StringUtils.isBlank(postData.getAddress().getAddress2())) {
                address.setAddress2(postData.getAddress().getAddress2());
            }
            if (!StringUtils.isBlank(postData.getAddress().getAddress3())) {
                address.setAddress3(postData.getAddress().getAddress3());
            }
            if (!StringUtils.isBlank(postData.getAddress().getZipCode())) {
                address.setZipCode(postData.getAddress().getZipCode());
            }
            if (!StringUtils.isBlank(postData.getAddress().getCity())) {
                address.setCity(postData.getAddress().getCity());
            }
            if (!StringUtils.isBlank(postData.getAddress().getCountry())) {
            	Country country=countryService.findByCode(postData.getAddress().getCountry());
                address.setCountry(country!=null?country.getDescriptionEn():postData.getAddress().getCountry());
            }
            if (!StringUtils.isBlank(postData.getAddress().getState())) {
                address.setState(postData.getAddress().getState());
            }

            accountEntity.setAddress(address);
        }

        if (postData.getName() != null) {
            Name name = accountEntity.getName() == null ? new Name() : accountEntity.getName();

            if (!StringUtils.isBlank(postData.getName().getFirstName())) {
                name.setFirstName(postData.getName().getFirstName());
            }
            if (!StringUtils.isBlank(postData.getName().getLastName())) {
                name.setLastName(postData.getName().getLastName());
            }
            if (!StringUtils.isBlank(postData.getName().getTitle())) {
                Title title = titleService.findByCode(postData.getName().getTitle());
                if (title == null) {
                    throw new EntityDoesNotExistsException(Title.class, postData.getName().getTitle());
                } else {
                    name.setTitle(title);
                }
            }

            accountEntity.setName(name);
        }
        
        accountEntity.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        if (!StringUtils.isBlank(postData.getDescription())) {
            accountEntity.setDescription(postData.getDescription());
        }
        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            accountEntity.setExternalRef1(postData.getExternalRef1());
        }
        if (!StringUtils.isBlank(postData.getExternalRef2())) {
            accountEntity.setExternalRef2(postData.getExternalRef2());
        }

    }
}