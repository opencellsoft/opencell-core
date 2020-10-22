package org.meveo.service.cpq;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteCustomerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.QuoteCustomerServiceException;
import org.meveo.service.quote.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteCustomerServiceService extends PersistenceService<QuoteCustomerService> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteCustomerServiceService.class);
	
	private static final String QUOTE_CUSTOMER_SERVICE_ALREADY_EXIST = "Quote customer service for code %s already exist";
	
	@Inject
	private QuoteService quoteService;
	
	public QuoteCustomerService addNewQuoteCustomerService(String code, Long idQuote, String name, int duration, Date executionDate) throws QuoteCustomerServiceException {
		LOGGER.info("adding a new quote customer service [code : {}, quoteId : {}]", code, idQuote);
		
		final Quote quote = quoteService.findById(idQuote);
		if(quote == null) 
			throw new QuoteCustomerServiceException(String.format(QUOTE_CUSTOMER_SERVICE_ALREADY_EXIST, code));
		if(!this.getEntityManager().createNamedQuery("QuoteCustomerService.findByCodeAndVersion")
										.setParameter("code", code)
											.setParameter("quoteVersion", 1)
												.getResultList().isEmpty()) {
			
		}
		
		final QuoteCustomerService qcs = new QuoteCustomerService();
		qcs.setCode(code);
		qcs.setQuote(quote);
		qcs.setName(name);
		qcs.setDuration(duration);
		qcs.setExecutionDate(executionDate);
		
		this.create(qcs);
		LOGGER.info("adding a new quote customer service [code : {}, quoteId : {}] ==> operation successful", code, idQuote);
		return qcs;
	}
	
}
