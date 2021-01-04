package org.meveo.service.cpq;

import java.util.Calendar;

import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.quote.QuoteStatusEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.CatalogHierarchyBuilderService;

public class CpqQuoteService extends BusinessService<CpqQuote> {

	@Inject private CatalogHierarchyBuilderService catalogHierarchyBuilderService;
	
	public CpqQuote duplicate(CpqQuote quote, QuoteVersion quoteVersion, boolean preserveCode, boolean duplicateHierarchy) {
		
		final CpqQuote duplicate = new CpqQuote(quote);
		duplicate.setStatus(QuoteStatusEnum.IN_PROGRESS);
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		detach(quote);
	   	 if(!preserveCode) {
	         String code = findDuplicateCode(duplicate);
	   	   	duplicate.setCode(code);
	   	 }
		 try {
		   	 	super.create(duplicate);
	   	 }catch(BusinessException e) {
	   		 throw new MeveoApiException(e);
	   	 }
		 
		 if(duplicateHierarchy) {
			 catalogHierarchyBuilderService.duplicateQuoteVersion(duplicate, quoteVersion);
		 }
		return duplicate;
	}
}
