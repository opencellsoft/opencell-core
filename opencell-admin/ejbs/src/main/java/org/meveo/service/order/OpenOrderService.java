package org.meveo.service.order;

import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.ordering.OpenOrderStatusEnum.IN_USE;
import static org.meveo.model.ordering.OpenOrderStatusEnum.NEW;
import static org.meveo.model.ordering.OpenOrderTypeEnum.ARTICLES;
import static org.meveo.model.ordering.OpenOrderTypeEnum.PRODUCTS;
import static org.meveo.model.shared.DateUtils.setTimeToZero;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.meveo.commons.utils.ListUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.settings.impl.OpenOrderSettingService;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.*;

import java.util.Date;
import java.util.Optional;

@Stateless
public class OpenOrderService extends BusinessService<OpenOrder> {

	@Inject
	private OpenOrderSettingService openOrderSettingService;

    @Inject
    private ServiceSingleton serviceSingleton;

	/**
	 * Find Open orders compatible with InvoiceLine in parameter.
	 * 
	 * @param il : InvoiceLine
	 * @return
	 */
	public OpenOrder findOpenOrderCompatibleForIL(InvoiceLine il) {
		
    	TypedQuery<OpenOrder> query = getEntityManager().createNamedQuery("OpenOrder.getOpenOrderCompatibleForIL", OpenOrder.class);

    	query.setParameter("billingAccountId", il.getBillingAccount().getId());
    	query.setParameter("ilAmountWithTax", il.getAmountWithTax());
    	query.setParameter("status", OpenOrderStatusEnum.CANCELED);
    	query.setParameter("ilValueDate", il.getValueDate());
    	query.setParameter("productId", ofNullable(il.getProductVersion()).map(ilp -> ilp.getProduct().getId()).orElse(null));
    	query.setParameter("articleId", ofNullable(il.getAccountingArticle()).map(ila -> ila.getId()).orElse(null));
    	
    	List<OpenOrder> result = query.getResultList();
    	
    	if(!ListUtils.isEmtyCollection(result)) {
			return result.get(0);
    	}
    	
    	return null;
    }

	/**
	 * Check and retrieve available OpenOrder for given Billing account and product
	 * 
	 * @param billingAccount
	 * @param product
	 * @param eventDate
	 * @return
	 */
	public Optional<OpenOrder> checkAvailableOpenOrderForProduct(BillingAccount billingAccount, Product product, Date eventDate) {
		OpenOrderSetting findLastOne = openOrderSettingService.findLastOne();
		
		if(findLastOne != null && findLastOne.getUseOpenOrders()) {
			TypedQuery<OpenOrder> query = getEntityManager().createNamedQuery("OpenOrder.availableOOForProduct", OpenOrder.class);
	    	query.setParameter("billingAccountId", billingAccount.getId());
	    	query.setParameter("status", OpenOrderStatusEnum.CANCELED);
	    	query.setParameter("eventDate", eventDate);
	    	query.setParameter("productId", ofNullable(product).map(Product::getId).orElse(null));

	    	List<OpenOrder> result = query.getResultList();
	    	if(!ListUtils.isEmtyCollection(result)) {
	    		return of(result.get(0));
	    	}
		}

		return empty();
	}

	/**
	 * Check and retrieve available OpenOrder for given Billing account and article
	 * 
	 * @param billingAccount
	 * @param product
	 * @param eventDate
	 * @return
	 */
	public Optional<OpenOrder> checkAvailableOpenOrderForArticle(BillingAccount billingAccount, AccountingArticle article, Date eventDate) {
		OpenOrderSetting findLastOne = openOrderSettingService.findLastOne();
		
		if(findLastOne != null && findLastOne.getUseOpenOrders()) {
			TypedQuery<OpenOrder> query = getEntityManager().createNamedQuery("OpenOrder.availableOOForArticle", OpenOrder.class);
	    	query.setParameter("billingAccountId", billingAccount.getId());
	    	query.setParameter("status", OpenOrderStatusEnum.CANCELED);
	    	query.setParameter("eventDate", eventDate);
	    	query.setParameter("articleId", ofNullable(article).map(AccountingArticle::getId).orElse(null));

	    	List<OpenOrder> result = query.getResultList();
	    	if(!ListUtils.isEmtyCollection(result)) {
	    		return of(result.get(0));
	    	}
		}

		return empty();
	}

	/**
	 * Create open order from an open order quote
	 *
	 * @param openOrderQuote : InvoiceLine
	 * @return OpenOrder
	 */
    public OpenOrder create(OpenOrderQuote openOrderQuote) {
        checkMandatoryFields(openOrderQuote);
        validate(openOrderQuote);
        OpenOrder openOrder = new OpenOrder();
        openOrder.setExternalReference(openOrderQuote.getExternalReference());
        if(openOrderQuote.getActivationDate().compareTo(setTimeToZero(new Date())) < 0) {
            openOrder.setStatus(NEW);
        } else {
            openOrder.setStatus(IN_USE);
        }
        openOrder.setCode(openOrderQuote.getCode() + new Date().getTime());
        openOrder.setDescription(openOrderQuote.getDescription());
        openOrder.setBillingAccount(openOrderQuote.getBillingAccount());
        openOrder.setType(openOrderQuote.getOpenOrderType());
        openOrder.setOpenOrderTemplate(openOrderQuote.getOpenOrderTemplate());
        openOrder.setOpenOrderNumber(serviceSingleton.getNextOpenOrderSequence());
        openOrder.setInitialAmount(openOrderQuote.getMaxAmount());
        openOrder.setBalance(openOrderQuote.getMaxAmount());
        openOrder.setCurrency(openOrderQuote.getCurrency());
        openOrder.setOpenOrderQuote(openOrderQuote);
        if(openOrderQuote.getTags() != null) {
            openOrder.setTags(new ArrayList<>(openOrderQuote.getTags()));
        }
        if (openOrder.getThresholds() != null) {
            openOrder.setThresholds(new ArrayList<>(openOrderQuote.getThresholds()));
        }
        openOrder.setArticles(buildArticles(openOrderQuote.getArticles()).orElse(null));
        openOrder.setProducts(buildProducts(openOrderQuote.getProducts()).orElse(null));
        openOrder.setActivationDate(openOrderQuote.getActivationDate());
        openOrder.setEndOfValidityDate(openOrderQuote.getEndOfValidityDate());
        attachNestedEntities(openOrder);
        create(openOrder);
        return openOrder;
    }

    private void checkMandatoryFields(OpenOrderQuote openOrderQuote) {
        List<String> missingFields = new ArrayList<>();
        if(openOrderQuote.getStatus() == null) {
            missingFields.add("status");
        }
        if (openOrderQuote.getOpenOrderType() == null) {
            missingFields.add("type");
        }
        if (openOrderQuote.getBillingAccount() == null) {
            missingFields.add("billingAccount");
        }
        if (openOrderQuote.getOpenOrderTemplate() == null) {
            missingFields.add("openOrderTemplate");
        }
        if (openOrderQuote.getMaxAmount() == null) {
            missingFields.add("maxAmount");
        }
        if (openOrderQuote.getCurrency() == null) {
            missingFields.add("currency");
        }
        if (openOrderQuote.getActivationDate() == null) {
            missingFields.add("activationDate");
        }
        if(missingFields.size() > 0) {
            throw new BusinessException(buildErrorMessage(missingFields) + " required for creating open order");
        }
    }

    private String buildErrorMessage(List<String> missingFields) {
        String errorMessage = missingFields.stream().collect(joining(","));
        if(missingFields.size() == 1) {
            errorMessage = errorMessage.concat(" is");
        } else  {
            errorMessage = errorMessage.concat(" are");
        }
        return errorMessage;
    }

    private void validate(OpenOrderQuote openOrderQuote) {
        if(openOrderQuote.getOpenOrderType() == ARTICLES
                && (openOrderQuote.getArticles() == null || openOrderQuote.getArticles().isEmpty())) {
            throw new BusinessException("At least an article must be linked to the open order when open order type is ARTICLES");
        }
        if(openOrderQuote.getOpenOrderType() == PRODUCTS
                && (openOrderQuote.getProducts() == null || openOrderQuote.getProducts().isEmpty())) {
            throw new BusinessException("At least a product must be linked to the open order when open order type is PRODUCTS");
        }
        if(openOrderQuote.getEndOfValidityDate() != null) {
            if(openOrderQuote.getEndOfValidityDate().compareTo(setTimeToZero(new Date())) <= 0) {
                throw new BusinessException("End of validity date should be > current date");
            }
            if(openOrderQuote.getEndOfValidityDate().compareTo(openOrderQuote.getActivationDate()) <= 0) {
                throw new BusinessException("End of validity date should be > activation date");
            }
        }
    }

    private Optional<List<AccountingArticle>> buildArticles(List<OpenOrderArticle> openOrderArticles) {
        if(openOrderArticles != null && !openOrderArticles.isEmpty()) {
            return of(openOrderArticles.stream()
                            .map(OpenOrderArticle::getAccountingArticle)
                            .collect(toList()));
        } else {
            return empty();
        }
    }

    private Optional<List<Product>> buildProducts(List<OpenOrderProduct> openOrderProducts) {
        if(openOrderProducts != null && !openOrderProducts.isEmpty()) {
            return of(openOrderProducts.stream()
                            .map(OpenOrderProduct::getProduct)
                            .collect(toList()));
        } else {
            return empty();
        }
    }

    private void attachNestedEntities(OpenOrder openOrder) {
        if(openOrder.getTags() != null && !openOrder.getTags().isEmpty()) {
            openOrder.getTags().forEach(tag -> tag.setOpenOrder(openOrder));
        }
        if(openOrder.getThresholds() != null && !openOrder.getThresholds().isEmpty()) {
            openOrder.getThresholds().forEach(threshold -> threshold.setOpenOrder(openOrder));
        }
        if(openOrder.getArticles() != null && !openOrder.getArticles().isEmpty()) {
            openOrder.getArticles().forEach(article -> article.setOpenOrder(openOrder));
        }
        if(openOrder.getProducts() != null && !openOrder.getProducts().isEmpty()) {
            openOrder.getProducts().forEach(product -> product.setOpenOrder(openOrder));
        }
    }
}