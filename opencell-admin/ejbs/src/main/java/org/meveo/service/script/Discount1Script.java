package org.meveo.service.script;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Discount1Script extends org.meveo.service.script.Script{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8778628162325285616L;

	private static final Logger log = LoggerFactory.getLogger(Discount1Script.class);
	 public void execute(Map<String, Object> context) throws BusinessException {
		 log.info("EXECUTE context {}", context);
		 String paramCode = (String) context.get("minAmount");
		 log.info("paramCode={}",paramCode);
		 context.put(Script.RESULT_VALUE,false);
		 if(paramCode!=null && !paramCode.isBlank()) {
			final BigDecimal minAmount=new BigDecimal(paramCode);
		 
	     QuoteVersion qv = (QuoteVersion) context.get(Script.CONTEXT_ENTITY);
	     try {
	     	log.info("qv={}",qv.getId());
	     	
				if(qv!=null) {
					for (QuoteOffer quoteOffer:qv.getQuoteOffers()) {
						Optional<QuotePrice> offerQuoteAttribute=quoteOffer.getQuotePrices().stream()
	                    .filter(qp -> qp.getAmountWithoutTax().compareTo(minAmount)>0)
	                    .findAny();
						
						if(offerQuoteAttribute.isPresent()){
							context.put(Script.RESULT_VALUE,true);
							break;
						}
					}
				}
				log.info("result: {}" ,Script.RESULT_VALUE);

	     } catch(Exception e ) {
	         log.error("Exception:", e);
	         throw new BusinessException(e.getMessage());
	     }
		 }
	 }
	
}
