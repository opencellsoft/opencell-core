package org.meveo.api;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SubscriptionWithCreditLimitServiceApi extends BaseApi {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

}
