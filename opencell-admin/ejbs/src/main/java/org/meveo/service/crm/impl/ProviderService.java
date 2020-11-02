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
package org.meveo.service.crm.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
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
import org.meveo.cache.TenantCacheContainerProvider;
import org.meveo.commons.utils.ParamBeanFactory;
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

    @Inject
    private TenantCacheContainerProvider tenantCacheContainerProvider;

    @Inject
    private KeycloakAdminClientService kcService;

    static boolean useTenantCache = true;

    @PostConstruct
    private void init() {
        useTenantCache = Boolean.parseBoolean(ParamBeanFactory.getAppScopeInstance().getProperty("cache.cacheTenant", "true"));
    }

    /**
     * Get current provider retrieved from Cache or database. Populates cache if not found in cache.
     * 
     * @return Current provider retrieved from Cache or database
     */
    public Provider getProvider() {
        Provider provider = null;
        if (useTenantCache) {
            provider = tenantCacheContainerProvider.getTenant();
        }
        if (provider == null) {
            provider = getProviderNoCache();
            tenantCacheContainerProvider.addUpdateTenant(provider);
        }
        return provider;
    }

    /**
     * Get current provider retrieved from Database. Does not use cache.
     * 
     * @return Current provider retrieved from Database
     */
    public Provider getProviderNoCache() {

        Provider provider = getEntityManager().find(Provider.class, Provider.CURRENT_PROVIDER_ID);
        getEntityManager().refresh(provider);
        if (provider.getInvoiceConfiguration() != null) {
            provider.getInvoiceConfiguration().isDisplayBillingCycle();
        }
        if (provider.getGdprConfiguration() != null) {
            provider.getGdprConfiguration().getInvoiceLife();
        }
        return provider;
    }

    @Override
    public void create(Provider provider) throws BusinessException {
        super.create(provider);
        createProviderUserInKC(provider);
        providerRegistry.addTenant(provider);
    }

    @Override
    public void remove(Provider provider) throws BusinessException {
        super.remove(provider);
        providerRegistry.removeTenant(provider);
    }

    @Override
    public Provider update(Provider provider) throws BusinessException {

        provider = super.update(provider);

        // Refresh appProvider request scope variable if applicable
        if (appProvider.getId().equals(provider.getId())) {
            refreshAppProvider(provider);
        }

        return provider;
    }

    /**
     * Ensure that provider code in secondary provider schema matches the tenant/provider code as it was listed in main provider's secondary tenant/provider record.
     * 
     * Note: This is to ensure db level data correctness only to cover cases when database schema is initialized with a default data - thus a default (DEMO) provider code
     * 
     * @param newCode New code to update to
     * @throws BusinessException Business exception
     */
    public void updateSecondaryTenantsCode(String newCode) throws BusinessException {
        Provider provider = getProviderNoCache();
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
            log.error("Failed to update appProvider fields");
        }

        appProvider.setCurrency(provider.getCurrency() != null ? provider.getCurrency() : null);
        appProvider.setCountry(provider.getCountry() != null ? provider.getCountry() : null);
        appProvider.setLanguage(provider.getLanguage() != null ? provider.getLanguage() : null);
        appProvider.setInvoiceConfiguration(provider.getInvoiceConfiguration() != null ? provider.getInvoiceConfiguration() : null);
        appProvider.setPaymentMethods(provider.getPaymentMethods());
        appProvider.setCfValues(provider.getCfValues());

        tenantCacheContainerProvider.addUpdateTenant(provider);
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

        TypedQuery<Provider> query = getEntityManager().createQuery("select be from Provider be where lower(code)=:code", entityClass).setParameter("code", code.toLowerCase())
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
     * @throws BusinessException Failed to create a user
     */
    private void createProviderUserInKC(Provider provider) throws BusinessException {
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

        try {
            kcService.createUser(request, userDto, provider.getCode());
        } catch (BusinessException e) {
            log.error("Failed to create a user in Keycloak", e);
            throw e;
        }
    }

    public GenericSequence getNextMandateNumber() throws BusinessException {
        GenericSequence genericSequence = serviceSingleton.getNextSequenceNumber(SequenceTypeEnum.RUM);      
        return genericSequence;
    }

    public GenericSequence getNextCustomerNumber() throws BusinessException {
        GenericSequence genericSequence = serviceSingleton.getNextSequenceNumber(SequenceTypeEnum.CUSTOMER_NO);       
        return genericSequence;
    }
    
    public void updateCustomerNumberSequence(GenericSequence genericSequence) throws BusinessException {
        serviceSingleton.updateCustomerNumberSequence(genericSequence);       
    }
}