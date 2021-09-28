package org.meveo.service.cpq.order;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.base.PersistenceService;

/**
 * @author Tarik FAKHOURI
 * @version 11.0
 * @dateCreation 19/01/2021
 */
@Stateless
public class OrderOfferService extends PersistenceService<OrderOffer> {


	@Inject
	private CustomGenericEntityCodeService customGenericEntityCodeService;
	

	public OrderOffer findByCodeAndQuoteVersion(String code, String orderCode) {
		if(Strings.isEmpty(code) || Strings.isEmpty(orderCode))
			throw new BusinessException("code and quoteVersion must not be empty");
		Query query=getEntityManager().createNamedQuery("OrderOffer.findByCodeAndOrderCode");
		query.setParameter("orderCode", orderCode)
			  .setParameter("code", code);
		try {
			return (OrderOffer) query.getSingleResult();
		}catch(NoResultException e ) {
			return null;
		}
	}
	
	@Override
	public void create(OrderOffer entity) throws BusinessException {
		if(Strings.isEmpty(entity.getCode())) {
			entity.setCode(customGenericEntityCodeService.getGenericEntityCode(entity));
		}
		var orderOfferExist = findByCodeAndQuoteVersion(entity.getCode(), entity.getOrder().getCode());
		if(orderOfferExist != null)
			throw new EntityAlreadyExistsException("Quote offer already exist with code : " + entity.getCode() + " and Order code : " + entity.getOrder().getCode());
		super.create(entity);
	}
}
