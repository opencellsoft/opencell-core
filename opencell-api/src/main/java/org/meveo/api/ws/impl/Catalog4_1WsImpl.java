package org.meveo.api.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.BusinessOfferApi;
import org.meveo.api.catalog.ChargeTemplateApi;
import org.meveo.api.catalog.CounterTemplateApi;
import org.meveo.api.catalog.DiscountPlanApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.catalog.OneShotChargeTemplateApi;
import org.meveo.api.catalog.PricePlanMatrixApi;
import org.meveo.api.catalog.RecurringChargeTemplateApi;
import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.catalog.TriggeredEdrApi;
import org.meveo.api.catalog.UsageChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplate4_1Dto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlansResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetTriggeredEdrResponseDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.ws.Catalog4_1Ws;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "Catalog4_1Ws", endpointInterface = "org.meveo.api.ws.Catalog4_1Ws")
@Interceptors({ WsRestApiInterceptor.class })
public class Catalog4_1WsImpl extends BaseWs implements Catalog4_1Ws {

    @Inject
    private BusinessOfferApi businessOfferApi;

    @Inject
    private TriggeredEdrApi triggeredEdrApi;

    @Inject
    private ChargeTemplateApi chargeTemplateApi;

    @Inject
    private CounterTemplateApi counterTemplateApi;

    @Inject
    private OfferTemplateApi offerTemplateApi;

    @Inject
    private OneShotChargeTemplateApi oneShotChargeTemplateApi;

    @Inject
    private PricePlanMatrixApi pricePlanApi;

    @Inject
    private RecurringChargeTemplateApi recurringChargeTemplateApi;

    @Inject
    private ServiceTemplateApi serviceTemplateApi;

    @Inject
    private UsageChargeTemplateApi usageChargeTemplateApi;

    @Inject
    private DiscountPlanApi discountPlanApi;

    @Inject
    private OfferTemplateCategoryApi offerTemplateCategoryApi;

    @Inject
    private MeveoModuleApi moduleApi;

    @Override
    public ActionStatus createCounterTemplate(CounterTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            counterTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCounterTemplate(CounterTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            counterTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCounterTemplateResponseDto findCounterTemplate(String counterTemplateCode) {
        GetCounterTemplateResponseDto result = new GetCounterTemplateResponseDto();

        try {
            result.setCounterTemplate(counterTemplateApi.find(counterTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCounterTemplate(String counterTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            counterTemplateApi.remove(counterTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOfferTemplate(OfferTemplate4_1Dto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        OfferTemplateDto offerTemplateDto = convertOfferTemplateDto(postData);

        try {
            offerTemplateApi.create(offerTemplateDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateOfferTemplate(OfferTemplate4_1Dto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        OfferTemplateDto offerTemplateDto = convertOfferTemplateDto(postData);

        try {
            offerTemplateApi.update(offerTemplateDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateResponseDto findOfferTemplate(String offerTemplateCode) {
        GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();

        try {
            result.setOfferTemplate(offerTemplateApi.find(offerTemplateCode, null, null));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeOfferTemplate(String offerTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.remove(offerTemplateCode, null,  null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateOfferTemplate(OfferTemplate4_1Dto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        OfferTemplateDto offerTemplateDto = convertOfferTemplateDto(postData);

        try {
            offerTemplateApi.createOrUpdate(offerTemplateDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * @param postData instance of OfferTemplate4_1Dto which contains posted infos.
     * @return instance of OfferTemplateDto 
     */
    private OfferTemplateDto convertOfferTemplateDto(OfferTemplate4_1Dto postData) {
        OfferTemplateDto offerTemplateDto = new OfferTemplateDto();
        offerTemplateDto.setCode(postData.getCode());
        offerTemplateDto.setDescription(postData.getDescription());
        offerTemplateDto.setDisabled(postData.isDisabled());
        offerTemplateDto.setBomCode(postData.getBomCode());
        offerTemplateDto.setOfferTemplateCategoryCode(postData.getOfferTemplateCategoryCode());
        offerTemplateDto.setCustomFields(postData.getCustomFields());

        if (postData.getServiceTemplates() != null && postData.getServiceTemplates().getServiceTemplate() != null) {
            List<OfferServiceTemplateDto> offerServiceTemplateDtos = new ArrayList<>();
            for (ServiceTemplateDto st : postData.getServiceTemplates().getServiceTemplate()) {
                OfferServiceTemplateDto offerServiceTemplateDto = new OfferServiceTemplateDto();
                offerServiceTemplateDto.setMandatory(false);
                offerServiceTemplateDto.setServiceTemplate(st);
                offerServiceTemplateDtos.add(offerServiceTemplateDto);
            }
            offerTemplateDto.setOfferServiceTemplates(offerServiceTemplateDtos);
        }

        return offerTemplateDto;
    }

    @Override
    public ActionStatus createOneShotChargeTemplate(OneShotChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateOneShotChargeTemplate(OneShotChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOneShotChargeTemplateResponseDto findOneShotChargeTemplate(String oneShotChargeTemplateCode) {
        GetOneShotChargeTemplateResponseDto result = new GetOneShotChargeTemplateResponseDto();

        try {
            result.setOneShotChargeTemplate(oneShotChargeTemplateApi.find(oneShotChargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplate(String languageCode, String countryCode, String currencyCode, String sellerCode, String date) {

        OneShotChargeTemplateWithPriceListDto result = new OneShotChargeTemplateWithPriceListDto();

        try {
            result.setOneShotChargeTemplateDtos(
                oneShotChargeTemplateApi.listWithPrice(languageCode, countryCode, currencyCode, sellerCode, DateUtils.parseDateWithPattern(date, "yyyy-MM-dd")));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeOneShotChargeTemplate(String oneShotChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.remove(oneShotChargeTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createPricePlan(PricePlanMatrixDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updatePricePlan(PricePlanMatrixDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetPricePlanResponseDto findPricePlan(String pricePlanCode) {
        GetPricePlanResponseDto result = new GetPricePlanResponseDto();

        try {
            result.setPricePlan(pricePlanApi.find(pricePlanCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removePricePlan(String pricePlanCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.remove(pricePlanCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetChargeTemplateResponseDto findChargeTemplate(String chargeTemplateCode) {
        GetChargeTemplateResponseDto result = new GetChargeTemplateResponseDto();

        try {
            result.setChargeTemplate(chargeTemplateApi.find(chargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createRecurringChargeTemplate(RecurringChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetRecurringChargeTemplateResponseDto findRecurringChargeTemplate(String recurringChargeTemplateCode) {
        GetRecurringChargeTemplateResponseDto result = new GetRecurringChargeTemplateResponseDto();

        try {
            result.setRecurringChargeTemplate(recurringChargeTemplateApi.find(recurringChargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus updateRecurringChargeTemplate(RecurringChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeRecurringChargeTemplate(String recurringChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.remove(recurringChargeTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createServiceTemplate(ServiceTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateServiceTemplate(ServiceTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetServiceTemplateResponseDto findServiceTemplate(String serviceTemplateCode) {
        GetServiceTemplateResponseDto result = new GetServiceTemplateResponseDto();

        try {
            result.setServiceTemplate(serviceTemplateApi.find(serviceTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeServiceTemplate(String serviceTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.remove(serviceTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createUsageChargeTemplate(UsageChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateUsageChargeTemplate(UsageChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUsageChargeTemplateResponseDto findUsageChargeTemplate(String usageChargeTemplateCode) {
        GetUsageChargeTemplateResponseDto result = new GetUsageChargeTemplateResponseDto();

        try {
            result.setUsageChargeTemplate(usageChargeTemplateApi.find(usageChargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeUsageChargeTemplate(String usageChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.remove(usageChargeTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createTriggeredEdr(TriggeredEdrTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            triggeredEdrApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateTriggeredEdr(TriggeredEdrTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            triggeredEdrApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTriggeredEdrResponseDto findTriggeredEdr(String triggeredEdrCode) {
        GetTriggeredEdrResponseDto result = new GetTriggeredEdrResponseDto();

        try {
            result.setTriggeredEdrTemplate(triggeredEdrApi.find(triggeredEdrCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeTriggeredEdr(String triggeredEdrCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            triggeredEdrApi.remove(triggeredEdrCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public PricePlanMatrixesResponseDto listPricePlanByEventCode(String eventCode) {
        PricePlanMatrixesResponseDto result = new PricePlanMatrixesResponseDto();

        try {
            result.getPricePlanMatrixes().setPricePlanMatrix(pricePlanApi.list(eventCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateUsageChargeTemplate(UsageChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            usageChargeTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateTriggeredEdr(TriggeredEdrTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            triggeredEdrApi.createOrUpdate(postData);
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateServiceTemplate(ServiceTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateRecurringChargeTemplate(RecurringChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            recurringChargeTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdatePricePlan(PricePlanMatrixDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            pricePlanApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateOneShotChargeTemplate(OneShotChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            oneShotChargeTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCounterTemplate(CounterTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            counterTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createBusinessOfferModel(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBusinessOfferModel(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBusinessOfferModelResponseDto findBusinessOfferModel(String businessOfferModelCode) {
        GetBusinessOfferModelResponseDto result = new GetBusinessOfferModelResponseDto();

        try {
            result.setBusinessOfferModel((BusinessOfferModelDto) moduleApi.find(businessOfferModelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeBusinessOfferModel(String businessOfferModelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.remove(businessOfferModelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateBusinessOfferModel(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOfferFromBOM(BomOfferDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            businessOfferApi.instantiateBOM(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createDiscountPlan(DiscountPlanDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateDiscountPlan(DiscountPlanDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetDiscountPlanResponseDto findDiscountPlan(String discountPlanCode) {
        GetDiscountPlanResponseDto result = new GetDiscountPlanResponseDto();

        try {
            result.setDiscountPlanDto(discountPlanApi.find(discountPlanCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeDiscountPlan(String discountPlanCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanApi.remove(discountPlanCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetDiscountPlansResponseDto listDiscountPlan() {
        GetDiscountPlansResponseDto result = new GetDiscountPlansResponseDto();

        try {
            result.setDiscountPlan(discountPlanApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateDiscountPlan(DiscountPlanDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOfferTemplateCategory(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateOfferTemplateCategory(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateOfferTemplateCategory(OfferTemplateCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeOfferTemplateCategory(String offerTemplateCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateCategoryApi.remove(offerTemplateCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateCategoryResponseDto findOfferTemplateCategory(String offerTemplateCategoryCode) {
        GetOfferTemplateCategoryResponseDto result = new GetOfferTemplateCategoryResponseDto();

        try {
            result.setOfferTemplateCategory(offerTemplateCategoryApi.find(offerTemplateCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}
