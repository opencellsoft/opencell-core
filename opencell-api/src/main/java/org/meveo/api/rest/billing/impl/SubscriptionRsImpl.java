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

package org.meveo.api.rest.billing.impl;

import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.GetDueDateDelayResponseDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.RateSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.catalog.GetListServiceInstanceResponseDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargesResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceInstanceResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.SubscriptionRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SubscriptionRsImpl extends BaseRs implements SubscriptionRs {

    @Inject
    private SubscriptionApi subscriptionApi;

    @Override
    public ActionStatus create(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Subscription subscription = subscriptionApi.create(postData);
            result.setEntityCode(subscription.getCode());
            result.setEntityId(subscription.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Subscription subscription = subscriptionApi.update(postData);
            result.setEntityCode(subscription.getCode());
            result.setEntityId(subscription.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus instantiateServices(InstantiateServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.instantiateServices(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus activateServices(ActivateServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.activateServices(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.applyOneShotChargeInstance(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus applyProduct(ApplyProductRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.applyProduct(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus terminateSubscription(TerminateSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.terminateSubscription(postData, ChargeInstance.NO_ORDER_NUMBER);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus terminateServices(TerminateSubscriptionServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            postData.setOrderNumber(ChargeInstance.NO_ORDER_NUMBER);
            subscriptionApi.terminateServices(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public SubscriptionsListResponseDto listGet(String userAccountCode, Boolean mergedCF, String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder,
            CustomFieldInheritanceEnum inheritCF) {

        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering(userAccountCode != null ? "userAccount.code:" + userAccountCode : query, null, offset, limit, sortBy, sortOrder);

        try {
            if (inheritCF != null) {
                return subscriptionApi.list(pagingAndFiltering, inheritCF);
            } else {
                return subscriptionApi.list(mergedCF, pagingAndFiltering);
            }
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
    @Override
    public SubscriptionsListResponseDto findByCustomer(String customerCodeCode) {
        
        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        try {
            List<SubscriptionDto> subscriptions = subscriptionApi.listByCustomer(customerCodeCode, false).getSubscription();
            result.getSubscriptions().setSubscription(subscriptions);
            result.getSubscriptions().setListSize(subscriptions.size());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public SubscriptionsListResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        try {
            return subscriptionApi.list(null, pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetSubscriptionResponseDto findSubscription(String subscriptionCode, boolean mergedCF, CustomFieldInheritanceEnum inheritCF, Date validityDate) {
        GetSubscriptionResponseDto result = new GetSubscriptionResponseDto();

        try {
            result.setSubscription(subscriptionApi.findSubscription(subscriptionCode, mergedCF, inheritCF, validityDate));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Subscription subscription = subscriptionApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(subscription.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateSubscriptionPartial(SubscriptionDto subscriptionDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.createOrUpdatePartialWithAccessAndServices(subscriptionDto, null, null, null);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public SubscriptionsListResponseDto listAll(Integer offset, Integer limit, boolean mergedCF, String sortBy, SortOrder sortOrder) {
        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        try {
            result = subscriptionApi.list(mergedCF, new PagingAndFiltering(null, null, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;

    }

    @Override
    public ActionStatus suspendSubscription(OperationSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.suspendSubscription(postData.getSubscriptionCode(), postData.getActionDate(), postData.getSubscriptionValidityDate());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus resumeSubscription(OperationSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.resumeSubscription(postData.getSubscriptionCode(), postData.getActionDate(), postData.getSubscriptionValidityDate());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus suspendServices(OperationServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.suspendServices(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus resumeServices(OperationServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.resumeServices(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus updateServices(UpdateServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.updateServiceInstance(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetServiceInstanceResponseDto findServiceInstance(String subscriptionCode, Long serviceInstanceId, String serviceInstanceCode, Date subscriptionValidityDate) {
        GetServiceInstanceResponseDto result = new GetServiceInstanceResponseDto();

        try {
            result.setServiceInstance(subscriptionApi.findServiceInstance(subscriptionCode, serviceInstanceId, serviceInstanceCode, subscriptionValidityDate));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetDueDateDelayResponseDto findDueDateDelay(String subscriptionCode, Date subscriptionValidityDate, String invoiceNumber, String invoiceTypeCode, String orderCode) {
        GetDueDateDelayResponseDto result = new GetDueDateDelayResponseDto();

        try {
            result.setDueDateDelay(subscriptionApi.getDueDateDelay(subscriptionCode, subscriptionValidityDate, invoiceNumber, invoiceTypeCode, orderCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    /**
     * Get all one shot charge others.
     * 
     * @see org.meveo.api.rest.billing.SubscriptionRs#getOneShotChargeOthers(String subscriptionCode, Date validityDate)
     */
    public GetOneShotChargesResponseDto getOneShotChargeOthers(String subscriptionCode, Date validityDate) {
        GetOneShotChargesResponseDto result = new GetOneShotChargesResponseDto();

        try {
            if (subscriptionCode == null) {
                List<OneShotChargeTemplateDto> oneShotChargeOthers = subscriptionApi.getOneShotChargeOthers();
                result.getOneshotCharges().addAll(oneShotChargeOthers);
            } else {
                List<OneShotChargeInstanceDto> oneShotChargeInstances = subscriptionApi.getOneShotChargeOthers(subscriptionCode, validityDate);
                result.getOneshotChargeInstances().addAll(oneShotChargeInstances);
            }

        } catch (Exception e) {
            processException(e, new ActionStatus(ActionStatusEnum.FAIL, ""));
        }

        return result;
    }

    @Override
    public GetListServiceInstanceResponseDto listServiceInstance(String subscriptionCode, Date subscriptionValidityDate, String serviceInstanceCode) {
        GetListServiceInstanceResponseDto result = new GetListServiceInstanceResponseDto();
        try {
            result.setServiceInstances(subscriptionApi.listServiceInstance(subscriptionCode, subscriptionValidityDate, serviceInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public RateSubscriptionResponseDto rate(RateSubscriptionRequestDto postData) {
        RateSubscriptionResponseDto result = new RateSubscriptionResponseDto();
        try {
            result = subscriptionApi.rateSubscription(postData);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus activate(String subscriptionCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.activateSubscription(subscriptionCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelSubscriptionRenewal(String subscriptionCode, Date subscriptionValidityDate) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.cancelSubscriptionRenewal(subscriptionCode, subscriptionValidityDate);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public SubscriptionForCustomerResponseDto activateForCustomer(SubscriptionForCustomerRequestDto postData) {
        SubscriptionForCustomerResponseDto result = new SubscriptionForCustomerResponseDto();
        try {
            result = subscriptionApi.activateForCustomer(postData);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus terminateOneShotCharge(String subscriptionCode, String oneshotChargeCode, Date validityDate) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.terminateOneShotCharge(oneshotChargeCode, subscriptionCode, validityDate);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus subscribeAndActivateServices(SubscriptionAndServicesToActivateRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.subscribeAndActivateServices(postData);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus patchSubscription(String code, SubscriptionPatchDto subscriptionPatchDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.patchSubscription(code, subscriptionPatchDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus rollbackOffer(String code, OfferRollbackDto offerRollbackDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.rollbackOffer(code, offerRollbackDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }


}