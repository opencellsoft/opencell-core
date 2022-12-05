package org.meveo.apiv2.cpq.service;

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.ordering.resource.oo.ImmutableAvailableOpenOrder;
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

	public List<ImmutableAvailableOpenOrder> findAvailableOpenOrders(String quoteCode) {
		List<QuoteVersion> quoteVersion = quoteVersionService.findLastVersionByCode(quoteCode);
		
		if(ListUtils.isEmtyCollection(quoteVersion)) {
			throw new BusinessException("Quote " + quoteCode + " doesn't exist");
		}
		QuoteVersion latestQuoteVersion = quoteVersion.get(0);
		
		List<QuoteProduct> quoteProducts = quoteProductService.findByQuoteVersion(latestQuoteVersion.getId());
		
		List<Product> product = quoteProducts.stream().map(qp -> qp.getProductVersion().getProduct()).collect(Collectors.toList());
		List<AccountingArticle> articles = latestQuoteVersion.getQuoteArticleLines().stream().map(QuoteArticleLine::getAccountingArticle).collect(Collectors.toList());

		// Getting OO for each Quote product
		;
		Map<OpenOrder, Set<Product>> ooForProducts = product.stream()
				.map(p -> new AbstractMap.SimpleEntry<>(openOrderService.checkAvailableOpenOrderForProduct(latestQuoteVersion.getQuote().getBillableAccount(), p, new Date()).orElse(null), p))
				.filter(e -> e.getKey() != null)
				.collect(Collectors.groupingBy(e -> e.getKey(), Collectors.mapping(e -> e.getValue(), Collectors.toSet())));
		
		// Getting OO for each Quote article
		Map<OpenOrder, Set<AccountingArticle>> ooForArticles = articles.stream()
				.map(a -> new AbstractMap.SimpleEntry<>(openOrderService.checkAvailableOpenOrderForArticle(latestQuoteVersion.getQuote().getBillableAccount(), a, new Date()).orElse(null), a))
				.filter(e -> e.getKey() != null)
				.collect(Collectors.groupingBy(e -> e.getKey(), Collectors.mapping(e -> e.getValue(), Collectors.toSet())));

		// Group OpenOrders by Order's products
		List<ImmutableAvailableOpenOrder> lResult = ooForProducts.entrySet().stream()
				.map(oo -> ImmutableAvailableOpenOrder.builder()
												.openOrderId(oo.getKey().getId())
												.openOrderNumber(oo.getKey().getOpenOrderNumber())
												.startDate(oo.getKey().getActivationDate())
												.externalReference(Optional.ofNullable(oo.getKey().getExternalReference()).orElse(""))
												.addAllProducts(oo.getValue().stream().map(Product::getId).collect(Collectors.toList()))
												.build()).collect(Collectors.toList());

		// Group OpenOrders by Order's articles
		lResult.addAll(ooForArticles.entrySet().stream()
									.map(oo -> ImmutableAvailableOpenOrder.builder()
												.openOrderId(oo.getKey().getId())
												.openOrderNumber(oo.getKey().getOpenOrderNumber())
												.startDate(oo.getKey().getActivationDate())
												.externalReference(Optional.ofNullable(oo.getKey().getExternalReference()).orElse(""))
												.addAllArticles(oo.getValue().stream().map(AccountingArticle::getId).collect(Collectors.toList()))
												.build())
									.collect(Collectors.toList()));

		return lResult;
		
	}
	
}
