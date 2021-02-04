package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FA.
 * @version 10.0
 */
@Stateless
public class QuoteLotService extends BusinessService<QuoteLot> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteLotService.class);
	
	@Inject
	private QuoteVersionService quoteVersionService;
	
	public QuoteLot addNewQuoteCustomerService(QuoteLot qcs, Long idQuote)  {
		LOGGER.info("adding a new quote customer service [code : {}, quoteId : {}]", qcs.getCode(), idQuote);
		
		final QuoteVersion quote = quoteVersionService.findById(idQuote);
		if(quote == null) 
			throw new EntityDoesNotExistsException(Quote.class, idQuote);
		/*qcs.setCode(code);
		qcs.setQuote(quote);
		qcs.setName(name);
		qcs.setDuration(duration);
		qcs.setExecutionDate(executionDate);*/
		
		this.create(qcs);
		LOGGER.info("adding a new quote customer service [code : {}, quoteId : {}] ==> operation successful", qcs.getCode(), idQuote);
		return qcs;
	}
	
	public QuoteLot findLastVersionByCode(String codeQuoteCustomerService) {
		try {
			return (QuoteLot) this.getEntityManager().createNamedQuery("QuoteLot.findLastVersionByCode")
											.setParameter("codeQuote", codeQuoteCustomerService)
											.setMaxResults(1).getSingleResult();
					
		}catch(NoResultException e) {
			return null;
		}
	}
	
	/*public QuoteLot findByCodeAndQuoteVersion(String quoteCustomerServiceCode, Long quoteVersionId) {
		try {
			return (QuoteLot) this.getEntityManager().createNamedQuery("QuoteLot.findByCodeAndVersion")
																	.setParameter("code", quoteCustomerServiceCode)
																		.setParameter("quoteVersionId", quoteVersionId).getSingleResult();
		}catch(NoResultException e) {
			LOGGER.warn("Unknow Quote Customer Service for key ("+quoteCustomerServiceCode+", "+quoteVersionId+")");
			return null;
		}
		
	}*/
	
	
	
}
