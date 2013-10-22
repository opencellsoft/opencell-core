package org.meveo.api;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.api.dto.OfferDto;
import org.meveo.api.exception.MeveoApiException;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OfferServiceApi extends BaseApi {

	public void create(OfferDto offerDto) throws MeveoApiException {

	}

}
