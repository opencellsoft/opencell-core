package org.meveo.service.medina.impl;

import java.io.File;
import java.io.Serializable;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.admin.parse.csv.CdrParserProducer;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.model.BaseEntity;
import org.meveo.model.IProvider;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.EdrService;
import org.slf4j.Logger;

@Singleton
public class CDRParsingService extends PersistenceService<EDR> {
	
	private Logger log;

	@Inject
	private EdrService edrService;

	@Inject
	@RejectedCDR
	private Event<Serializable> rejectededCdrEventProducer;

	@Inject
	private CdrParserProducer cdrParserProducer;
	
	@Inject
	private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;
	
	private String batchName;
	private String originBatch;
	private String username;


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

	public List<EDR> getEDRList(Serializable cdr,Provider provider) throws CDRParsingException {
		List<EDR> result = new ArrayList<EDR>();
		deduplicate(cdr, provider);
		List<Access> accessPoints = accessPointLookup(cdr,provider);
		boolean foundMatchingAccess = false;
		for (Access accessPoint : accessPoints) {
			EDRDAO edrDAO = getEDR(cdr);
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
				edr.setParameter5(edrDAO.getParameter5());
				edr.setParameter6(edrDAO.getParameter6());
				edr.setParameter7(edrDAO.getParameter7());
				edr.setParameter8(edrDAO.getParameter8());
				edr.setParameter9(edrDAO.getParameter9());
				edr.setDateParam1(edrDAO.getDateParam1());
				edr.setDateParam2(edrDAO.getDateParam2());
				edr.setDateParam3(edrDAO.getDateParam3());
				edr.setDateParam4(edrDAO.getDateParam4());
				edr.setDateParam5(edrDAO.getDateParam5());
				edr.setDecimalParam1(edrDAO.getDecimalParam1());
				edr.setDecimalParam2(edrDAO.getDecimalParam2());
				edr.setDecimalParam3(edrDAO.getDecimalParam3());
				edr.setDecimalParam4(edrDAO.getDecimalParam4());
				edr.setDecimalParam5(edrDAO.getDecimalParam5());
				edr.setAccessCode(accessPoint.getAccessUserId());
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

	private void deduplicate(Serializable cdr, Provider provider) throws DuplicateException {
		if (edrService.duplicateFound(provider, getOriginBatch(), getOriginRecord(cdr))) {
			throw new DuplicateException(cdr);
		}
	}

    private List<Access> accessPointLookup(Serializable cdr, Provider provider) throws InvalidAccessException {
        String accessUserId = getAccessUserId(cdr);
        List<Access> accesses = cdrEdrProcessingCacheContainerProvider.getAccessesByAccessUserId(provider.getId(), accessUserId);
        if (accesses == null || accesses.size() == 0) {
            ((IProvider)cdr).setProvider(provider);
            rejectededCdrEventProducer.fire(cdr);
            throw new InvalidAccessException(cdr);
        }
        return accesses;
    }

	public String getCDRLine(Serializable cdr, String reason) {
			return ((CDR) cdr).toString() + ";" + reason;
	}


	
	public EDRDAO getEDR(Serializable object) {
		CDR cdr = (CDR) object;
		EDRDAO result = new EDRDAO();
		result.setEventDate(cdr.getTimestamp());
		result.setOriginBatch(getOriginBatch());
		result.setOriginRecord(getOriginRecord(object));
	//	result.setQuantity(cdr.quantity.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
		result.setParameter1(cdr.param1);
		result.setParameter2(cdr.param2);
		result.setParameter3(cdr.param3);
		result.setParameter4(cdr.param4);
//		result.setParameter5(cdr.param5);
//		result.setParameter6(cdr.param6);
//		result.setParameter7(cdr.param7);
//		result.setParameter8(cdr.param8);
//		result.setParameter9(cdr.param9);
//		result.setDateParam1(cdr.dateParam1!=0?new Date(cdr.dateParam1):null);
//		result.setDateParam2(cdr.dateParam2!=0?new Date(cdr.dateParam2):null);
//		result.setDateParam3(cdr.dateParam3!=0?new Date(cdr.dateParam3):null);
//		result.setDateParam4(cdr.dateParam4!=0?new Date(cdr.dateParam4):null);
//		result.setDateParam5(cdr.dateParam5!=0?new Date(cdr.dateParam5):null);
//		result.setDecimalParam1(cdr.decimalParam1!=null ? cdr.decimalParam1.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
//		result.setDecimalParam2(cdr.decimalParam2!=null ? cdr.decimalParam2.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
//		result.setDecimalParam3(cdr.decimalParam3!=null ? cdr.decimalParam3.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
//		result.setDecimalParam4(cdr.decimalParam4!=null ? cdr.decimalParam4.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
//		result.setDecimalParam5(cdr.decimalParam5!=null ? cdr.decimalParam5.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
//		
		return result;
	}

	
	public String getOriginBatch() {
		if (StringUtils.isBlank(originBatch)) {
			return batchName == null ? "CDR_CONS_CSV" : batchName;
		} else {
			return originBatch;
		}
	}
	
	
	public String getOriginRecord(Serializable object) {
		String result = null;
		if (StringUtils.isBlank(username)) {
			CDR cdr = (CDR) object;
			result = cdr.toString();

			if (messageDigest != null) {
				synchronized (messageDigest) {
					messageDigest.reset();
					messageDigest.update(result.getBytes(Charset.forName("UTF8")));
					final byte[] resultByte = messageDigest.digest();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < resultByte.length; ++i) {
						sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
					}
					result = sb.toString();
				}
			}
		} else {
			return username + "_" + new Date().getTime();
		}

		return result;
	}
	
	static MessageDigest messageDigest = null;
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//log.error("No message digest of type MD5", e);
		}
	}


	
	public void init(File CDRFile) {
		batchName = "CDR_" + CDRFile.getName();
	}

	
	public void initByApi(String username, String ip) throws BusinessException{
		originBatch = "API_" + ip;
		this.username = username;
	}
	
	public String getAccessUserId(Serializable cdr) throws InvalidAccessException {
		String result = ((CDR) cdr).access_id;
		if (result == null || result.trim().length() == 0) {
			throw new InvalidAccessException(cdr);
		}
		/*
		 * if(((CDR)cdr).service_id!=null && (((CDR)cdr).service_id.length()>0)
		 * ){ result+="_"+((CDR)cdr).service_id; }
		 */
		return result;
	}
}
