package org.meveo.service.order;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static org.meveo.model.ordering.OpenOrderStatusEnum.*;
import static org.meveo.model.ordering.OpenOrderTypeEnum.ARTICLES;
import static org.meveo.model.ordering.OpenOrderTypeEnum.PRODUCTS;
import static org.meveo.model.shared.DateUtils.setTimeToZero;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.meveo.commons.utils.ListUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.settings.impl.OpenOrderSettingService;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.*;

@Stateless
public class OpenOrderService extends BusinessService<OpenOrder> {

	@Inject
	private OpenOrderSettingService openOrderSettingService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

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
    	query.setParameter("productId", ofNullable(il.getServiceInstance()).map(si -> si.getProductVersion()).map(pv -> pv.getProduct().getId()).orElse(null));
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
	 * @param article
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
        if(setTimeToZero(openOrderQuote.getActivationDate()).after(setTimeToZero(new Date()))) {
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
        buildArticles(openOrder, openOrderQuote.getArticles());
        buildProducts(openOrder, openOrderQuote.getProducts());
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
            if(setTimeToZero(openOrderQuote.getEndOfValidityDate()).compareTo(setTimeToZero(new Date())) <= 0) {
                throw new BusinessException("End of validity date should be > current date");
            }
            if(setTimeToZero(openOrderQuote.getEndOfValidityDate()).compareTo(openOrderQuote.getActivationDate()) <= 0) {
                throw new BusinessException("End of validity date should be > activation date");
            }
        }
    }

    private void buildArticles(OpenOrder openOrder, List<OpenOrderArticle> openOrderArticles) {
        if (openOrderArticles != null && !openOrderArticles.isEmpty()) {
            List<OpenOrderArticle> oorArticles = new ArrayList<>();
            openOrderArticles.forEach(ooqArticle -> {
                OpenOrderArticle oorArticle = new OpenOrderArticle();
                oorArticle.setAccountingArticle(ooqArticle.getAccountingArticle());
                oorArticle.updateAudit(currentUser);
                oorArticle.setOpenOrderTemplate(ooqArticle.getOpenOrderTemplate());
                oorArticles.add(oorArticle);
            });

            openOrder.setArticles(oorArticles);
        }
    }

    private void buildProducts(OpenOrder openOrder, List<OpenOrderProduct> openOrderProducts) {
        if (openOrderProducts != null && !openOrderProducts.isEmpty()) {
            List<OpenOrderProduct> oorProducts = new ArrayList<>();
            openOrderProducts.forEach(ooqProduct -> {
                OpenOrderProduct oorProduct = new OpenOrderProduct();
                oorProduct.setProduct(ooqProduct.getProduct());
                oorProduct.updateAudit(currentUser);
                oorProduct.setOpenOrderTemplate(ooqProduct.getOpenOrderTemplate());
                oorProducts.add(oorProduct);
            });

            openOrder.setProducts(oorProducts);
        }
    }

    private void attachNestedEntities(OpenOrder openOrder) {

        if(openOrder.getThresholds() != null && !openOrder.getThresholds().isEmpty()) {
            openOrder.getThresholds().forEach(threshold -> threshold.setOpenOrder(openOrder));
        }
    }

    /**
     * List open orders by status a given status list
     * @param status
     * @return Open order ids list
     */
    public List<Long> listOpenOrderIdsByStatus(List<OpenOrderStatusEnum> status) {
        if(status == null || status.isEmpty()) {
            return emptyList();
        }
        return getEntityManager().createNamedQuery("OpenOrder.ListOOIdsByStatus")
                    .setParameter("status", status)
                    .getResultList();
    }

    /**
     * Update openOder status based on activation date / end of validity date / balance
     * @param openOrder
     * @return updated open order
     */
    public OpenOrder changeStatus(OpenOrder openOrder) {
        Date today = setTimeToZero(new Date());
        OpenOrderStatusEnum initialStatus = openOrder.getStatus();
        if (today.compareTo(setTimeToZero(openOrder.getActivationDate())) >= 0) {
            openOrder.setStatus(IN_USE);
        }
        if(openOrder.getEndOfValidityDate() != null && today.compareTo(openOrder.getEndOfValidityDate()) > 0) {
            openOrder.setStatus(EXPIRED);
        }
        if(openOrder.getBalance().compareTo(ZERO) == 0) {
            openOrder.setStatus(SOLD_OUT);
        }
        if(initialStatus != openOrder.getStatus()) {
            update(openOrder);
        }
        return openOrder;
    }

    public OpenOrder findByOpenOrderNumber(String openOrderNumber) {
        try {
            return (OpenOrder) getEntityManager()
                                .createNamedQuery("OpenOrder.findByOpenOrderNumber")
                                .setParameter("openOrderNumber", openOrderNumber)
                                .getSingleResult();
        } catch (Exception exception) {
            return null;
        }
    }
}