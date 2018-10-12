package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.OperationServicesRequestDto;
import org.meveo.api.dto.billing.OperationSubscriptionRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.GetDueDateDelayResponseDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
import org.meveo.api.dto.response.catalog.GetListServiceInstanceResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceInstanceResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.SubscriptionWs;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@WebService(serviceName = "SubscriptionWs", endpointInterface = "org.meveo.api.ws.SubscriptionWs")
@Interceptors({ WsRestApiInterceptor.class })
public class SubscriptionWsImpl extends BaseWs implements SubscriptionWs {

    @Inject
    private SubscriptionApi subscriptionApi;

    @Override
    public ActionStatus create(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.update(postData);
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
	public ActionStatus activateSubscription(String subscriptionCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.activateSubscription(subscriptionCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
	}

    @Override
    public SubscriptionsResponseDto listSubscriptionByUserAccount(String userAccountCode) {
        SubscriptionsResponseDto result = new SubscriptionsResponseDto();

        try {
            result.setSubscriptions(subscriptionApi.listByUserAccount(userAccountCode, false, "code", SortOrder.ASCENDING));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public SubscriptionsListResponseDto listAll(Boolean mergedCF, PagingAndFiltering pagingAndFiltering) {
        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        try {
            result = subscriptionApi.list(mergedCF, pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetSubscriptionResponseDto findSubscription(String subscriptionCode,CustomFieldInheritanceEnum inheritCF) {
        GetSubscriptionResponseDto result = new GetSubscriptionResponseDto();

        try {
            result.setSubscription(subscriptionApi.findSubscription(subscriptionCode, inheritCF != null ? inheritCF : CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdateSubscription(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.createOrUpdate(postData);
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
    public ActionStatus suspendSubscription(OperationSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.suspendSubscription(postData.getSubscriptionCode(), postData.getActionDate());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus resumeSubscription(OperationSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.resumeSubscription(postData.getSubscriptionCode(), postData.getActionDate());
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
    public GetServiceInstanceResponseDto findServiceInstance(String subscriptionCode, Long serviceInstanceId, String serviceInstanceCode) {
        GetServiceInstanceResponseDto result = new GetServiceInstanceResponseDto();

        try {
            result.setServiceInstance(subscriptionApi.findServiceInstance(subscriptionCode, serviceInstanceId, serviceInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    public GetDueDateDelayResponseDto findDueDateDelay(String subscriptionCode, String invoiceNumber, String invoiceTypeCode, String orderCode) {
        GetDueDateDelayResponseDto result = new GetDueDateDelayResponseDto();

        try {
            result.setDueDateDelay(subscriptionApi.getDueDateDelay(subscriptionCode, invoiceNumber, invoiceTypeCode, orderCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetListServiceInstanceResponseDto listServiceInstance(String subscriptionCode, String serviceInstanceCode) {
        GetListServiceInstanceResponseDto result = new GetListServiceInstanceResponseDto();

        try {
            result.setServiceInstances(subscriptionApi.listServiceInstance(subscriptionCode, serviceInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

	@Override
	public ActionStatus cancelSubscriptionRenewal(String subscriptionCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.cancelSubscriptionRenewal(subscriptionCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
	}

}