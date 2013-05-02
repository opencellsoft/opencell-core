package org.meveo.admin.job;

import javax.inject.Named;

import org.meveo.model.billing.Subscription;
import org.meveo.model.rating.EDR;
import org.meveo.service.medina.impl.AccessDAO;
import org.meveo.service.medina.impl.CDRParser;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;

@Named
public class ASGCdrParser implements CDRParser{

	@Override
	public String verifyFormat(String line) throws InvalidFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueKey(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessDAO getAccessInfo(String line) throws InvalidAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EDR getEDR(String line, Subscription s) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
