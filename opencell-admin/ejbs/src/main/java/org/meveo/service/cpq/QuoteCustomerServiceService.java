package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteCustomerService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.quote.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteCustomerServiceService extends BusinessService<QuoteCustomerService> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteCustomerServiceService.class);
	
	@Inject
	private QuoteService quoteService;
	
	public QuoteCustomerService addNewQuoteCustomerService(QuoteCustomerService qcs, Long idQuote)  {
		LOGGER.info("adding a new quote customer service [code : {}, quoteId : {}]", qcs.getCode(), idQuote);
		
		final Quote quote = quoteService.findById(idQuote);
		if(quote == null) 
			throw new EntityDoesNotExistsException(Quote.class, idQuote);
		QuoteCustomerService lastQuoteCustomerService = findLastVersionByCode(quote.getCode());
		if(lastQuoteCustomerService == null) {
			qcs.setQuoteVersion(1);
		}else {
			qcs.setQuoteVersion(lastQuoteCustomerService.getQuoteVersion() + 1 );
		}
		/*qcs.setCode(code);
		qcs.setQuote(quote);
		qcs.setName(name);
		qcs.setDuration(duration);
		qcs.setExecutionDate(executionDate);*/
		
		this.create(qcs);
		LOGGER.info("adding a new quote customer service [code : {}, quoteId : {}] ==> operation successful", qcs.getCode(), idQuote);
		return qcs;
	}
	
	public QuoteCustomerService findLastVersionByCode(String codeQuoteCustomerService) {
		try {
			return (QuoteCustomerService) this.getEntityManager().createNamedQuery("QuoteCustomerService.findLastVersionByCode")
											.setParameter("codeQuote", codeQuoteCustomerService)
											.setMaxResults(1).getSingleResult();
					
		}catch(NoResultException e) {
			return null;
		}
	}
	
	public QuoteCustomerService findByCodeAndQuoteVersion(String quoteCustomerServiceCode, int quoteVersion) {
		try {
			return (QuoteCustomerService) this.getEntityManager().createNamedQuery("QuoteCustomerService.findByCodeAndVersion")
																	.setParameter("code", quoteCustomerServiceCode)
																		.setParameter("quoteVersion", quoteVersion).getSingleResult();
		}catch(NoResultException e) {
			LOGGER.warn("Unknow Quote Customer Service for key ("+quoteCustomerServiceCode+", "+quoteVersion+")");
			return null;
		}
		
	}
	
	
	
}
