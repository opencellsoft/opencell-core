package org.meveo.service.medina.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;

@Stateless
public class CDRParsingService {
	
	@Inject
	CDRParser cdrParser;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<EDR> parse(String line) throws CDRParsingException {
		List<EDR> result= new ArrayList<EDR>();
		cdrParser.verifyFormat(line);
		deduplicate(line);
		Access accessPoint=accessPointLookup(line);
		for(Subscription s:accessPoint.getSubscriptions()){
			result.add(cdrParser.getEDR(line,s));
		}		
		return result;
	}
	
	private void deduplicate(String line) throws DuplicateException{
		
	}

	private Access accessPointLookup(String line) throws InvalidAccessException,MultipleAccessException {
		Access result=null;
		return result;
	}
}
