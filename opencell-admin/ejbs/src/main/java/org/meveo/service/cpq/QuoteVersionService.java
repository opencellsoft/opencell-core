package org.meveo.service.cpq;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;
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

	private final String MISSING_QUOTE_ID = "Missing quote for id %d";
	private final String MISSING_QUOTE_VERSION = "Missing quote version for code %d";
	private final String QUOTE_VERSION_STATUS_NOT_DRAFT = "you can not publish a quote version with status %s, the status must be DRAFT";
	private final String QUOTE_VERSION_ALREADY_CLOSED = "Quote version code %d is already closed";
	
//	private final String QUOTE_VERSION_ALREADY_EXIST_ALREADY = "Violation contraint : quote version code %s and version %d exist already";
	
	@Inject
	private QuoteService quoteService;
	
	public QuoteVersion createQuoteVersion(QuoteVersion quoteVersion) {
		LOGGER.info("adding new quote version attached to quote:({}) ", quoteVersion.getQuote().getCode());
		quoteVersion.setQuoteVersion(this.getLastVersionByCode(quoteVersion.getQuote().getCode()) + 1 );
		this.create(quoteVersion);
		LOGGER.info("adding new quote version attached to quote:({}) => operation successful ", quoteVersion.getQuote().getCode());
		return quoteVersion;
	}
	
	private int getLastVersionByCode(String codeVersion) {
		var quoteVersions = this.findLastVersionByCode(codeVersion);
		return quoteVersions.isEmpty() ? 0 : quoteVersions.get(0).getQuoteVersion();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuoteVersion> findLastVersionByCode(String code) {
			return this.getEntityManager().createNamedQuery("QuoteVersion.findByCode")
																			.setParameter("code", code).getResultList();
	}
	
	public QuoteVersion findByQuoteAndVersion(String codeQuote, int quoteVersion) {
		try {
			return (QuoteVersion) this.getEntityManager().createNamedQuery("QuoteVersion.findByQuoteAndVersion")
																.setParameter("code", codeQuote)
																	.setParameter("quoteVersion", quoteVersion)
																		.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<QuoteVersion> findByQuoteIdAndStatusActive(Long idQuote) {
			return (List<QuoteVersion>) this.getEntityManager().createNamedQuery("QuoteVersion.findByQuoteIdAndStatusActive")
															.setParameter("id", idQuote).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<QuoteVersion> findByQuoteId(Long idQuote) {
			return (List<QuoteVersion>) this.getEntityManager().createNamedQuery("QuoteVersion.findByQuoteId")
															.setParameter("id", idQuote).getResultList();
	}

	public int countByCode(String idQuote) {
			return (int) this.getEntityManager().createNamedQuery("QuoteVersion.countCode")
															.setParameter("code", idQuote).getSingleResult();
	}
	
    @SuppressWarnings("unchecked")
    public List<QuoteVersion>findByQuoteCode(String quoteCode) throws BusinessException {
        try {
            Query q = getEntityManager().createQuery("from QuoteVersion where quote.code =:quoteCode and status<>:status");
            q.setParameter("quoteCode", quoteCode);
            q.setParameter("status", VersionStatusEnum.CLOSED);
            List<QuoteVersion> versions = q.getResultList();
            log.info("findByQuoteCode: founds {} QuoteVersion with quoteCode={} and status<>{} ", versions.size(),quoteCode, "CLOSED" );
            return versions;
        } catch (Exception e) {
            return null;
        }
    }
	
	
	/**
	 * publish a quote version and closed all related version of quotes
	 * @param idQuote
	 * @param idQuoteVersion
	 * @param statusEnum
	 * @throws BusinessException : <ul>
	 * <li>QuoteVersion is missing</li>
	 * <li>Quote status is different to DRAFT status</li>
	 * <li>Quote is missing</li>
	 * </ul>
	 */
	public void publish(Long idQuote,Long idQuoteVersion, VersionStatusEnum statusEnum){
		LOGGER.info("publishing quoteversion id : {} attached to quote id : {}", idQuoteVersion, idQuote);
		final QuoteVersion quoteVersion = this.findById(idQuoteVersion);
		if(quoteVersion == null) 
			throw new BusinessException(String.format(MISSING_QUOTE_VERSION, idQuoteVersion));
		if(quoteVersion.getStatus() != VersionStatusEnum.DRAFT) {
			throw new BusinessException(String.format(QUOTE_VERSION_STATUS_NOT_DRAFT, quoteVersion.getStatus().toString()));
		}

		final Quote quote = quoteService.findById(idQuote);
		if(quote == null) {
			throw new BusinessException(String.format(MISSING_QUOTE_ID, idQuote));
		}
		findByQuoteIdAndStatusActive(idQuote).stream().forEach(q -> {
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
	 * @throws BusinessException if the quote version is null or the status is different to DRAFT status 
	 */
	public void updateQuoteVersion(QuoteVersion quoteVersion){
		if(quoteVersion == null)
			throw new BusinessException("Missing Quote Version");
		if(!quoteVersion.getStatus().equals(VersionStatusEnum.DRAFT))
			throw new BusinessException(String.format(QUOTE_VERSION_STATUS_NOT_DRAFT, quoteVersion.getStatus().toString()));

		this.update(quoteVersion);
	}
	
	public void close(QuoteVersion quoteVersion){
		if(quoteVersion == null)
			throw new BusinessException("Missing Quote Version");
		if(quoteVersion.getStatus().equals(VersionStatusEnum.CLOSED))
			throw new BusinessException(String.format(QUOTE_VERSION_ALREADY_CLOSED, quoteVersion.getStatus().toString()));
		this.update(quoteVersion);
	}
	
	/**
	 * @param idQuoteVersion
	 * @return
	 * @throws BusinessException
	 */
	public QuoteVersion duplicate(Long idQuoteVersion){
		final QuoteVersion q = this.findById(idQuoteVersion);
		if(q == null) 
			throw new BusinessException(String.format(MISSING_QUOTE_VERSION, idQuoteVersion));
		
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
