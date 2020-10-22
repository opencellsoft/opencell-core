package org.meveo.service.cpq;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.QuoteVersionException;
import org.meveo.service.quote.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 */
@Stateless
public class QuoteVersionService extends PersistenceService<QuoteVersion>   {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteVersionService.class);
	private final static int NEW_VERSION = 1;

	private final String MISSING_QUOTE = "Missing quote for code %s";
	private final String MISSING_QUOTE_ID = "Missing quote for id %d";
	private final String MISSING_QUOTE_VERSION = "Missing quote version for code %d";
	private final String QUOTE_VERSION_STATUS_NOT_DRAFT = "you can not publish a quote version with status %s, the status must be DRAFT";
	private final String QUOTE_VERSION_ALREADY_CLOSED = "Quote version code %d is already closed";
	
	private final String QUOTE_VERSION_ALREADY_EXIST_ALREADY = "Violation contraint : quote version code %s and version %d exist already";
	
	@Inject
	private QuoteService quoteService;
	
	public QuoteVersion addNewQuoteVersion(String codeQuote, String shortDescription, Date startDate, 
											Date endDate, String billingPlanCode) throws QuoteVersionException {
		LOGGER.info("adding new quote version attached to quote:({}) ", codeQuote);
		
		final Quote quote = quoteService.findByCode(codeQuote);
		if(quote == null) {
			throw new QuoteVersionException(String.format(MISSING_QUOTE, codeQuote));
		}
		
		QuoteVersion quoteVersion = this.findByCodeWithVersionOne(codeQuote);
		if(quoteVersion != null) {
				throw new QuoteVersionException(String.format(QUOTE_VERSION_ALREADY_EXIST_ALREADY, codeQuote, NEW_VERSION));
		}
		quoteVersion = new QuoteVersion();
		quoteVersion.setVersion(NEW_VERSION);
		quoteVersion.setStatus(VersionStatusEnum.DRAFT);
		quoteVersion.setStatusDate(Calendar.getInstance().getTime());
		quoteVersion.setBillingPlanCode(billingPlanCode);
		quoteVersion.setStartDate(startDate);
		quoteVersion.setEndDate(endDate);
		quoteVersion.setQuote(quote);
		quoteVersion.setVersion(NEW_VERSION);
		try {
			this.create(quoteVersion);
		}catch(BusinessException e) {
			throw new QuoteVersionException("Error while adding a new quote version", e);
		}
		LOGGER.info("adding new quote version attached to quote:({}) => operation successful ", codeQuote);
		return quoteVersion;
	}
	
	public QuoteVersion findByCodeWithVersionOne(String code) {
		try {
			return (QuoteVersion) this.getEntityManager().createNamedQuery("QuoteVersion.findByQuoteCodeAndQuoteVersion")
															.setParameter("code", code)
																.setParameter("quoteVersion", NEW_VERSION)
																	.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<QuoteVersion> findByQuoteId(Long idQuote) {
			return (List<QuoteVersion>) this.getEntityManager().createNamedQuery("QuoteVersion.findByQuoteIdAndStatusActive")
															.setParameter("id", idQuote).getResultList();
	}
	
	
	/**
	 * publish a quote version and closed all related version of quotes
	 * @param idQuote
	 * @param idQuoteVersion
	 * @param statusEnum
	 * @throws QuoteVersionException : <ul>
	 * <li>QuoteVersion is missing</li>
	 * <li>Quote status is different to DRAFT status</li>
	 * <li>Quote is missing</li>
	 * </ul>
	 */
	public void publish(Long idQuote,Long idQuoteVersion, VersionStatusEnum statusEnum) throws  QuoteVersionException{
		LOGGER.info("publishing quoteversion id : {} attached to quote id : {}", idQuoteVersion, idQuote);
		final QuoteVersion quoteVersion = this.findById(idQuoteVersion);
		if(quoteVersion == null) 
			throw new QuoteVersionException(String.format(MISSING_QUOTE_VERSION, idQuoteVersion));
		if(quoteVersion.getStatus() != VersionStatusEnum.DRAFT) {
			throw new QuoteVersionException(String.format(QUOTE_VERSION_STATUS_NOT_DRAFT, quoteVersion.getStatus().toString()));
		}

		final Quote quote = quoteService.findById(idQuote);
		if(quote == null) {
			throw new QuoteVersionException(String.format(MISSING_QUOTE_ID, idQuote));
		}
		findByQuoteId(idQuote).stream().forEach(q -> {
			q.setStatus(VersionStatusEnum.CLOSED);
			q.setStatusDate(Calendar.getInstance().getTime());
			this.update(q);
			
		});
		quoteVersion.setStatus(statusEnum);
		quoteVersion.setStatusDate(Calendar.getInstance().getTime());
		this.update(quoteVersion);
		LOGGER.info("publishing quoteversion id : {} attached to quote id : {} => operation successful", idQuoteVersion, idQuote);
	}
	
	/**
	 * only quote version's status is DRAFT can be updated
	 * @param quoteVersion
	 * @throws QuoteVersionException if the quote version is null or the status is different to DRAFT status 
	 */
	public void updateQuoteVersion(QuoteVersion quoteVersion) throws QuoteVersionException {
		if(quoteVersion == null)
			throw new QuoteVersionException("Missing Quote Version");
		if(!quoteVersion.getStatus().equals(VersionStatusEnum.DRAFT))
			throw new QuoteVersionException(String.format(QUOTE_VERSION_STATUS_NOT_DRAFT, quoteVersion.getStatus().toString()));

		this.update(quoteVersion);
	}
	
	public void close(QuoteVersion quoteVersion) throws QuoteVersionException {
		if(quoteVersion == null)
			throw new QuoteVersionException("Missing Quote Version");
		if(quoteVersion.getStatus().equals(VersionStatusEnum.CLOSED))
			throw new QuoteVersionException(String.format(QUOTE_VERSION_ALREADY_CLOSED, quoteVersion.getStatus().toString()));
		this.update(quoteVersion);
	}
	
	/**
	 * @param idQuoteVersion
	 * @return
	 * @throws QuoteVersionException
	 */
	public QuoteVersion duplicate(Long idQuoteVersion) throws QuoteVersionException{
		final QuoteVersion q = this.findById(idQuoteVersion);
		if(q == null) 
			throw new QuoteVersionException(String.format(MISSING_QUOTE_VERSION, idQuoteVersion));
		
		final QuoteVersion duplicate = new QuoteVersion();
		
		duplicate.setBillingPlanCode(q.getBillingPlanCode());
		duplicate.setEndDate(q.getEndDate());
		duplicate.setId(q.getId());
		duplicate.setQuote(q.getQuote());
		duplicate.setStartDate(q.getStartDate());
		duplicate.setStatus(VersionStatusEnum.DRAFT);
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		duplicate.setVersion(NEW_VERSION);
		
		this.create(duplicate);
		
		return duplicate;
	}
}
