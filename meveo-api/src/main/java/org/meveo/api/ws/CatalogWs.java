package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponse;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponse;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponse;
import org.meveo.api.dto.response.catalog.GetPricePlanResponse;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponse;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponse;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface CatalogWs extends IBaseWs {

	@WebMethod
	ActionStatus createCounterTemplate(@WebParam(name = "counterTemplate") CounterTemplateDto postData);

	@WebMethod
	ActionStatus updateCounterTemplate(@WebParam(name = "counterTemplate") CounterTemplateDto postData);

	@WebMethod
	GetCounterTemplateResponse findCounterTemplate(@WebParam(name = "counterTemplateCode") String counterTemplateCode);

	@WebMethod
	ActionStatus removeCounterTemplate(@WebParam(name = "counterTemplateCode") String counterTemplateCode);

	@WebMethod
	ActionStatus createOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplateDto postData);

	@WebMethod
	ActionStatus updateOfferTemplate(@WebParam(name = "offerTemplate") OfferTemplateDto postData);

	@WebMethod
	GetOfferTemplateResponse findOfferTemplate(@WebParam(name = "offerTemplateCode") String offerTemplateCode);

	@WebMethod
	ActionStatus removeOfferTemplate(@WebParam(name = "offerTemplateCode") String offerTemplateCode);

	@WebMethod
	public ActionStatus createOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

	@WebMethod
	public ActionStatus updateOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplate") OneShotChargeTemplateDto postData);

	@WebMethod
	public GetOneShotChargeTemplateResponse findOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

	@WebMethod
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplate(
			@WebParam(name = "languageCode") String languageCode, @WebParam(name = "countryCode") String countryCode,
			@WebParam(name = "currencyCode") String currencyCode, @WebParam(name = "sellerCode") String sellerCode,
			@WebParam(name = "date") String date);

	@WebMethod
	public ActionStatus removeOneShotChargeTemplate(
			@WebParam(name = "oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

	@WebMethod
	ActionStatus createPricePlan(@WebParam(name = "pricePlan") PricePlanDto postData);

	@WebMethod
	ActionStatus updatePricePlan(@WebParam(name = "pricePlan") PricePlanDto postData);

	@WebMethod
	GetPricePlanResponse findPricePlan(@WebParam(name = "pricePlanCode") String pricePlanCode);

	@WebMethod
	ActionStatus removePricePlan(@WebParam(name = "pricePlanCode") String pricePlanCode);

	@WebMethod
	public ActionStatus createRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

	@WebMethod
	public GetRecurringChargeTemplateResponse findRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplateCode") String recurringChargeTemplateCode);

	@WebMethod
	public ActionStatus updateRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplate") RecurringChargeTemplateDto postData);

	@WebMethod
	public ActionStatus removeRecurringChargeTemplate(
			@WebParam(name = "recurringChargeTemplateCode") String recurringChargeTemplateCode);

	@WebMethod
	ActionStatus createServiceTemplate(@WebParam(name = "serviceTemplate") ServiceTemplateDto postData);

	@WebMethod
	ActionStatus updateServiceTemplate(@WebParam(name = "serviceTemplate") ServiceTemplateDto postData);

	@WebMethod
	GetServiceTemplateResponse findServiceTemplate(@WebParam(name = "serviceTemplateCode") String serviceTemplateCode);

	@WebMethod
	ActionStatus removeServiceTemplate(@WebParam(name = "serviceTemplateCode") String serviceTemplateCode);

	@WebMethod
	public ActionStatus createUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

	@WebMethod
	public ActionStatus updateUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplate") UsageChargeTemplateDto postData);

	@WebMethod
	public GetUsageChargeTemplateResponse findUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplateCode") String usageChargeTemplateCode);

	@WebMethod
	public ActionStatus removeUsageChargeTemplate(
			@WebParam(name = "usageChargeTemplateCode") String usageChargeTemplateCode);

}
