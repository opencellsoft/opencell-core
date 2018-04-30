package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BpmProductDto;
import org.meveo.api.dto.catalog.BsmServiceDto;
import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.DigitalResourceDto;
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

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface CatalogWs extends IBaseWs {

    // counter template

    @WebMethod
    ActionStatus createCounterTemplate(@WebParam(name = "counterTemplate") CounterTemplateDto postData);

    @WebMethod
    ActionStatus updateCounterTemplate(@WebParam(name = "counterTemplate") CounterTemplateDto postData);

    @WebMethod
    GetCounterTemplateResponseDto findCounterTemplate(@WebParam(name = "counterTemplateCode") String counterTemplateCode);

    @WebMethod
    ActionStatus removeCounterTemplate(@WebParam(name = "counterTemplateCode") String counterTemplateCode);

    @WebMethod
    ActionStatus createOrUpdateCounterTemplate(@WebParam(name = "counterTemplate") CounterTemplateDto postData);

    /**
     * Enable a Counter template by its code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableCounterTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a Counter template by its code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableCounterTemplate(@WebParam(name = "code") String code);

    // charges

    @WebMethod
    GetChargeTemplateResponseDto findChargeTemplate(@WebParam(name = "chargeTemplateCode") String chargeTemplateCode);

    @WebMethod
    public ActionStatus createRecurringChargeTemplate(@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

    @WebMethod
    public GetRecurringChargeTemplateResponseDto findRecurringChargeTemplate(@WebParam(name = "recurringChargeTemplateCode") String recurringChargeTemplateCode);

    @WebMethod
    public ActionStatus updateRecurringChargeTemplate(@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

    @WebMethod
    public ActionStatus removeRecurringChargeTemplate(@WebParam(name = "recurringChargeTemplateCode") String recurringChargeTemplateCode);

    @WebMethod
    public ActionStatus createOrUpdateRecurringChargeTemplate(@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

    /**
     * Enable a Recurring charge template by its code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableRecurringChargeTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a Recurring charge template by its code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableRecurringChargeTemplate(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus createOneShotChargeTemplate(@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

    @WebMethod
    public ActionStatus updateOneShotChargeTemplate(@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

    @WebMethod
    public GetOneShotChargeTemplateResponseDto findOneShotChargeTemplate(@WebParam(name = "oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    @WebMethod
    public ActionStatus createOrUpdateOneShotChargeTemplate(@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

    @WebMethod
    public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplate(@WebParam(name = "languageCode") String languageCode, @WebParam(name = "countryCode") String countryCode,
            @WebParam(name = "currencyCode") String currencyCode, @WebParam(name = "sellerCode") String sellerCode, @WebParam(name = "date") String date);

    @WebMethod
    public ActionStatus removeOneShotChargeTemplate(@WebParam(name = "oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * Enable a One shot charge template by its code
     * 
     * @param code One shot charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableOneShotChargeTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a One shot charge template by its code
     * 
     * @param code One shot charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableOneShotChargeTemplate(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus createUsageChargeTemplate(@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

    @WebMethod
    public ActionStatus updateUsageChargeTemplate(@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

    @WebMethod
    public GetUsageChargeTemplateResponseDto findUsageChargeTemplate(@WebParam(name = "usageChargeTemplateCode") String usageChargeTemplateCode);

    @WebMethod
    public ActionStatus removeUsageChargeTemplate(@WebParam(name = "usageChargeTemplateCode") String usageChargeTemplateCode);

    @WebMethod
    public ActionStatus createOrUpdateUsageChargeTemplate(@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

    /**
     * Enable a Usage charge template by its code
     * 
     * @param code Usage charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableUsageChargeTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a Usage charge template by its code
     * 
     * @param code Usage charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableUsageChargeTemplate(@WebParam(name = "code") String code);

    // service

    @WebMethod
    ActionStatus createServiceTemplate(@WebParam(name = "serviceTemplate") ServiceTemplateDto postData);

    @WebMethod
    ActionStatus updateServiceTemplate(@WebParam(name = "serviceTemplate") ServiceTemplateDto postData);

    @WebMethod
    GetServiceTemplateResponseDto findServiceTemplate(@WebParam(name = "serviceTemplateCode") String serviceTemplateCode);

    @WebMethod
    ActionStatus removeServiceTemplate(@WebParam(name = "serviceTemplateCode") String serviceTemplateCode);

    @WebMethod
    ActionStatus createOrUpdateServiceTemplate(@WebParam(name = "serviceTemplate") ServiceTemplateDto postData);

    /**
     * Enable a Service template by its code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableServiceTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a Service template by its code
     * 
     * @param code Service template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableServiceTemplate(@WebParam(name = "code") String code);

    // offer

    @WebMethod
    ActionStatus createOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplateDto postData);

    @WebMethod
    ActionStatus updateOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplateDto postData);

    @WebMethod
    GetOfferTemplateResponseDto findOfferTemplate(@WebParam(name = "offerTemplateCode") String offerTemplateCode, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo, @WebParam(name = "loadOfferServiceTemplate") boolean loadOfferServiceTemplate,
            @WebParam(name = "loadOfferProductTemplate") boolean loadOfferProductTemplate, @WebParam(name = "loadServiceChargeTemplate") boolean loadServiceChargeTemplate,
            @WebParam(name = "loadProductChargeTemplate") boolean loadProductChargeTemplate);

    @WebMethod
    ActionStatus removeOfferTemplate(@WebParam(name = "offerTemplateCode") String offerTemplateCode, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo);

    @WebMethod
    ActionStatus createOrUpdateOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplateDto postData);

    /**
     * List all offer templates optionally filtering by code and validity dates. If neither date is provided, validity dates will not be considered.If only validFrom is provided, a
     * search will return offers valid on a given date. If only validTo date is provided, a search will return offers valid from today to a given date.
     * 
     * @param code Offer template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @param pagingAndFiltering Pagination and filtering criteria.
     * @return A list of offer templates
     */
    @WebMethod
    GetListOfferTemplateResponseDto listOfferTemplate(@Deprecated @WebParam(name = "offerTemplateCode") String code, @Deprecated @WebParam(name = "validFrom") Date validFrom,
            @Deprecated @WebParam(name = "validTo") Date validTo, @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    /**
     * Enable a Offer template by its code and validity dates
     * 
     * @param code Offer template code
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableOfferTemplate(@WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    /**
     * Disable a Offer template by its code and validity dates
     * 
     * @param code Offer template code
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableOfferTemplate(@WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    // price plan

    @WebMethod
    ActionStatus createPricePlan(@WebParam(name = "pricePlan") PricePlanMatrixDto postData);

    @WebMethod
    ActionStatus updatePricePlan(@WebParam(name = "pricePlan") PricePlanMatrixDto postData);

    @WebMethod
    GetPricePlanResponseDto findPricePlan(@WebParam(name = "pricePlanCode") String pricePlanCode);

    @WebMethod
    ActionStatus removePricePlan(@WebParam(name = "pricePlanCode") String pricePlanCode);

    @WebMethod
    PricePlanMatrixesResponseDto listPricePlanByEventCode(@WebParam(name = "eventCode") String eventCode);

    @WebMethod
    ActionStatus createOrUpdatePricePlan(@WebParam(name = "pricePlan") PricePlanMatrixDto postData);

    /**
     * Enable a Price plan by its code
     * 
     * @param code Price plan code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enablePricePlan(@WebParam(name = "code") String code);

    /**
     * Disable a Price plan by its code
     * 
     * @param code Price plan code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disablePricePlan(@WebParam(name = "code") String code);

    // triggered Edr

    @WebMethod
    ActionStatus createTriggeredEdr(@WebParam(name = "triggeredEdrTemplate") TriggeredEdrTemplateDto postData);

    @WebMethod
    ActionStatus updateTriggeredEdr(@WebParam(name = "triggeredEdrTemplate") TriggeredEdrTemplateDto postData);

    @WebMethod
    GetTriggeredEdrResponseDto findTriggeredEdr(@WebParam(name = "triggeredEdrCode") String triggeredEdrCode);

    @WebMethod
    ActionStatus removeTriggeredEdr(@WebParam(name = "triggeredEdrCode") String triggeredEdrCode);

    @WebMethod
    ActionStatus createOrUpdateTriggeredEdr(@WebParam(name = "triggeredEdrTemplate") TriggeredEdrTemplateDto postData);

    // bom

    @WebMethod
    ActionStatus createBusinessOfferModel(@WebParam(name = "businessOfferModel") BusinessOfferModelDto postData);

    @WebMethod
    ActionStatus updateBusinessOfferModel(@WebParam(name = "businessOfferModel") BusinessOfferModelDto postData);

    @WebMethod
    GetBusinessOfferModelResponseDto findBusinessOfferModel(@WebParam(name = "businessOfferModelCode") String businessOfferModelCode);

    @WebMethod
    ActionStatus removeBusinessOfferModel(@WebParam(name = "businessOfferModelCode") String businessOfferModelCode);

    @WebMethod
    ActionStatus createOrUpdateBusinessOfferModel(@WebParam(name = "businessOfferModel") BusinessOfferModelDto postData);

    @WebMethod
    ActionStatus installBusinessOfferModel(@WebParam(name = "businessOfferModel") BusinessOfferModelDto postData);

    @WebMethod
    MeveoModuleDtosResponse listBusinessOfferModel();

    /**
     * Enable a Business offer model by its code
     * 
     * @param code Business offer model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableBusinessOfferModel(@WebParam(name = "code") String code);

    /**
     * Disable a Business offer model by its code
     * 
     * @param code Business offer model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableBusinessOfferModel(@WebParam(name = "code") String code);

    // som
    @WebMethod
    ActionStatus createBusinessServiceModel(@WebParam(name = "businessServiceModel") BusinessServiceModelDto postData);

    @WebMethod
    ActionStatus updateBusinessServiceModel(@WebParam(name = "businessServiceModel") BusinessServiceModelDto postData);

    @WebMethod
    GetBusinessServiceModelResponseDto findBusinessServiceModel(@WebParam(name = "businessServiceModelCode") String businessServiceModelCode);

    @WebMethod
    ActionStatus removeBusinessServiceModel(@WebParam(name = "businessServiceModelCode") String businessServiceModelCode);

    @WebMethod
    ActionStatus createOrUpdateBusinessServiceModel(@WebParam(name = "businessServiceModel") BusinessServiceModelDto postData);

    @WebMethod
    ActionStatus installBusinessServiceModel(@WebParam(name = "businessServiceModel") BusinessServiceModelDto postData);

    @WebMethod
    MeveoModuleDtosResponse listBusinessServiceModel();

    /**
     * Enable a Business service model by its code
     * 
     * @param code Business service model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableBusinessServiceModel(@WebParam(name = "code") String code);

    /**
     * Disable a Business service model by its code
     * 
     * @param code Business service model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableBusinessServiceModel(@WebParam(name = "code") String code);

    // bpm
    @WebMethod
    ActionStatus createBusinessProductModel(@WebParam(name = "businessProductModel") BusinessProductModelDto postData);

    @WebMethod
    ActionStatus updateBusinessProductModel(@WebParam(name = "businessProductModel") BusinessProductModelDto postData);

    @WebMethod
    GetBusinessProductModelResponseDto findBusinessProductModel(@WebParam(name = "businessProductModelCode") String businessProductModelCode);

    @WebMethod
    ActionStatus removeBusinessProductModel(@WebParam(name = "businessProductModelCode") String businessProductModelCode);

    @WebMethod
    ActionStatus createOrUpdateBusinessProductModel(@WebParam(name = "businessProductModel") BusinessProductModelDto postData);

    @WebMethod
    ActionStatus installBusinessProductModel(@WebParam(name = "businessProductModel") BusinessProductModelDto postData);

    @WebMethod
    MeveoModuleDtosResponse listBusinessProductModel();

    /**
     * Enable a Business product model by its code
     * 
     * @param code Business product model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableBusinessProductModel(@WebParam(name = "code") String code);

    /**
     * Disable a Business product model by its code
     * 
     * @param code Business product model code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableBusinessProductModel(@WebParam(name = "code") String code);

    // bom offer

    @WebMethod
    ActionStatus createOfferFromBOM(@WebParam(name = "bomOffer") BomOfferDto postData);

    // bsm service

    @WebMethod
    ActionStatus createServiceFromBSM(@WebParam(name = "bsmService") BsmServiceDto postData);

    // bpm

    /**
     * Instantiates a product from a given BPM.
     * 
     * @param postData post Data DTO
     * @return ActionStatus
     */
    @WebMethod
    ActionStatus createProductFromBPM(@WebParam(name = "bpm") BpmProductDto postData);

    // discount Plan
    @WebMethod
    ActionStatus createDiscountPlan(@WebParam(name = "discountPlan") DiscountPlanDto postData);

    @WebMethod
    ActionStatus updateDiscountPlan(@WebParam(name = "discountPlan") DiscountPlanDto postData);

    @WebMethod
    ActionStatus createOrUpdateDiscountPlan(@WebParam(name = "discountPlan") DiscountPlanDto postData);

    @WebMethod
    GetDiscountPlanResponseDto findDiscountPlan(@WebParam(name = "discountPlanCode") String discountPlanCode);

    @WebMethod
    ActionStatus removeDiscountPlan(@WebParam(name = "discountPlanCode") String discountPlanCode);

    @WebMethod
    GetDiscountPlansResponseDto listDiscountPlan();

    /**
     * Enable a Discount plan by its code
     * 
     * @param code Discount plan code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableDiscountPlan(@WebParam(name = "code") String code);

    /**
     * Disable a Discount plan by its code
     * 
     * @param code Discount plan code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableDiscountPlan(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createOfferTemplateCategory(@WebParam(name = "offerTemplateCategory") OfferTemplateCategoryDto postData);

    @WebMethod
    ActionStatus updateOfferTemplateCategory(@WebParam(name = "offerTemplateCategory") OfferTemplateCategoryDto postData);

    @WebMethod
    ActionStatus createOrUpdateOfferTemplateCategory(@WebParam(name = "offerTemplateCategory") OfferTemplateCategoryDto postData);

    @WebMethod
    ActionStatus removeOfferTemplateCategory(@WebParam(name = "offerTemplateCategoryCode") String code);

    @WebMethod
    GetOfferTemplateCategoryResponseDto findOfferTemplateCategory(@WebParam(name = "offerTemplateCategoryCode") String code);

    /**
     * Enable a Offer template category by its code
     * 
     * @param code Offer template category code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableOfferTemplateCategory(@WebParam(name = "code") String code);

    /**
     * Disable a Offer template category by its code
     * 
     * @param code Offer template category code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableOfferTemplateCategory(@WebParam(name = "code") String code);

    // discount Plan item
    @WebMethod
    ActionStatus createDiscountPlanItem(@WebParam(name = "discountPlanItem") DiscountPlanItemDto postData);

    @WebMethod
    ActionStatus updateDiscountPlanItem(@WebParam(name = "discountPlanItem") DiscountPlanItemDto postData);

    @WebMethod
    ActionStatus createOrUpdateDiscountPlanItem(@WebParam(name = "discountPlanItem") DiscountPlanItemDto postData);

    @WebMethod
    DiscountPlanItemResponseDto findDiscountPlanItem(@WebParam(name = "discountPlanItemCode") String discountPlanItemCode);

    @WebMethod
    ActionStatus removeDiscountPlanItem(@WebParam(name = "discountPlanItemCode") String discountPlanItemCode);

    @WebMethod
    DiscountPlanItemsResponseDto listDiscountPlanItem();

    /**
     * Enable a Discount plan item by its code
     * 
     * @param code Discount plan item code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableDiscountPlanItem(@WebParam(name = "code") String code);

    /**
     * Disable a Discount plan item by its code
     * 
     * @param code Discount plan item code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableDiscountPlanItem(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createProductTemplate(@WebParam(name = "productTemplate") ProductTemplateDto postData);

    @WebMethod
    ActionStatus updateProductTemplate(@WebParam(name = "productTemplate") ProductTemplateDto postData);

    @WebMethod
    ActionStatus createOrUpdateProductTemplate(@WebParam(name = "productTemplate") ProductTemplateDto postData);

    @WebMethod
    ActionStatus removeProductTemplate(@WebParam(name = "productTemplateCode") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    /**
     * Enable a Product template by its code and validity dates
     * 
     * @param code Product template code
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableProductTemplate(@WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    /**
     * Disable a Product template by its code and validity dates
     * 
     * @param code Product template code
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableProductTemplate(@WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    /**
     * List product templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered.If only validFrom is provided, a search will return product bundles valid on a given date. If only valdTo
     * date is provided, a search will return product valid from today to a given date.
     * 
     * @param code Product template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @param pagingAndFiltering Paging and filtering criteria.
     * @return A list of product templates
     */
    @WebMethod
    GetListProductTemplateResponseDto listProductTemplate(@Deprecated @WebParam(name = "productTemplateCode") String code, @Deprecated @WebParam(name = "validFrom") Date validFrom,
            @Deprecated @WebParam(name = "validTo") Date validTo, @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    @WebMethod
    GetProductTemplateResponseDto findProductTemplate(@WebParam(name = "productTemplateCode") String code, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo);

    @WebMethod
    ActionStatus createDigitalResource(@WebParam(name = "digitalResource") DigitalResourceDto postData);

    @WebMethod
    ActionStatus updateDigitalResource(@WebParam(name = "digitalResource") DigitalResourceDto postData);

    @WebMethod
    ActionStatus createOrUpdateDigitalResource(@WebParam(name = "digitalResource") DigitalResourceDto postData);

    @WebMethod
    ActionStatus removeDigitalResource(@WebParam(name = "digitalResourceCode") String code);

    @WebMethod
    GetDigitalResourceResponseDto findDigitalResource(@WebParam(name = "digitalResourceCode") String code);

    /**
     * Enable a Digital resource by its code
     * 
     * @param code Digital resource code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableDigitalResource(@WebParam(name = "code") String code);

    /**
     * Disable a Digital resource by its code
     * 
     * @param code Digital resource code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableDigitalResource(@WebParam(name = "code") String code);

    @WebMethod
    public ActionStatus createOrUpdateProductChargeTemplate(@WebParam(name = "productChargeTemplate") ProductChargeTemplateDto postData);

    @WebMethod
    public ActionStatus createProductChargeTemplate(@WebParam(name = "productChargeTemplate") ProductChargeTemplateDto postData);

    @WebMethod
    public ActionStatus updateProductChargeTemplate(@WebParam(name = "productChargeTemplate") ProductChargeTemplateDto postData);

    @WebMethod
    public GetProductChargeTemplateResponseDto findProductChargeTemplate(@WebParam(name = "productChargeTemplateCode") String productChargeTemplateCode);

    @WebMethod
    public ActionStatus removeProductChargeTemplate(@WebParam(name = "productChargeTemplateCode") String productChargeTemplateCode);

    /**
     * Enable a Product charge template by its code
     * 
     * @param code Product charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableProductChargeTemplate(@WebParam(name = "code") String code);

    /**
     * Disable a Product charge template by its code
     * 
     * @param code Product charge template code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableProductChargeTemplate(@WebParam(name = "code") String code);

    @WebMethod
    ActionStatus createBundleTemplate(@WebParam(name = "bundleTemplate") BundleTemplateDto postData);

    @WebMethod
    ActionStatus updateBundleTemplate(@WebParam(name = "bundleTemplate") BundleTemplateDto postData);

    @WebMethod
    ActionStatus createOrUpdateBundleTemplate(@WebParam(name = "bundleTemplate") BundleTemplateDto postData);

    @WebMethod
    ActionStatus removeBundleTemplate(@WebParam(name = "bundleTemplateCode") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    @WebMethod
    GetBundleTemplateResponseDto findBundleTemplate(@WebParam(name = "bundleTemplateCode") String code, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo);

    /**
     * List product bundle templates matching filtering and query criteria or code and validity dates.
     * 
     * If neither date is provided, validity dates will not be considered.If only validFrom is provided, a search will return product bundles valid on a given date. If only valdTo
     * date is provided, a search will return product bundles valid from today to a given date.
     * 
     * @param code Product template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @param pagingAndFiltering Paging and filtering criteria.
     * @return A list of product templates
     */
    @WebMethod
    GetListBundleTemplateResponseDto listBundleTemplate(@Deprecated @WebParam(name = "bundleTemplateCode") String code, @Deprecated @WebParam(name = "validFrom") Date validFrom,
            @Deprecated @WebParam(name = "validTo") Date validTo, @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    /**
     * Enable a Product bundle template by its code and validity dates
     * 
     * @param code Product bundle template code
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableBundleTemplate(@WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    /**
     * Disable a Product bundle template by its code and validity dates
     * 
     * @param code Product bundle template code
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableBundleTemplate(@WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo);

    @WebMethod
    ActionStatus createChannel(@WebParam(name = "channel") ChannelDto postData);

    @WebMethod
    ActionStatus updateChannel(@WebParam(name = "channel") ChannelDto postData);

    @WebMethod
    ActionStatus createOrUpdateChannel(@WebParam(name = "channel") ChannelDto postData);

    @WebMethod
    ActionStatus removeChannel(@WebParam(name = "channel") String code);

    @WebMethod
    GetChannelResponseDto findChannel(@WebParam(name = "channel") String code);

    /**
     * Enable a Channel by its code
     * 
     * @param code Channel code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableChannel(@WebParam(name = "code") String code);

    /**
     * Disable a Channel by its code
     * 
     * @param code Channel code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableChannel(@WebParam(name = "code") String code);
}
