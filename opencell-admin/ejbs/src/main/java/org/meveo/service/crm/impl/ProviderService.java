/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.crm.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.keycloak.KeycloakSecurityContext;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.keycloak.client.KeycloakAdminClientService;
import org.meveo.model.crm.Provider;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.sequence.SequenceTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.ServiceSingleton;

/**
 * Provider service implementation.
 * 
 * @author Andrius Karpavicius
 * @author Edward Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.2
 * 
 */
@Stateless
public class ProviderService extends PersistenceService<Provider> {

    /**
     * The tenant registry to add or remove a new tenant.
     */
    @EJB
    private TenantRegistry providerRegistry;

    /**
     * The request information, it will be used to get current user credentials to be able to add new tenant user on keycloak.
     */
    @Inject
    private HttpServletRequest request;
    
    @Inject
    private ServiceSingleton serviceSingleton;

    /**
     * @return provider
     */
    public Provider getProvider() {

        Provider provider = getEntityManager().createNamedQuery("Provider.first", Provider.class).getResultList().get(0);

        if (provider.getCurrency() != null) {
            provider.getCurrency().getCurrencyCode();
        }
        if (provider.getCountry() != null) {
            provider.getCountry().getCountryCode();
        }
        if (provider.getLanguage() != null) {
            provider.getLanguage().getLanguageCode();
        }
        if (provider.getInvoiceConfiguration() != null) {
            provider.getInvoiceConfiguration().getDisplayBillingCycle();
        }
        provider.getPaymentMethods().size();
        return provider;
    }

    @Override
    public void create(Provider provider) throws BusinessException {
        super.create(provider);
        providerRegistry.addTenant(provider);
        createProviderUser(provider);
    }

    @Override
    public void remove(Provider provider) throws BusinessException {
        super.remove(provider);
        providerRegistry.removeTenant(provider);
    }

    @Override
    public Provider update(Provider provider) throws BusinessException {
        // Refresh appProvider application scope variable
        refreshAppProvider(provider);
        
        provider = super.update(provider);

        // clusterEventPublisher.publishEvent(provider, CrudActionEnum.update);
        return provider;
    }

    /**
     * Update appProvider's code.
     * 
     * @param newCode New code to update to
     * @throws BusinessException Business exception
     */
    public void updateProviderCode(String newCode) throws BusinessException {
        Provider provider = getProvider();
        provider.setCode(newCode);
        updateNoCheck(provider);
    }

    /**
     * Refresh appProvider request scope variable, just in case it is used in some EL expressions within the same request.
     * 
     * @param provider New provider data to refresh with
     */
    private void refreshAppProvider(Provider provider) {

        try {
            BeanUtils.copyProperties(appProvider, provider);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to update alProvider fields");
        }

        appProvider.setCurrency(provider.getCurrency() != null ? provider.getCurrency() : null);
        appProvider.setCountry(provider.getCountry() != null ? provider.getCountry() : null);
        appProvider.setLanguage(provider.getLanguage() != null ? provider.getLanguage() : null);
        appProvider.setInvoiceConfiguration(provider.getInvoiceConfiguration() != null ? provider.getInvoiceConfiguration() : null);
        appProvider.setPaymentMethods(provider.getPaymentMethods());
        appProvider.setCfValues(provider.getCfValues());
    }

    /**
     * Find Provider by code - strict match.
     * 
     * @param code Code to match
     * @return A single entity matching code
     */
    public Provider findByCode(String code) {

        if (code == null) {
            return null;
        }

        TypedQuery<Provider> query = getEntityManager().createQuery("select be from Provider be where upper(code)=:code", entityClass).setParameter("code", code.toUpperCase())
            .setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No Provider of code {} found", code);
            return null;
        }
    }

    /**
     * Create the superadmin user of a new provider.
     * 
     * @param provider the tenant information
     */
    private void createProviderUser(Provider provider) {
        KeycloakSecurityContext session = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        log.info("> addTenant > getTokenString : " + session.getTokenString());

        // Create user
        UserDto userDto = new UserDto();
        String name = (provider.getCode() + "." + "superadmin").toLowerCase();
        log.info("> addTenant > name " + name);
        userDto.setUsername(name);
        userDto.setPassword(name);
        if (!StringUtils.isBlank(provider.getEmail())) {
            userDto.setEmail(provider.getEmail());
        } else {
            userDto.setEmail(name + "@" + provider.getCode().toLowerCase() + ".com");
        }
        userDto.setRoles(Arrays.asList("CUSTOMER_CARE_USER", "superAdministrateur"));
        userDto.setExternalRoles(Arrays.asList(new RoleDto("CC_ADMIN"), new RoleDto("SUPER_ADMIN")));

        // Get services
        KeycloakAdminClientService kc = (KeycloakAdminClientService) EjbUtils.getServiceInterface(KeycloakAdminClientService.class.getSimpleName());
        try {
            kc.createUser(request, userDto, provider.getCode());
        } catch (EntityDoesNotExistsException e) {
            e.printStackTrace();
        } catch (BusinessException e) {
            e.printStackTrace();

        }
    }

    public GenericSequence getNextMandateNumber() throws BusinessException {		
		GenericSequence genericSequence = serviceSingleton.getNextSequenceNumber(SequenceTypeEnum.RUM);
		Provider provider = findById(appProvider.getId());
		provider.setRumSequence(genericSequence);
		update(provider);
		
		return genericSequence;
	}
    
    public GenericSequence getNextCustomerNumber() throws BusinessException {		
		GenericSequence genericSequence = serviceSingleton.getNextSequenceNumber(SequenceTypeEnum.CUSTOMER_NO);
		Provider provider = findById(appProvider.getId());
		provider.setCustomerNoSequence(genericSequence);
		update(provider);
		
		return genericSequence;
	}
}