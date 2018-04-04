package org.meveo.api.ws.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.catalog.BundleTemplateApi;
import org.meveo.api.catalog.BusinessOfferApi;
import org.meveo.api.catalog.ChannelApi;
import org.meveo.api.catalog.ChargeTemplateApi;
import org.meveo.api.catalog.CounterTemplateApi;
import org.meveo.api.catalog.DigitalResourceApi;
import org.meveo.api.catalog.DiscountPlanApi;
import org.meveo.api.catalog.DiscountPlanItemApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.catalog.OneShotChargeTemplateApi;
import org.meveo.api.catalog.PricePlanMatrixApi;
import org.meveo.api.catalog.ProductChargeTemplateApi;
import org.meveo.api.catalog.ProductTemplateApi;
import org.meveo.api.catalog.RecurringChargeTemplateApi;
import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.catalog.TriggeredEdrApi;
import org.meveo.api.catalog.UsageChargeTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BpmProductDto;
import org.meveo.api.dto.catalog.BsmServiceDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.DigitalResourcesDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.dto.response.catalog.GetBundleTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.catalog.GetBusinessProductModelResponseDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.catalog.GetChannelResponseDto;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetDigitalResourceResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlansResponseDto;
import org.meveo.api.dto.response.catalog.GetListBundleTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetListOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetListProductTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.GetProductChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetProductTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetTriggeredEdrResponseDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.ws.CatalogWs;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 * @lastModifiedVersion 5.0
 */
@WebService(serviceName = "CatalogWs", endpointInterface = "org.meveo.api.ws.CatalogWs")
@Interceptors({ WsRestApiInterceptor.class })
public class CatalogWsImpl extends BaseWs implements CatalogWs {

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

    @Inject
    private DiscountPlanItemApi discountPlanItemApi;

    @Inject
    private ProductTemplateApi productTemplateApi;

    @Inject
    private DigitalResourceApi digitalResourceApi;

    @Inject
    private ProductChargeTemplateApi productChargeTemplateApi;

    @Inject
    private BundleTemplateApi bundleTemplateApi;

    @Inject
    private ChannelApi channelApi;

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
    public ActionStatus createOfferTemplate(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateOfferTemplate(OfferTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOfferTemplateResponseDto findOfferTemplate(String offerTemplateCode, Date validFrom, Date validTo, boolean loadOfferServiceTemplate, boolean loadOfferProductTemplate,
            boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate) {
        GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();

        try {
            result.setOfferTemplate(offerTemplateApi.find(offerTemplateCode, validFrom, validTo, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, loadOfferServiceTemplate,
                loadOfferProductTemplate, loadServiceChargeTemplate, loadProductChargeTemplate));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeOfferTemplate(String offerTemplateCode, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.remove(offerTemplateCode, validFrom, validTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetListOfferTemplateResponseDto listOfferTemplate(String code, Date validFrom, Date validTo, PagingAndFiltering pagingAndFiltering) {

        GetListOfferTemplateResponseDto result = new GetListOfferTemplateResponseDto();

        try {
            result = offerTemplateApi.list(code, validFrom, validTo, pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
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
    public ActionStatus createOrUpdateOfferTemplate(OfferTemplateDto postData) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
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
        } catch (Exception e) {
            processException(e, result);
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
            moduleApi.delete(businessOfferModelCode);
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
    public ActionStatus installBusinessOfferModel(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listBusinessOfferModel() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        try {
            result = moduleApi.list(BusinessOfferModel.class);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOfferFromBOM(BomOfferDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage("" + businessOfferApi.instantiateBOM(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    @Override
    public ActionStatus createServiceFromBSM(BsmServiceDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage("" + businessOfferApi.instantiateBSM(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
    @Override
    public ActionStatus createProductFromBPM(BpmProductDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage("" + businessOfferApi.instantiateBPM(postData));
            
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

    @Override
    public ActionStatus createBusinessServiceModel(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBusinessServiceModel(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBusinessServiceModelResponseDto findBusinessServiceModel(String businessServiceModelCode) {
        GetBusinessServiceModelResponseDto result = new GetBusinessServiceModelResponseDto();

        try {
            result.setBusinessServiceModel((BusinessServiceModelDto) moduleApi.find(businessServiceModelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeBusinessServiceModel(String businessServiceModelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.delete(businessServiceModelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateBusinessServiceModel(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus installBusinessServiceModel(BusinessServiceModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listBusinessServiceModel() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        try {
            result = moduleApi.list(BusinessServiceModel.class);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createDiscountPlanItem(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateProductTemplate(ProductTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateDiscountPlanItem(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateDiscountPlanItem(DiscountPlanItemDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DiscountPlanItemResponseDto findDiscountPlanItem(String discountPlanItemCode) {
        DiscountPlanItemResponseDto result = new DiscountPlanItemResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");
        try {
            DiscountPlanItemDto dto = discountPlanItemApi.find(discountPlanItemCode);
            result.setDiscountPlanItem(dto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeDiscountPlanItem(String discountPlanItemCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            discountPlanItemApi.remove(discountPlanItemCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public DiscountPlanItemsResponseDto listDiscountPlanItem() {
        DiscountPlanItemsResponseDto result = new DiscountPlanItemsResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");
        try {
            List<DiscountPlanItemDto> dtos = discountPlanItemApi.list();
            result.setDiscountPlanItems(dtos);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createProductTemplate(ProductTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateProductTemplate(ProductTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetProductTemplateResponseDto findProductTemplate(String code, Date validFrom, Date validTo) {
        GetProductTemplateResponseDto result = new GetProductTemplateResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");

        try {
            ProductTemplateDto productTemplateDto = productTemplateApi.find(code, validFrom, validTo);
            result.setProductTemplate(productTemplateDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeProductTemplate(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productTemplateApi.remove(code, validFrom, validTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateDigitalResource(DigitalResourcesDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            digitalResourceApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createDigitalResource(DigitalResourcesDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            digitalResourceApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateDigitalResource(DigitalResourcesDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            digitalResourceApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetDigitalResourceResponseDto findDigitalResource(String code) {
        GetDigitalResourceResponseDto result = new GetDigitalResourceResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");

        try {
            DigitalResourcesDto digitalResourcesDto = digitalResourceApi.find(code);
            result.setDigitalResourcesDto(digitalResourcesDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeDigitalResource(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            digitalResourceApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateProductChargeTemplate(ProductChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productChargeTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createProductChargeTemplate(ProductChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productChargeTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateProductChargeTemplate(ProductChargeTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productChargeTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetProductChargeTemplateResponseDto findProductChargeTemplate(String productChargeTemplateCode) {
        GetProductChargeTemplateResponseDto result = new GetProductChargeTemplateResponseDto();

        try {
            result.setProductChargeTemplate(productChargeTemplateApi.find(productChargeTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeProductChargeTemplate(String productChargeTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            productChargeTemplateApi.remove(productChargeTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateBundleTemplate(BundleTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            bundleTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createBundleTemplate(BundleTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            bundleTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBundleTemplate(BundleTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            bundleTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBundleTemplateResponseDto findBundleTemplate(String code, Date validFrom, Date validTo) {
        GetBundleTemplateResponseDto result = new GetBundleTemplateResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");

        try {
            BundleTemplateDto bundleTemplateDto = bundleTemplateApi.find(code, validFrom, validTo);
            result.setBundleTemplate(bundleTemplateDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeBundleTemplate(String code, Date validFrom, Date validTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            bundleTemplateApi.remove(code, validFrom, validTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetListBundleTemplateResponseDto listBundleTemplate(String code, Date validFrom, Date validTo, PagingAndFiltering pagingAndFiltering) {
        GetListBundleTemplateResponseDto result = new GetListBundleTemplateResponseDto();

        try {
            return bundleTemplateApi.list(code, validFrom, validTo, pagingAndFiltering);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createChannel(ChannelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            channelApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateChannel(ChannelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            channelApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateChannel(ChannelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            channelApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeChannel(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            channelApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetChannelResponseDto findChannel(String code) {
        GetChannelResponseDto result = new GetChannelResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");

        try {
            ChannelDto channelDto = channelApi.find(code);
            result.setChannel(channelDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public GetListProductTemplateResponseDto listProductTemplate(String code, Date validFrom, Date validTo, PagingAndFiltering pagingAndFiltering) {

        GetListProductTemplateResponseDto result = new GetListProductTemplateResponseDto();

        try {
            return productTemplateApi.list(code, validFrom, validTo, pagingAndFiltering);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createBusinessProductModel(BusinessProductModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBusinessProductModel(BusinessProductModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBusinessProductModelResponseDto findBusinessProductModel(String businessProductModelCode) {
        GetBusinessProductModelResponseDto result = new GetBusinessProductModelResponseDto();

        try {
            result.setBusinessProductModel((BusinessProductModelDto) moduleApi.find(businessProductModelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeBusinessProductModel(String businessProductModelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.delete(businessProductModelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateBusinessProductModel(BusinessProductModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus installBusinessProductModel(BusinessProductModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listBusinessProductModel() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        try {
            result = moduleApi.list(BusinessProductModel.class);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
    
   
}