package org.meveo.service.medina.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;

@Stateless
public class CDRParsingService {
	
	@Inject
	CSVCDRParser cdrParser;
	
	@Inject
	EdrService edrService;

	@Inject
	AccessService accessService;

	public void init(File CDRFile) {
		cdrParser.init(CDRFile);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<EDR> getEDRList(String line) throws CDRParsingException {
		List<EDR> result= new ArrayList<EDR>();
		Serializable cdr=cdrParser.getCDR(line);
		deduplicate(cdr);
		List<Access> accessPoints=accessPointLookup(cdr);
		for(Access accessPoint:accessPoints){
			EDRDAO edrDAO = cdrParser.getEDR(cdr);
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
		return result;
	}
	
	private void deduplicate(Serializable cdr) throws DuplicateException{
		EDR edr=edrService.findByBatchAndRecordId(cdrParser.getOriginBatch(),cdrParser.getOriginRecord(cdr));
		if(edr!=null){
			throw new DuplicateException(cdr);
		}
	}

	private List<Access> accessPointLookup(Serializable cdr) throws InvalidAccessException {
		String userId = cdrParser.getAccessUserId(cdr);
		List<Access> accesses = accessService.findByUserID(userId);
		if(accesses.size()==0){
			throw new InvalidAccessException(cdr);
		}
		return accesses;
	}

	public String getCDRLine(Serializable cdr, String reason) {
		return cdrParser.getCDRLine(cdr, reason) ;
	}

}
