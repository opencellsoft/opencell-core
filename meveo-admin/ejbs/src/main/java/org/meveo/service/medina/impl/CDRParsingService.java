package org.meveo.service.medina.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CdrParserProducer;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.util.MeveoCacheContainerProvider;

@Stateless
public class CDRParsingService {
	
	private CSVCDRParser cdrParser;

	@Inject
	private EdrService edrService;

	@Inject
	private AccessService accessService;
	

	@Inject
	@RejectedCDR
	private Event<Serializable> rejectededCdrEventProducer;

	@Inject
	private CdrParserProducer cdrParserProducer;

	public void init(File CDRFile) throws BusinessException {
		cdrParser = cdrParserProducer.getParser();
		cdrParser.init(CDRFile);
	}

	public void initByApi(String username, String ip) throws BusinessException {
		cdrParser = cdrParserProducer.getParser();
		cdrParser.initByApi(username, ip);
	}

	/*public void resetAccessPointCache(Access access) {
		List<Access> accesses = null;
		if (MeveoCacheContainerProvider.getAccessCache().containsKey(access.getAccessUserId())) {
			accesses = MeveoCacheContainerProvider.getAccessCache().get(access.getAccessUserId());
			boolean found = false;
			for (Access cachedAccess : accesses) {
				if ((access.getSubscription().getId() != null && access.getSubscription().getId()
						.equals(cachedAccess.getSubscription().getId()))
						|| (cachedAccess.getSubscription().getCode() != null && cachedAccess.getSubscription()
								.getCode().equals(access.getSubscription().getCode()))) {
					cachedAccess.setStartDate(access.getStartDate());
					cachedAccess.setEndDate(access.getEndDate());
					found = true;
					break;
				}
			}
			if (!found) {
				accesses.add(access);
			}
		} else {
			accesses = new ArrayList<Access>();
			accesses.add(access);
			MeveoCacheContainerProvider.getAccessCache().put(access.getAccessUserId(), accesses);
		}
	}*/

	public List<EDR> getEDRList(String line,Provider provider) throws CDRParsingException {
		List<EDR> result = new ArrayList<EDR>();
		Serializable cdr = cdrParser.getCDR(line);
		deduplicate(cdr);
		List<Access> accessPoints = accessPointLookup(cdr,provider);
		boolean foundMatchingAccess = false;
		for (Access accessPoint : accessPoints) {
			EDRDAO edrDAO = cdrParser.getEDR(cdr);
			if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= edrDAO.getEventDate()
					.getTime())
					&& (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > edrDAO.getEventDate()
							.getTime())) {
				foundMatchingAccess = true;
				EDR edr = new EDR();
				edr.setCreated(new Date());
				edr.setEventDate(edrDAO.getEventDate());
				edr.setOriginBatch(edrDAO.getOriginBatch());
				edr.setOriginRecord(edrDAO.getOriginRecord());
				edr.setParameter1(edrDAO.getParameter1());
				edr.setParameter2(edrDAO.getParameter2());
				edr.setParameter3(edrDAO.getParameter3());
				edr.setParameter4(edrDAO.getParameter4());
				edr.setProvider(accessPoint.getProvider());
				edr.setQuantity(edrDAO.getQuantity());
				edr.setStatus(EDRStatusEnum.OPEN);
				edr.setSubscription(accessPoint.getSubscription());
				result.add(edr);
			}
		}

		if (!foundMatchingAccess) {
			throw new InvalidAccessException(cdr);
		}

		return result;
	}

	private void deduplicate(Serializable cdr) throws DuplicateException {
		if (edrService.duplicateFound(cdrParser.getOriginBatch(), cdrParser.getOriginRecord(cdr))) {
			throw new DuplicateException(cdr);
		}
	}

	private List<Access> accessPointLookup(Serializable cdr,Provider provider) throws InvalidAccessException {
		String userId = cdrParser.getAccessUserId(cdr);
		String cacheKey = provider.getCode()+"_"+userId;
		List<Access> accesses = null;
		if (MeveoCacheContainerProvider.getAccessCache().containsKey(cacheKey)) {
			accesses = MeveoCacheContainerProvider.getAccessCache().get(cacheKey);
		} else {
			accesses = accessService.findByUserID(userId,provider);
			if (accesses.size() == 0) {
				rejectededCdrEventProducer.fire(cdr);
				throw new InvalidAccessException(cdr);
			}
			MeveoCacheContainerProvider.getAccessCache().put(cacheKey, accesses);
		}
		return accesses;
	}

	public String getCDRLine(Serializable cdr, String reason) {
		return cdrParser.getCDRLine(cdr, reason);
	}

	public CSVCDRParser getCdrParser() {
		return cdrParser;
	}

}
