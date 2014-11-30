package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface OfferTemplateWs extends IBaseWs {

	@WebMethod
	ActionStatus create(OfferTemplateDto postData);

	@WebMethod
	ActionStatus update(OfferTemplateDto postData);

	@WebMethod
	GetOfferTemplateResponse find(String offerTemplateCode);

	@WebMethod
	ActionStatus remove(String offerTemplateCode);

}
