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
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SubscriptionRsImpl extends BaseRs implements SubscriptionRs {

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
    public SubscriptionsListResponseDto listGet(String userAccountCode, Boolean mergedCF, String query, String fields, Integer offset, Integer limit, String sortBy,
            SortOrder sortOrder, CustomFieldInheritanceEnum inheritCF) {

        SubscriptionsListResponseDto result = new SubscriptionsListResponseDto();

        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering(userAccountCode != null ? "userAccount.code:" + userAccountCode : query, null, offset, limit, sortBy,
            sortOrder);

        try {
            if(inheritCF != null) {
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
    public GetSubscriptionResponseDto findSubscription(String subscriptionCode, boolean mergedCF, CustomFieldInheritanceEnum inheritCF) {
        GetSubscriptionResponseDto result = new GetSubscriptionResponseDto();

        try {
            result.setSubscription(subscriptionApi.findSubscription(subscriptionCode, mergedCF, inheritCF));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(SubscriptionDto postData) {
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

    @Override
    public GetDueDateDelayResponseDto findDueDateDelay(String subscriptionCode, String invoiceNumber, String invoiceTypeCode, String orderCode) {
        GetDueDateDelayResponseDto result = new GetDueDateDelayResponseDto();

        try {
            result.setDueDateDelay(subscriptionApi.getDueDateDelay(subscriptionCode, invoiceNumber, invoiceTypeCode, orderCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    /**
     * Get all one shot charge others.
     * 
     * @see org.meveo.api.rest.billing.SubscriptionRs#getOneShotChargeOthers(String subscriptionCode)
     */
    public GetOneShotChargesResponseDto getOneShotChargeOthers(String subscriptionCode) {
        GetOneShotChargesResponseDto result = new GetOneShotChargesResponseDto();

        try {
            if(subscriptionCode == null) {
                List<OneShotChargeTemplateDto> oneShotChargeOthers = subscriptionApi.getOneShotChargeOthers();
                result.getOneshotCharges().addAll(oneShotChargeOthers);
            } else {
                List<OneShotChargeInstanceDto> oneShotChargeInstances = subscriptionApi.getOneShotChargeOthers(subscriptionCode);
                result.getOneshotChargeInstances().addAll(oneShotChargeInstances);
            }

        } catch (Exception e) {
            processException(e, new ActionStatus(ActionStatusEnum.FAIL, ""));
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
	public ActionStatus cancelSubscriptionRenewal(String subscriptionCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.cancelSubscriptionRenewal(subscriptionCode);
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
    public ActionStatus cancelOneShotCharge(Long oneshotChargeId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.cancelOneShotCharge(oneshotChargeId);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}