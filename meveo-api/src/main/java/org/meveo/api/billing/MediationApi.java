package org.meveo.api.billing;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.CdrListDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.slf4j.Logger;

@Stateless
public class MediationApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private CDRParsingService cdrParsingService;

	@Inject
	private EdrService edrService;
	
	@Inject UsageRatingService usageRatingService;

	public void registerCdrList(CdrListDto postData, User currentUser) throws MeveoApiException {

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
				missingParameters.add("cdr");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void chargeCdr(String  cdr, User user, String ip) throws MeveoApiException {
		if(!StringUtils.isBlank(cdr)){
			try {
				cdrParsingService.initByApi(user.getUserName(), ip);
			} catch (BusinessException e1) {
				log.error(e1.getMessage());
				throw new MeveoApiException(e1.getMessage());
			}
			List<EDR> edrs;
			try {
				edrs = cdrParsingService.getEDRList(cdr);
				for (EDR edr : edrs) {
					log.debug("edr={}", edr);
					edrService.create(edr, user, user.getProvider());
					try {
						usageRatingService.ratePostpaidUsageWithinTransaction(edr, user);
						if(edr.getStatus()==EDRStatusEnum.REJECTED){
							try{
								edrService.remove(edr);
							} catch(Exception e1){}
							log.error("edr rejected={}", edr.getRejectReason());
							throw new MeveoApiException(edr.getRejectReason());
						}
					} catch (BusinessException e) {
						try{
							edrService.remove(edr);
						} catch(Exception e1){}
						log.error("Exception rating edr={}", e.getMessage());
						if("INSUFFICIENT_BALANCE".equals(e.getMessage())){
							throw new MeveoApiException(e.getMessage());
						} else {
							throw new MeveoApiException(e.getMessage());
						}
						
					}
				}
			} catch (CDRParsingException e) {
				log.error("Error parsing cdr={}", e.getRejectionCause());
				throw new MeveoApiException(e.getRejectionCause().toString());
			}
		} else {
			missingParameters.add("cdr");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void chargeCdrList(CdrListDto postData, User currentUser) throws MeveoApiException {
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
						try {
							usageRatingService.ratePostpaidUsageWithinTransaction(edr, currentUser);
						} catch (BusinessException e) {
							log.error("Exception rating edr={}", e.getMessage());
							throw new MeveoApiException(e.getMessage());
						}
					}
				}
			} catch (CDRParsingException e) {
				log.error("Error parsing cdr={}", e.getMessage());
				throw new MeveoApiException(e.getMessage());
			}
		} else {
			if (postData.getCdr() == null || postData.getCdr().size() == 0) {
				missingParameters.add("cdr");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
