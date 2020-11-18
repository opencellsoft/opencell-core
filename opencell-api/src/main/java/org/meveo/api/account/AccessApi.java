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

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccessesDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.mediation.Access;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.medina.impl.AccessService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccessApi extends BaseApi {

    @Inject
    private AccessService accessService;

    @Inject
    private SubscriptionService subscriptionService;

    public Access create(AccessDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getSubscription() == null) {
            missingParameters.add("subscription");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscription(), postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription(), postData.getSubscriptionValidityDate());
        }

        Access access = new Access();
        access.setStartDate(postData.getStartDate());
        access.setEndDate(postData.getEndDate());
        access.setAccessUserId(postData.getCode());
        access.setSubscription(subscription);
        if (postData.isDisabled() != null) {
            access.setDisabled(postData.isDisabled());
        }

        if (accessService.isDuplicateAndOverlaps(access)) {
            throw new MeveoApiException(MeveoApiErrorCodeEnum.DUPLICATE_ACCESS, "Duplicate subscription / access point pair.");
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), access, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        accessService.create(access);

        return access;

    }

    public Access update(AccessDto postData) throws MeveoApiException, BusinessException {

        if (postData.getCode() == null) {
            missingParameters.add("code");
        }
        if (postData.getSubscription() == null) {
            missingParameters.add("subscription");
        }
        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(postData.getSubscription(), postData.getSubscriptionValidityDate());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        Access access = accessService.findByUserIdAndSubscription(postData.getCode(), subscription, postData.getStartDate(), postData.getEndDate());
        if (access == null) {
            throw new EntityDoesNotExistsException(Access.class, postData.getCode());
        }

        access.setStartDate(postData.getStartDate());
        access.setEndDate(postData.getEndDate());

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), access, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        access = accessService.update(access);

        return access;
    }

    public AccessDto find(String accessCode, String subscriptionCode, Date subscriptionValidityDate, Date startDate, Date endDate, Date usageDate) throws MeveoApiException {

        if (StringUtils.isBlank(accessCode)) {
            missingParameters.add("accessCode");
        }
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }
        if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
            startDate = usageDate;
            endDate = usageDate;
        } else {
            if (StringUtils.isBlank(startDate)) {
                missingParameters.add("startDate");
            }
            if (StringUtils.isBlank(endDate)) {
                missingParameters.add("endDate");
            }
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCodeAndValidityDate(subscriptionCode, subscriptionValidityDate);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode, subscriptionValidityDate);
        }

        Access access = accessService.findByUserIdAndSubscription(accessCode, subscription, startDate, endDate);
        if (access == null) {
            throw new EntityDoesNotExistsException(Access.class, accessCode);
        }

        return new AccessDto(access, entityToDtoConverter.getCustomFieldsDTO(access, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }

    public void remove(String accessCode, String subscriptionCode, Date startDate, Date endDate) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(accessCode)) {
            missingParameters.add("accessCode");
        }
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        Access access = accessService.findByUserIdAndSubscription(accessCode, subscription, startDate, endDate);
        if (access == null) {
            throw new EntityDoesNotExistsException(Access.class, accessCode);
        }

        accessService.remove(access);
    }

    public AccessesDto listBySubscription(String subscriptionCode) throws MeveoApiException {
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }
        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        AccessesDto result = new AccessesDto();
        List<Access> accesses = accessService.listBySubscription(subscription);
        if (accesses != null) {
            for (Access ac : accesses) {
                result.getAccess().add(new AccessDto(ac, entityToDtoConverter.getCustomFieldsDTO(ac, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        return result;
    }

    /**
     * 
     * Create or update access based on the access user id and its subscription
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(AccessDto postData) throws MeveoApiException, BusinessException {

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        Access access = accessService.findByUserIdAndSubscription(postData.getCode(), subscription, postData.getStartDate(), postData.getEndDate());

        if (access == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * @param accessDto access dto
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdatePartial(AccessDto accessDto) throws MeveoApiException, BusinessException {
        AccessDto existedAccessDto = null;
        try {
            existedAccessDto = find(accessDto.getCode(), accessDto.getSubscription(), accessDto.getSubscriptionValidityDate(), accessDto.getStartDate(), accessDto.getEndDate(), null);
        } catch (Exception e) {
            existedAccessDto = null;
        }
        if (existedAccessDto == null) {
            create(accessDto);
        } else {

            if (!StringUtils.isBlank(accessDto.getStartDate())) {
                existedAccessDto.setStartDate(accessDto.getStartDate());
            }
            if (!StringUtils.isBlank(accessDto.getEndDate())) {
                existedAccessDto.setEndDate(accessDto.getEndDate());
            }
            if (accessDto.getCustomFields() != null && !accessDto.getCustomFields().isEmpty()) {
                existedAccessDto.setCustomFields(accessDto.getCustomFields());
            }
            if (accessDto.isDisabled() != null) {
                existedAccessDto.setDisabled(accessDto.isDisabled());
            }
            update(existedAccessDto);
        }
    }

    /**
     * Enable or disable access point
     * 
     * @param accessCode Access code
     * @param subscriptionCode subscription code
     * @param enable Should Access be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String accessCode, String subscriptionCode, Date startDate, Date endDate, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(accessCode)) {
            missingParameters.add("accessCode");
        }
        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
        }

        handleMissingParameters();

        Subscription subscription = subscriptionService.findByCode(subscriptionCode);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        Access access = accessService.findByUserIdAndSubscription(accessCode, subscription, startDate, endDate);
        if (access == null) {
            throw new EntityDoesNotExistsException(Access.class, accessCode);
        }
        if (enable) {
            accessService.enable(access);
        } else {
            accessService.disable(access);
        }
    }
}