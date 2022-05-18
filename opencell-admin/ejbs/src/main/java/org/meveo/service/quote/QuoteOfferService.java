package org.meveo.service.quote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.base.PersistenceService;

@Stateless
public class QuoteOfferService extends PersistenceService<QuoteOffer> {
	
	@Inject
	private CustomGenericEntityCodeService customGenericEntityCodeService;

	@SuppressWarnings("unchecked")
	public List<QuoteOffer> findByQuoteVersion(QuoteVersion quoteVersion) {
		QueryBuilder queryBuilder = new QueryBuilder(QuoteOffer.class, "qo", Arrays.asList("quoteVersion"));
		 queryBuilder.addCriterion("qo.quoteVersion.id", "=", quoteVersion.getId(), false);
		 Query query = queryBuilder.getQuery(getEntityManager());
		 return query.getResultList();
	}
	
	
	public QuoteOffer findByCodeAndQuoteVersion(String code, Long quoteVersionId) {
		if(Strings.isEmpty(code) || quoteVersionId == null)
			throw new BusinessException("code and quoteVersion must not be empty");
		Query query=getEntityManager().createNamedQuery("QuoteOffer.findByCodeAndQuoteVersion");
		query.setParameter("quoteVersionId", quoteVersionId)
			  .setParameter("code", code);
		try {
			return (QuoteOffer) query.getSingleResult();
		}catch(NoResultException e ) {
			return null;
		}
	}
	
	@Override
	public void create(QuoteOffer entity) throws BusinessException {
		if(Strings.isEmpty(entity.getCode())) {
			entity.setCode(customGenericEntityCodeService.getGenericEntityCode(entity));
		}
		var quoteOfferExist = findByCodeAndQuoteVersion(entity.getCode(), entity.getQuoteVersion().getId());
		if(quoteOfferExist != null)
			throw new EntityAlreadyExistsException("Quote offer already exist with code : " + entity.getCode() + " and quote version id : " + entity.getQuoteVersion().getId());
		super.create(entity);
	}
	
	public List<QuoteOffer> findBySubscriptionAndStatus(String subscriptionCode, OfferLineTypeEnum quoteLineType) {
		Query query=getEntityManager().createNamedQuery("OrderOffer.findByStatusAndSubscription");
		query.setParameter("subscriptionCode", subscriptionCode)
			  .setParameter("status", quoteLineType);
		try {
			return (List<QuoteOffer>) query.getResultList();
		}catch(NoResultException e ) {
			return new ArrayList<QuoteOffer>();
		}
	}
}
