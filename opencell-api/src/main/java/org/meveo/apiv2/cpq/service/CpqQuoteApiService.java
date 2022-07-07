package org.meveo.apiv2.cpq.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.cpq.QuoteProductService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.order.OpenOrderService;

@Stateless
public class CpqQuoteApiService {

	@Inject
	private OpenOrderService openOrderService;

	@Inject
	private QuoteVersionService quoteVersionService;

	@Inject
	private QuoteProductService quoteProductService;

	public Set<OpenOrder> findAvailableOpenOrders(String quoteCode) {
		List<QuoteVersion> quoteVersion = quoteVersionService.findLastVersionByCode(quoteCode);
		
		if(ListUtils.isEmtyCollection(quoteVersion)) {
			throw new BusinessException("Quote " + quoteCode + " doesn't exist");
		}
		QuoteVersion latestQuoteVersion = quoteVersion.get(0);
		
		List<QuoteProduct> quoteProducts = quoteProductService.findByQuoteVersion(latestQuoteVersion.getId());
		
		List<Product> product = quoteProducts.stream().map(qp -> qp.getProductVersion().getProduct()).collect(Collectors.toList());
		List<AccountingArticle> articles = latestQuoteVersion.getQuoteArticleLines().stream().map(QuoteArticleLine::getAccountingArticle).collect(Collectors.toList());

		// Getting OO for each Quote product
		Set<OpenOrder> avaiableOO = product.stream()
											.map(p -> openOrderService.checkAvailableOpenOrderForProduct(latestQuoteVersion.getQuote().getBillableAccount(), p, new Date()))
											.filter(oo -> oo.isPresent())
											.map(oo -> oo.get())
											.collect(Collectors.toSet());
		
		// Getting OO for each Quote article
		avaiableOO.addAll(articles.stream()
											.map(a -> openOrderService.checkAvailableOpenOrderForArticle(latestQuoteVersion.getQuote().getBillableAccount(), a, new Date()))
											.filter(oo -> oo.isPresent())
											.map(oo -> oo.get())
											.collect(Collectors.toSet()));

		// Group product and articles by OO
		// Map<Long, OpenOrder> openOrderMap = avaiableOO.stream().collect(Collectors.toMap(OpenOrder::getId, Function.identity()));

		return avaiableOO;
		
	}
	
}
