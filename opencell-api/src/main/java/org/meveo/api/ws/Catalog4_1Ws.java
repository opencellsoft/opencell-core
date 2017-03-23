package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.OfferTemplate4_1Dto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
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

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface Catalog4_1Ws extends IBaseWs {

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

	// charges

	@WebMethod
	GetChargeTemplateResponseDto findChargeTemplate(@WebParam(name = "chargeTemplateCode") String chargeTemplateCode);

	@WebMethod
	public ActionStatus createRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

	@WebMethod
	public GetRecurringChargeTemplateResponseDto findRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplateCode") String recurringChargeTemplateCode);

	@WebMethod
	public ActionStatus updateRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

	@WebMethod
	public ActionStatus removeRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplateCode") String recurringChargeTemplateCode);

	@WebMethod
	public ActionStatus createOrUpdateRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

	@WebMethod
	public ActionStatus createOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

	@WebMethod
	public ActionStatus updateOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

	@WebMethod
	public GetOneShotChargeTemplateResponseDto findOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

	@WebMethod
	public ActionStatus createOrUpdateOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

	@WebMethod
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplate(
			@WebParam(name = "languageCode") String languageCode, @WebParam(name = "countryCode") String countryCode,
			@WebParam(name = "currencyCode") String currencyCode, @WebParam(name = "sellerCode") String sellerCode,
			@WebParam(name = "date") String date);

	@WebMethod
	public ActionStatus removeOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

	@WebMethod
	public ActionStatus createUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

	@WebMethod
	public ActionStatus updateUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

	@WebMethod
	public GetUsageChargeTemplateResponseDto findUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplateCode") String usageChargeTemplateCode);

	@WebMethod
	public ActionStatus removeUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplateCode") String usageChargeTemplateCode);

	@WebMethod
	public ActionStatus createOrUpdateUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

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

	// offer

	@WebMethod
	ActionStatus createOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplate4_1Dto postData);

	@WebMethod
	ActionStatus updateOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplate4_1Dto postData);

	@WebMethod
	GetOfferTemplateResponseDto findOfferTemplate(@WebParam(name = "offerTemplateCode") String offerTemplateCode);

	@WebMethod
	ActionStatus removeOfferTemplate(@WebParam(name = "offerTemplateCode") String offerTemplateCode);

	@WebMethod
	ActionStatus createOrUpdateOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplate4_1Dto postData);

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

	// bom offer

	@WebMethod
	ActionStatus createOfferFromBOM(@WebParam(name = "bomOffer") BomOfferDto postData);
	
	//discount Plan
	@WebMethod
	ActionStatus createDiscountPlan(@WebParam(name = "discountPlan") DiscountPlanDto postData);
	
	@WebMethod
	ActionStatus updateDiscountPlan(@WebParam(name = "discountPlan") DiscountPlanDto postData);
	
	@WebMethod
	ActionStatus createOrUpdateDiscountPlan(@WebParam(name = "discountPlan") DiscountPlanDto postData);
	
	@WebMethod
	GetDiscountPlanResponseDto findDiscountPlan(@WebParam(name = "discountPlanCocde") String discountPlanCode);
	
	@WebMethod
	ActionStatus removeDiscountPlan(@WebParam(name = "discountPlanCocde") String discountPlanCode);
	
	@WebMethod
	GetDiscountPlansResponseDto listDiscountPlan();
	
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
	
}
