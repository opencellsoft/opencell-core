package org.meveo.api.billing;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class MediationApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private CDRParsingService cdrParsingService;

	@Inject
	private EdrService edrService;

	public void create(CdrListDto postData, User currentUser) throws MeveoApiException {
		if (postData.getCdr() != null && postData.getCdr().size() > 0) {
			try {
				cdrParsingService.initByApi(currentUser.getUserName(), postData.getIpAddress());
			} catch (BusinessException e1) {
				log.error(e1.getMessage());
				throw new MeveoApiException(e1.getMessage());
			}

			try {
				for (String line : postData.getCdr()) {
					List<EDR> edrs = cdrParsingService.getEDRList(line);
					for (EDR edr : edrs) {
						log.debug("edr={}", edr);
						edrService.create(edr, currentUser, currentUser.getProvider());
					}
				}
			} catch (CDRParsingException e) {
				log.error("Error parsing cdr={}", e.getMessage());
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			if (postData.getCdr() == null || postData.getCdr().size() == 0) {
				missingParameters.add("edrs");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
