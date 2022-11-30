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

package org.meveo.api.tunnel;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.tunnel.TunnelCustomizationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.tunnel.ElectronicSignature;
import org.meveo.model.tunnel.Theme;
import org.meveo.model.tunnel.TunnelCustomization;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.tunnel.ElectronicSignatureService;
import org.meveo.service.tunnel.ThemeService;
import org.meveo.service.tunnel.TunnelCustomizationService;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Ilham CHAFIK
 */
@Stateless
public class TunnelCustomizationApi extends BaseCrudApi<TunnelCustomization, TunnelCustomizationDto> {

    @Inject
    private TunnelCustomizationService tunnelCustomizationService;

    @Inject
    private ThemeService themeService;

    @Inject
    private ElectronicSignatureService signatureService;

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private CustomerCategoryService customerCategoryService;

    @Override
    public TunnelCustomization create(TunnelCustomizationDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(TunnelCustomization.class.getName(), postData);
        }

        if (tunnelCustomizationService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(TunnelCustomization.class, postData.getCode());
        }

        TunnelCustomization entity = new TunnelCustomization();

        dtoToEntity(postData, entity);
        tunnelCustomizationService.create(entity);

        return entity;
    }

    @Override
    public TunnelCustomization update(TunnelCustomizationDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        TunnelCustomization tunnelCustomization = tunnelCustomizationService.findByCode(postData.getCode());
        if (tunnelCustomization == null) {
            throw new EntityDoesNotExistsException(TunnelCustomization.class, postData.getCode());
        }

        dtoToEntity(postData, tunnelCustomization);

        tunnelCustomizationService.update(tunnelCustomization);

        return tunnelCustomization;
    }

    /**
     * Populate entity with fields from DTO entity
     *
     * @param dto DTO entity object to populate from
     * @param entity Entity to populate
     **/
    private void dtoToEntity(TunnelCustomizationDto dto, TunnelCustomization entity) {
        entity.setCode(dto.getCode());
        if(dto.getRgpd() != null) {
            entity.setRgpd(convertMultiLanguageToMapOfValues(dto.getRgpd(), null));
        }
        if (dto.getTermsAndConditions() != null) {
            entity.setTermsAndConditions(convertMultiLanguageToMapOfValues(dto.getTermsAndConditions(), null));
        }
        if (dto.getOrderValidationMsg() != null) {
            entity.setOrderValidationMsg(convertMultiLanguageToMapOfValues(dto.getOrderValidationMsg(), null));
        }
        if (dto.getSignatureMsg() != null) {
            entity.setSignatureMsg(convertMultiLanguageToMapOfValues(dto.getSignatureMsg(), null));
        }
        if (dto.getAnalytics() != null) {
            entity.setAnalytics(dto.getAnalytics());
        }
        if (dto.getPaymentMethods() != null) {
            entity.setPaymentMethods(dto.getPaymentMethods());
        }
        if (dto.getContractActive() != null) {
            entity.setContractActive(dto.getContractActive());
        }
        if (dto.getMandateContract() != null) {
            entity.setMandateContract(dto.getMandateContract());
        }
        if (dto.getContactMethods() != null) {
            entity.setContactMethods(dto.getContactMethods());
        }
        if (dto.getBillingCycleCode() != null) {
            BillingCycle billingCycle = billingCycleService.findByCode(dto.getBillingCycleCode());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, dto.getBillingCycleCode());
            }
            entity.setBillingCycle(billingCycle);
        }
        if (dto.getCustomerCategoryCode() != null) {
            CustomerCategory customerCategory = customerCategoryService.findByCode(dto.getCustomerCategoryCode());
            if (customerCategory == null) {
                throw new EntityDoesNotExistsException(CustomerCategory.class, dto.getCustomerCategoryCode());
            }
            entity.setCustomerCategory(customerCategory);
        }
        if (dto.getThemeCode() != null) {
            Theme theme = themeService.findByCode(dto.getThemeCode());
            if (theme == null) {
                throw new EntityDoesNotExistsException(Theme.class, dto.getThemeCode());
            }
            entity.setTheme(theme);
        }
        if (dto.getSignatureActive() != null) {
            entity.setSignatureActive(dto.getSignatureActive());
        }
        if (dto.getElectronicSignatureCode() != null) {
            ElectronicSignature signature = signatureService.findByCode(dto.getElectronicSignatureCode());
            if (signature == null) {
                throw new EntityDoesNotExistsException(ElectronicSignature.class, dto.getElectronicSignatureCode());
            }
            entity.setElectronicSignature(signature);
        }
    }
}
