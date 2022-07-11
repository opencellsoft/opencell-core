package org.meveo.apiv2.cpq.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderArticleLine;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.cpq.QuoteVersionService;
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

	public Set<OpenOrder> findAvailableOpenOrders(String code) {
		
		BusinessEntity be = commercialOrderService.findBusinessEntityByCode(code);
		if(be == null) {
			throw new BusinessException("Commercial Order " + code + " doesn't exist");
		}
		CommercialOrder order = (CommercialOrder) be;
		List<OrderProduct> products = orderProductService.findOrderProductsByOrder(order.getId());
		List<OrderArticleLine> articleLines = orderArticleLineService.findByOrderId(order.getId());

		List<Product> product = products.stream().map(qp -> qp.getProductVersion().getProduct()).collect(Collectors.toList());
		List<AccountingArticle> articles = articleLines.stream().map(OrderArticleLine::getAccountingArticle).collect(Collectors.toList());

		// Getting OO for each Commercial Order product
		Set<OpenOrder> avaiableOO = product.stream()
											.map(p -> openOrderService.checkAvailableOpenOrderForProduct(order.getBillingAccount(), p, new Date()))
											.filter(oo -> oo.isPresent())
											.map(oo -> oo.get())
											.collect(Collectors.toSet());
		
		// Getting OO for each Commercial Order article
		avaiableOO.addAll(articles.stream()
									.map(a -> openOrderService.checkAvailableOpenOrderForArticle(order.getBillingAccount(), a, new Date()))
									.filter(oo -> oo.isPresent())
									.map(oo -> oo.get())
									.collect(Collectors.toSet()));


		return avaiableOO;
		
	}
	
}
