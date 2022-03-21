package org.meveo.service.billing.impl;

import static java.util.Collections.emptyList;
import static org.meveo.model.billing.InvoiceLineStatusEnum.OPEN;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.IL_MIN_AMOUNT_BA;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.IL_MIN_AMOUNT_CA;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.IL_MIN_AMOUNT_CUST;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.IL_MIN_AMOUNT_SE;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.IL_MIN_AMOUNT_SU;
import static org.meveo.model.cpq.commercial.InvoiceLineMinAmountTypeEnum.IL_MIN_AMOUNT_UA;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.ApplyMinimumModeEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ExtraMinAmount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.MinAmountData;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.MinAmountsResult;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.crm.Customer;
import org.meveo.model.filter.Filter;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;
@Stateless
public class InvoiceLineService extends PersistenceService<InvoiceLine> {

    private static final String INVOICING_PROCESS_TYPE = "InvoiceLine";
    private static final String INVOICE_MINIMUM_COMPLEMENT_CODE = "MIN-STD";

    @Inject
    private FilterService filterService;

    @Inject
    private TaxMappingService taxMappingService;

    @Inject
    private AccountingArticleService accountingArticleService;

    @Inject
    private MinAmountService minAmountService;
    
    @Inject
    private CpqQuoteService cpqQuoteService;
    
    @Inject
    private CommercialOrderService commercialOrderService;
    
    @Inject
    private DiscountPlanItemService discountPlanItemService;
    
    @Inject
    private DiscountPlanService discountPlanService;
    
    
    public List<InvoiceLine> findByQuote(CpqQuote quote) {
        return getEntityManager().createNamedQuery("InvoiceLine.findByQuote", InvoiceLine.class)
                .setParameter("quote", quote)
                .getResultList();
    }

    public List<InvoiceLine> findByCommercialOrder(CommercialOrder commercialOrder) {
        return getEntityManager().createNamedQuery("InvoiceLine.findByCommercialOrder", InvoiceLine.class)
                .setParameter("commercialOrder", commercialOrder)
                .getResultList();
    }
    @Override
    public void create(InvoiceLine entity) throws BusinessException {
    	AccountingArticle accountingArticle=entity.getAccountingArticle();
    	Invoice invoice=entity.getInvoice();
    	Date date=new Date();
    	if(entity.getValueDate()!=null) {
    		date=entity.getValueDate();
    	}
    	Seller seller=null;
    	BillingAccount billingAccount=null;
    	if(invoice!=null) {
    	   if(invoice.getInvoiceDate()!=null) {
        		date=invoice.getInvoiceDate();
        	}
    	 seller=invoice.getSeller()!=null?invoice.getSeller():invoice.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
    	 billingAccount=invoice.getBillingAccount();
    	}
    	 else if (entity.getBillingAccount()!=null) {
    		 seller=entity.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
    		 billingAccount=entity.getBillingAccount();
    	 }
    	 if(accountingArticle!=null) {
             TaxInfo taxInfo = taxMappingService.determineTax(accountingArticle.getTaxClass(), seller, billingAccount,null, date, false, false);
             if(taxInfo!=null)
            	 entity.setTax(taxInfo.tax);
            }
    	super.create(entity);
    	
    	 if(entity.getDiscountPlan() != null) {
         	addDiscountPlanInvoice( entity.getDiscountPlan(), entity, billingAccount, invoice, accountingArticle, seller);
         }
    }
    
    private void addDiscountPlanInvoice(DiscountPlan discount, InvoiceLine entity, BillingAccount billingAccount, Invoice invoice, AccountingArticle accountingArticle, Seller seller) {
    	var isDiscountApplicable = discountPlanService.isDiscountPlanApplicable(billingAccount, discount, null, null, null, null, invoice.getInvoiceDate());
    	if(isDiscountApplicable) {
    		List<DiscountPlanItem> discountItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, entity.getDiscountPlan(), null, null, null, null, accountingArticle, null, null);
            BigDecimal invoiceLineDiscountAmount = addDiscountPlanInvoiceLine(discountItems, entity, invoice, billingAccount, seller);
            entity.setDiscountAmount(invoiceLineDiscountAmount);
    	}
    }
    
    public BigDecimal addDiscountPlanInvoiceLine(List<DiscountPlanItem> discountItems,InvoiceLine invoiceLine, Invoice invoice, BillingAccount billingAccount, Seller seller) {
        BigDecimal invoiceLineDiscountAmount = BigDecimal.ZERO;
        for (DiscountPlanItem discountPlanItem : discountItems) {
        	InvoiceLine discountInvoice = new InvoiceLine(invoiceLine, invoice);
        	discountInvoice.setStatus(invoiceLine.getStatus());
            if(discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.FIXED) {
                invoiceLineDiscountAmount = invoiceLineDiscountAmount.add(discountPlanItem.getDiscountValue());
            } else {
                //invoiceLineDiscountAmount = invoiceLineDiscountAmount.add((discountPlanItem.getDiscountValue().divide(hundred)).multiply(entity.getAmountWithoutTax()));
                BigDecimal taxPercent = invoiceLine.getTaxRate();
                if(invoiceLine.getAccountingArticle() != null) {
                	TaxInfo taxInfo = taxMappingService.determineTax(invoiceLine.getAccountingArticle().getTaxClass(), seller, billingAccount, null, invoice.getInvoiceDate(), false, false);
                        taxPercent = taxInfo.tax.getPercent();
                }
                BigDecimal discountAmount = discountPlanItemService.getDiscountAmount(invoiceLine.getUnitPrice(), discountPlanItem,null, Collections.emptyList());
                if(discountAmount != null) {
                	invoiceLineDiscountAmount = invoiceLineDiscountAmount.add(discountAmount);
        	  	}
                BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(invoiceLineDiscountAmount, invoiceLineDiscountAmount, taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                var quantity = invoiceLine.getQuantity();
                discountInvoice.setUnitPrice(invoiceLineDiscountAmount);
                discountInvoice.setAmountWithoutTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[0]):BigDecimal.ZERO);
                discountInvoice.setAmountWithTax(quantity.multiply(amounts[1]));
                discountInvoice.setDiscountPlan(null);
                discountInvoice.setDiscountedInvoiceLine(invoiceLine);
                discountInvoice.setAmountTax(quantity.multiply(amounts[2]));
                discountInvoice.setTaxRate(taxPercent);
            	super.create(discountInvoice);
            }
        }
        return invoiceLineDiscountAmount;
    }

    public List<InvoiceLine> listInvoiceLinesToInvoice(IBillableEntity entityToInvoice, Date firstTransactionDate,
                                                       Date lastTransactionDate, Filter filter, int pageSize) throws BusinessException {
        if (filter != null) {
            return (List<InvoiceLine>) filterService.filteredListAsObjects(filter, null);

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceBySubscription", InvoiceLine.class)
                    .setParameter("subscriptionId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        } else if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByBillingAccount", InvoiceLine.class)
                    .setParameter("billingAccountId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByOrderNumber", InvoiceLine.class)
                    .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        }else if (entityToInvoice instanceof CommercialOrder) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByCommercialOrder", InvoiceLine.class)
                    .setParameter("commercialOrderId", ((CommercialOrder) entityToInvoice).getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        }else if (entityToInvoice instanceof CpqQuote) {
            return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByQuote", InvoiceLine.class)
                    .setParameter("quoteId", entityToInvoice.getId())
                    .setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate)
                    .setHint("org.hibernate.readOnly", true)
                    .setMaxResults(pageSize)
                    .getResultList();
        }
        return emptyList();
    }
    
    public List<InvoiceLine> listInvoiceLinesByInvoice(long invoiceId) {
        try {
            return getEntityManager().createNamedQuery("InvoiceLine.InvoiceLinesByInvoiceID", InvoiceLine.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();
        } catch (NoResultException e) {
            log.warn("No invoice found for the provided Invoice : " + invoiceId);
            return emptyList();
        }
    }
    public void createInvoiceLine(IBillableEntity entityToInvoice, AccountingArticle accountingArticle, ProductVersion productVersion,OrderLot orderLot,OfferTemplate offerTemplate,OrderOffer orderOffer, BigDecimal amountWithoutTaxToBeInvoiced, BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        
    	InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setAccountingArticle(accountingArticle);
        invoiceLine.setLabel(accountingArticle.getDescription()); 
        invoiceLine.setProduct(productVersion.getProduct());
        invoiceLine.setProductVersion(productVersion);
        invoiceLine.setOrderLot(orderLot);
        invoiceLine.setOfferTemplate(offerTemplate);
        invoiceLine.setOrderOffer(orderOffer);
        invoiceLine.setQuoteOffer(orderOffer.getQuoteOffer());
        if (entityToInvoice instanceof CpqQuote) {
        entityToInvoice = cpqQuoteService.retrieveIfNotManaged((CpqQuote) entityToInvoice);
        CpqQuote quote =((CpqQuote) entityToInvoice);
        invoiceLine.setQuote(quote);
        invoiceLine.setBillingAccount(quote.getBillableAccount());
        }
        CommercialOrder commercialOrder=null;
        if (entityToInvoice instanceof CommercialOrder) {
        	entityToInvoice = commercialOrderService.retrieveIfNotManaged((CommercialOrder) entityToInvoice);
        	commercialOrder = ((CommercialOrder) entityToInvoice);
        	invoiceLine.setCommercialOrder(commercialOrder);
        	invoiceLine.setOrderNumber(commercialOrder.getOrderNumber());
        	invoiceLine.setBillingAccount(commercialOrder.getBillingAccount());
        }
        invoiceLine.setQuantity(BigDecimal.valueOf(1));
        invoiceLine.setUnitPrice(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithoutTax(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithTax(amountWithTaxToBeInvoiced);
        invoiceLine.setAmountTax(taxAmountToBeInvoiced);
        invoiceLine.setTaxRate(totalTaxRate);
      
        invoiceLine.setValueDate(new Date());
        if(accountingArticle!=null && commercialOrder!=null) {
         TaxInfo taxInfo = taxMappingService.determineTax(accountingArticle.getTaxClass(), commercialOrder.getSeller(), commercialOrder.getBillingAccount(),commercialOrder.getUserAccount() , commercialOrder.getOrderDate(), false, false);
         if(taxInfo!=null)
         invoiceLine.setTax(taxInfo.tax);
        }
        create(invoiceLine);
    }

    public void calculateAmountsAndCreateMinAmountLines(IBillableEntity billableEntity, Date lastTransactionDate,
    		boolean calculateAndUpdateTotalAmounts, MinAmountForAccounts minAmountForAccounts) throws BusinessException {
    	Amounts totalInvoiceableAmounts;
    	List<InvoiceLine> minAmountLines = new ArrayList<>();
    	List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();
    	Date minRatingDate = addDaysToDate(lastTransactionDate, -1);

    	if (billableEntity instanceof Order) {
    		if (calculateAndUpdateTotalAmounts) {
    			totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
    		}
    	}else  if (billableEntity instanceof CpqQuote) {
    		if (calculateAndUpdateTotalAmounts) {
    			totalInvoiceableAmounts = computeTotalQuoteAmount((CpqQuote) billableEntity, new Date(0), lastTransactionDate);
    		}
    	}else {
    		BillingAccount billingAccount =
    				(billableEntity instanceof Subscription) ? ((Subscription) billableEntity).getUserAccount().getBillingAccount() : (BillingAccount) billableEntity;
    		Class[] accountClasses = new Class[] { ServiceInstance.class, Subscription.class,
    				UserAccount.class, BillingAccount.class, CustomerAccount.class, Customer.class };
    		for (Class accountClass : accountClasses) {
    			if (minAmountForAccounts.isMinAmountForAccountsActivated(accountClass, billableEntity)) {
    				MinAmountsResult minAmountsResults = createMinILForAccount(billableEntity, billingAccount,
    						lastTransactionDate, minRatingDate, extraMinAmounts, accountClass);
    				extraMinAmounts = minAmountsResults.getExtraMinAmounts();
    				minAmountLines.addAll(minAmountsResults.getMinAmountInvoiceLines());
    			}
    		}
    		totalInvoiceableAmounts =
    				minAmountService.computeTotalInvoiceableAmount(billableEntity, new Date(0), lastTransactionDate, INVOICING_PROCESS_TYPE);
    		final Amounts totalAmounts = new Amounts();
    		extraMinAmounts.forEach(extraMinAmount -> {
    			extraMinAmount.getCreatedAmount().values().forEach(amounts -> {
    				totalAmounts.addAmounts(amounts);
    			});
    		});
    		totalInvoiceableAmounts.addAmounts(totalAmounts);
    	}
    }

    private Amounts computeTotalOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate) {
        String queryString = "InvoiceLine.sumTotalInvoiceableByOrderNumber";
        Query query = getEntityManager().createNamedQuery(queryString)
                .setParameter("orderNumber", order.getOrderNumber())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
        return (Amounts) query.getSingleResult();
    }
    
    private Amounts computeTotalQuoteAmount(CpqQuote quote, Date firstTransactionDate, Date lastTransactionDate) {
        String queryString = "InvoiceLine.sumTotalInvoiceableByQuote";
        Query query = getEntityManager().createNamedQuery(queryString)
                .setParameter("quoteId", quote.getId())
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate);
        return (Amounts) query.getSingleResult();
    }

    private MinAmountsResult createMinILForAccount(IBillableEntity billableEntity, BillingAccount billingAccount,
                                                   Date lastTransactionDate, Date minRatingDate, List<ExtraMinAmount> extraMinAmounts,
                                                   Class accountClass) throws BusinessException {
        MinAmountsResult minAmountsResult = new MinAmountsResult();
        Map<Long, MinAmountData> accountToMinAmount =
                minAmountService.getInvoiceableAmountDataPerAccount(billableEntity, billingAccount, lastTransactionDate, extraMinAmounts, accountClass, INVOICING_PROCESS_TYPE);
        accountToMinAmount = minAmountService.prepareAccountsWithMinAmount(billableEntity, billingAccount, extraMinAmounts, accountClass, accountToMinAmount);

        for (Map.Entry<Long, MinAmountData> accountAmounts : accountToMinAmount.entrySet()) {
            Map<String, Amounts> minILAmountMap = new HashMap<>();
            if (accountAmounts.getValue() == null || accountAmounts.getValue().getMinAmount() == null) {
                continue;
            }
            BigDecimal minAmount = accountAmounts.getValue().getMinAmount();
            String minAmountLabel = accountAmounts.getValue().getMinAmountLabel();
            BigDecimal totalInvoiceableAmount =
                    appProvider.isEntreprise() ? accountAmounts.getValue().getAmounts().getAmountWithoutTax() : accountAmounts.getValue().getAmounts().getAmountWithTax();
            BusinessEntity entity = accountAmounts.getValue().getEntity();
            Seller seller = accountAmounts.getValue().getSeller();
            if (seller == null) {
                throw new BusinessException("Default Seller is mandatory for invoice minimum (Customer.seller)");
            }
            String mapKeyPrefix = seller.getId().toString() + "_";
            BigDecimal diff = minAmount.subtract(totalInvoiceableAmount);
            if (diff.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            AccountingArticle defaultMinAccountingArticle = getDefaultAccountingArticle();
            if (defaultMinAccountingArticle == null) {
                log.error("No default AccountingArticle defined");
                continue;
            }
            InvoiceSubCategory invoiceSubCategory = defaultMinAccountingArticle.getInvoiceSubCategory();
            String mapKey = mapKeyPrefix + invoiceSubCategory.getId();
            TaxMappingService.TaxInfo taxInfo = taxMappingService.determineTax(defaultMinAccountingArticle.getTaxClass(), seller, billingAccount,
                    null, minRatingDate, true, false);
            InvoiceLine invoiceLine = createInvoiceLine( minAmountLabel, billableEntity, billingAccount, minRatingDate,
                    entity, seller, defaultMinAccountingArticle, taxInfo, diff);
            minAmountsResult.addMinAmountIL(invoiceLine);
            minILAmountMap.put(mapKey, new Amounts(invoiceLine.getAmountWithoutTax(), invoiceLine.getAmountWithTax(), invoiceLine.getAmountTax()));
            extraMinAmounts.add(new ExtraMinAmount(entity, minILAmountMap));
        }
        minAmountsResult.setExtraMinAmounts(extraMinAmounts);
        return minAmountsResult;
    }

    private AccountingArticle getDefaultAccountingArticle() {
        AccountingArticle accountingArticle = accountingArticleService.findByCode(INVOICE_MINIMUM_COMPLEMENT_CODE);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, INVOICE_MINIMUM_COMPLEMENT_CODE);
        return accountingArticle;
    }

    private String getMinAmountInvoiceLineCode(BusinessEntity entity, Class accountClass) {
        StringBuilder prefix = new StringBuilder("");
        if (accountClass.equals(ServiceInstance.class)) {
            prefix.append(IL_MIN_AMOUNT_SE.getCode());
        }
        if (accountClass.equals(Subscription.class)) {
            prefix.append(IL_MIN_AMOUNT_SU.getCode());
        }
        if (accountClass.equals(UserAccount.class)) {
            prefix.append(IL_MIN_AMOUNT_UA.getCode());
        }
        if (accountClass.equals(BillingAccount.class)) {
            prefix.append(IL_MIN_AMOUNT_BA.getCode());
        }
        if (accountClass.equals(CustomerAccount.class)) {
            prefix.append(IL_MIN_AMOUNT_CA.getCode());
        }
        if (accountClass.equals(Customer.class)) {
            prefix.append(IL_MIN_AMOUNT_CUST.getCode());
        }
        return prefix.append("_")
                .append(entity.getCode())
                .toString();
    }

    private InvoiceLine createInvoiceLine( String minAmountLabel, IBillableEntity billableEntity, BillingAccount billingAccount, Date minRatingDate,
                                          BusinessEntity entity, Seller seller, AccountingArticle defaultAccountingArticle,
                                          TaxMappingService.TaxInfo taxInfo, BigDecimal ilMinAmount) {
        Tax tax = taxInfo.tax;
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(ilMinAmount, ilMinAmount, tax.getPercent(), appProvider.isEntreprise(),
                appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        InvoiceLine invoiceLine = new InvoiceLine(minRatingDate, BigDecimal.ONE, amounts[0], amounts[1], amounts[2], OPEN,
                billingAccount, minAmountLabel, tax, tax.getPercent(), defaultAccountingArticle);
        if (entity instanceof ServiceInstance) {
            invoiceLine.setServiceInstance((ServiceInstance) entity);
        }
        if (entity instanceof Subscription) {
            invoiceLine.setSubscription((Subscription) entity);
        }
        if (billableEntity instanceof Subscription) {
            invoiceLine.setSubscription((Subscription) billableEntity);
        }
        if(invoiceLine.getSubscription() != null) {
            invoiceLine.getSubscription().setSeller(seller);
        }
        return invoiceLine;
    }

    public MinAmountForAccounts isMinAmountForAccountsActivated(IBillableEntity entity, ApplyMinimumModeEnum applyMinimumModeEnum) {
        return new MinAmountForAccounts(minAmountService.isMinUsed(), entity, applyMinimumModeEnum);
    }
    
    
	/**
	 * @param invoice 
	 * @param invoiceLineRessource
	 * @return 
	 */
	public InvoiceLine create(Invoice invoice, org.meveo.apiv2.billing.InvoiceLine invoiceLineRessource) {
		InvoiceLine invoiceLine = initInvoiceLineFromRessource(invoiceLineRessource, null);
		invoiceLine.setInvoice(invoice);
		create(invoiceLine);
		return invoiceLine;
	}
	
	public InvoiceLine initInvoiceLineFromRessource(org.meveo.apiv2.billing.InvoiceLine resource, InvoiceLine invoiceLine) {
		if(invoiceLine==null) {
			invoiceLine = new InvoiceLine();
		}
		Optional.ofNullable(resource.getPrestation()).ifPresent(invoiceLine::setPrestation);
		Optional.ofNullable(resource.getQuantity()).ifPresent(invoiceLine::setQuantity);
		Optional.ofNullable(resource.getUnitPrice()).ifPresent(invoiceLine::setUnitPrice);
		Optional.ofNullable(resource.getDiscountRate()).ifPresent(invoiceLine::setDiscountRate);
		Optional.ofNullable(resource.getAmountWithoutTax()).ifPresent(invoiceLine::setAmountWithoutTax);
		Optional.ofNullable(resource.getTaxRate()).ifPresent(invoiceLine::setTaxRate);
		Optional.ofNullable(resource.getAmountWithTax()).ifPresent(invoiceLine::setAmountWithTax);
		Optional.ofNullable(resource.getAmountTax()).ifPresent(invoiceLine::setAmountTax);
		Optional.ofNullable(resource.getOrderRef()).ifPresent(invoiceLine::setOrderRef);
		Optional.ofNullable(resource.getAccessPoint()).ifPresent(invoiceLine::setAccessPoint);
		Optional.ofNullable(resource.getValueDate()).ifPresent(invoiceLine::setValueDate);
		Optional.ofNullable(resource.getOrderNumber()).ifPresent(invoiceLine::setOrderNumber);
		Optional.ofNullable(resource.getDiscountAmount()).ifPresent(invoiceLine::setDiscountAmount);
		Optional.ofNullable(resource.getLabel()).ifPresent(invoiceLine::setLabel);
		Optional.ofNullable(resource.getRawAmount()).ifPresent(invoiceLine::setRawAmount);
		
		if(resource.getServiceInstanceCode()!=null) {
			invoiceLine.setServiceInstance((ServiceInstance)tryToFindByEntityClassAndCode(ServiceInstance.class, resource.getServiceInstanceCode()));
		}
		if(resource.getSubscriptionCode()!=null) {
			invoiceLine.setSubscription((Subscription)tryToFindByEntityClassAndCode(Subscription.class, resource.getSubscriptionCode()));
		}
		if(resource.getProductCode()!=null) {
			invoiceLine.setProduct((Product)tryToFindByEntityClassAndCode(Product.class, resource.getProductCode()));
		}
		if(resource.getAccountingArticleCode()!=null) {
			invoiceLine.setAccountingArticle((AccountingArticle)tryToFindByEntityClassAndCode(AccountingArticle.class, resource.getAccountingArticleCode()));
		}
		if(resource.getServiceTemplateCode()!=null) {
			invoiceLine.setServiceTemplate((ServiceTemplate)tryToFindByEntityClassAndCode(ServiceTemplate.class, resource.getServiceTemplateCode()));
		}
		if(resource.getDiscountPlanCode()!=null) {
			invoiceLine.setDiscountPlan((DiscountPlan)tryToFindByEntityClassAndCode(DiscountPlan.class, resource.getDiscountPlanCode()));
		}
		if(resource.getTaxCode()!=null) {
			invoiceLine.setTax((Tax)tryToFindByEntityClassAndCode(Tax.class, resource.getTaxCode()));
		}
		if(resource.getOrderLotCode()!=null) {
			invoiceLine.setOrderLot((OrderLot)tryToFindByEntityClassAndCode(OrderLot.class, resource.getOrderLotCode()));
		}
		if(resource.getBillingAccountCode()!=null) {
			invoiceLine.setBillingAccount((BillingAccount)tryToFindByEntityClassAndCode(BillingAccount.class, resource.getBillingAccountCode()));
		}
		if(resource.getOfferTemplateCode()!=null) {
			invoiceLine.setOfferTemplate((OfferTemplate)tryToFindByEntityClassAndCode(OfferTemplate.class, resource.getOfferTemplateCode()));
		}
		
		if(resource.isTaxRecalculated()!=null){
			invoiceLine.setTaxRecalculated( resource.isTaxRecalculated());
		}
		
		var datePeriod = new DatePeriod();
		if(resource.getStartDate() != null)
			datePeriod.setFrom(resource.getStartDate());
		if(resource.getEndDate() != null)
			datePeriod.setTo(resource.getEndDate());
		
		invoiceLine.setValidity(datePeriod);
		invoiceLine.setProductVersion((ProductVersion)tryToFindByEntityClassAndId(ProductVersion.class, resource.getProductVersionId()));
		invoiceLine.setOfferServiceTemplate((OfferServiceTemplate) tryToFindByEntityClassAndId(OfferServiceTemplate.class, resource.getOfferServiceTemplateId()));
		invoiceLine.setCommercialOrder((CommercialOrder)tryToFindByEntityClassAndId(CommercialOrder.class, resource.getCommercialOrderId()));
		invoiceLine.setBillingRun((BillingRun)tryToFindByEntityClassAndId(BillingRun.class, resource.getBillingRunId()));
		
		return invoiceLine;
	}
	
	/**
	 * @param invoice
	 * @param invoiceLine
	 */
	public void update(Invoice invoice, org.meveo.apiv2.billing.InvoiceLine invoiceLineRessource, Long invoiceLineId) {
		InvoiceLine invoiceLine = findInvoiceLine(invoice, invoiceLineId);
		invoiceLine = initInvoiceLineFromRessource(invoiceLineRessource, invoiceLine);
		update(invoiceLine);
	}

	private InvoiceLine findInvoiceLine(Invoice invoice, Long invoiceLineId) {
		InvoiceLine invoiceLine = findById(invoiceLineId);
		if(!invoice.equals(invoiceLine.getInvoice())) {
			throw new BusinessException("invoice line with ID "+invoiceLineId+" is not related to invoice with id:"+invoice.getId());
		}
		return invoiceLine;
	}

	/**
	 * @param invoice
	 * @param lineId
	 */
	public void remove(Invoice invoice, Long lineId) {
		InvoiceLine invoiceLine = findInvoiceLine(invoice, lineId);
		invoiceLine.setStatus(InvoiceLineStatusEnum.CANCELED);
		invoiceLine.setInvoice(null);
	}

    public List<Object[]> getTotalPositiveILAmountsByBR(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("InvoiceLine.sumPositiveILByBillingRun")
                .setParameter("billingRunId", billingRun.getId())
                .getResultList();
    }

    public void uninvoiceILs(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("RatedTransaction.unInvoiceByInvoiceIds")
                .setParameter("now", new Date())
                .setParameter("invoiceIds", invoicesIds)
                .executeUpdate();

    }

    public void deleteSupplementalILs(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("RatedTransaction.deleteSupplementalRTByInvoiceIds")
                .setParameter("invoicesIds", invoicesIds)
                .executeUpdate();
    }
    
    /**
     * Get a list of invoiceable Invoice Liness for a given BllingAccount and a list of ids
     *
     * @param billingAccountId
     * @param ids
     *
     * @return A list of InvoiceLine entities
     * @throws BusinessException General exception
     */
	public List<InvoiceLine> listByBillingAccountAndIDs(Long billingAccountId, Set<Long> ids) throws BusinessException {
		return getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByBillingAccountAndIDs", InvoiceLine.class)
				.setParameter("billingAccountId", billingAccountId).setParameter("listOfIds", ids).getResultList();
	}

	/**
	 * @param invoiceSubCategory
	 * @return
	 */
	public List<InvoiceLine> findOpenILbySubCat(InvoiceSubCategory invoiceSubCategory) {
        QueryBuilder qb = new QueryBuilder("select il from InvoiceLine il ", "il");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("il.accountingArticle.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addSql("il.status='OPEN'");

        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return new ArrayList<InvoiceLine>();
        }
	}
}
