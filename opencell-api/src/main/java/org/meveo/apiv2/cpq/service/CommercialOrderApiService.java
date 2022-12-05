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
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderArticleLine;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderArticleLineService;
import org.meveo.service.cpq.order.OrderProductService;
import org.meveo.service.order.OpenOrderService;

@Stateless
public class CommercialOrderApiService {

	@Inject
	private OpenOrderService openOrderService;

	@Inject
	private OrderProductService orderProductService;

	@Inject
	private OrderArticleLineService orderArticleLineService;

	@Inject
	private CommercialOrderService commercialOrderService;

	public List<ImmutableAvailableOpenOrder> findAvailableOpenOrders(String code) {
		
		BusinessEntity be = commercialOrderService.findBusinessEntityByCode(code);
		if(be == null) {
			throw new BusinessException("Commercial Order " + code + " doesn't exist");
		}
		CommercialOrder order = (CommercialOrder) be;
		List<OrderProduct> products = orderProductService.findOrderProductsByOrder(order.getId());
		List<OrderArticleLine> articleLines = orderArticleLineService.findByOrderId(order.getId());

		List<Product> product = products.stream().map(qp -> qp.getProductVersion().getProduct()).collect(Collectors.toList());
		List<AccountingArticle> articles = articleLines.stream().map(OrderArticleLine::getAccountingArticle).collect(Collectors.toList());

		// Getting OO for each Order product
		Map<OpenOrder, Set<Product>> ooForProducts = product.stream()
								.map(p -> new AbstractMap.SimpleEntry<>(openOrderService.checkAvailableOpenOrderForProduct(order.getBillingAccount(), p, new Date()).orElse(null), p))
								.filter(e -> e.getKey() != null)
								.collect(Collectors.groupingBy(e -> e.getKey(), Collectors.mapping(e -> e.getValue(), Collectors.toSet())));
		
		// Getting OO for each Order article
		Map<OpenOrder, Set<AccountingArticle>> ooForArticles = articles.stream()
								.map(a -> new AbstractMap.SimpleEntry<>(openOrderService.checkAvailableOpenOrderForArticle(order.getBillingAccount(), a, new Date()).orElse(null), a))
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
