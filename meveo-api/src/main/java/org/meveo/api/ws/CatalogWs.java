package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponse;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponse;
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
	ActionStatus createCounterTemplate(CounterTemplateDto postData);

	@WebMethod
	ActionStatus updateCounterTemplate(CounterTemplateDto postData);

	@WebMethod
	GetCounterTemplateResponse findCounterTemplate(String counterTemplateCode);

	@WebMethod
	ActionStatus removeCounterTemplate(String counterTemplateCode);
	
	@WebMethod
	ActionStatus createDiscountPlan(DiscountPlanDto postData);

	@WebMethod
	ActionStatus updateDiscountPlan(DiscountPlanDto postData);

	@WebMethod
	GetDiscountPlanResponse findDiscountPlan(Long id);

	@WebMethod
	ActionStatus removeDiscountPlan(Long id);
	
	@WebMethod
	ActionStatus createOfferTemplate(OfferTemplateDto postData);

	@WebMethod
	ActionStatus updateOfferTemplate(OfferTemplateDto postData);

	@WebMethod
	GetOfferTemplateResponse findOfferTemplate(String offerTemplateCode);

	@WebMethod
	ActionStatus removeOfferTemplate(String offerTemplateCode);
	
	@WebMethod
	public ActionStatus createOneShotChargeTemplate(OneShotChargeTemplateDto postData);

	@WebMethod
	public ActionStatus updateOneShotChargeTemplate(OneShotChargeTemplateDto postData);

	@WebMethod
	public GetOneShotChargeTemplateResponse findOneShotChargeTemplate(
			String oneShotChargeTemplateCode);

	@WebMethod
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplate(
			String languageCode, String countryCode, String currencyCode,
			String sellerCode, String date);

	@WebMethod
	public ActionStatus removeOneShotChargeTemplate(String oneShotChargeTemplateCode);
	
	@WebMethod
	ActionStatus createPricePlan(PricePlanDto postData);

	@WebMethod
	ActionStatus updatePricePlan(PricePlanDto postData);

	@WebMethod
	GetPricePlanResponse findPricePlan(Long id);

	@WebMethod
	ActionStatus removePricePlan(Long id);
	
	@WebMethod
	public ActionStatus createRecurringChargeTemplate(RecurringChargeTemplateDto postData);

	@WebMethod
	public GetRecurringChargeTemplateResponse findRecurringChargeTemplate(
			String recurringChargeTemplateCode);

	@WebMethod
	public ActionStatus updateRecurringChargeTemplate(RecurringChargeTemplateDto postData);

	@WebMethod
	public ActionStatus removeRecurringChargeTemplate(String recurringChargeTemplateCode);
	
	@WebMethod
	ActionStatus createServiceTemplate(ServiceTemplateDto postData);

	@WebMethod
	ActionStatus updateServiceTemplate(ServiceTemplateDto postData);

	@WebMethod
	GetServiceTemplateResponse findServiceTemplate(String serviceTemplateCode);

	@WebMethod
	ActionStatus removeServiceTemplate(String serviceTemplateCode);
	
	@WebMethod
	public ActionStatus createUsageChargeTemplate(UsageChargeTemplateDto postData);

	@WebMethod
	public ActionStatus updateUsageChargeTemplate(UsageChargeTemplateDto postData);

	@WebMethod
	public GetUsageChargeTemplateResponse findUsageChargeTemplate(String usageChargeTemplateCode);

	@WebMethod
	public ActionStatus removeUsageChargeTemplate(String usageChargeTemplateCode);
	
}
