package org.meveo.service.billing.impl;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
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
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.AggregationConfiguration;
import org.meveo.admin.job.InvoiceLinesFactory;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
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
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.MinAmountData;
import org.meveo.model.billing.MinAmountForAccounts;
import org.meveo.model.billing.MinAmountsResult;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
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
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;
import org.meveo.util.ApplicationProvider;

import com.google.common.base.Strings;


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
    private RatedTransactionService ratedTransactionService;
    
    @Inject
    private CpqQuoteService cpqQuoteService;

    @Inject
    private CommercialOrderService commercialOrderService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private TaxService taxService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private SellerService sellerService;
    
    @Inject
	private BillingRunService billingRunService;
    
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
    	BillingAccount billingAccount=entity.getBillingAccount();
    	Seller seller=null;
    	if(invoice!=null) {
       	 seller=invoice.getSeller()!=null?invoice.getSeller():seller;
       	 billingAccount=invoice.getBillingAccount();
       	}
    	billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
    	if(seller==null) {
    		 seller=entity.getCommercialOrder()!=null?entity.getCommercialOrder().getSeller():billingAccount.getCustomerAccount().getCustomer().getSeller();
    	}
    	 if (accountingArticle != null) {
             seller = sellerService.refreshOrRetrieve(seller);
             setApplicableTax(accountingArticle, date, seller, billingAccount, entity);
         }
    	super.create(entity);
    	

        if(entity.getDiscountPlan() != null) {
        	addDiscountPlanInvoice(entity.getDiscountPlan(), entity, billingAccount, invoice, accountingArticle, seller, entity);
        }
    }
    
    private void addDiscountPlanInvoice(DiscountPlan discount, InvoiceLine entity, BillingAccount billingAccount, Invoice invoice, AccountingArticle accountingArticle, Seller seller, InvoiceLine invoiceLine) {
    	var isDiscountApplicable = discountPlanService.isDiscountPlanApplicable(billingAccount, discount, null,null,null, null, invoice.getInvoiceDate(), invoiceLine);
    	if(isDiscountApplicable) {
    		List<DiscountPlanItem> discountItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, entity.getDiscountPlan(), null,null, null,null,null,new Date(), accountingArticle);
            BigDecimal totalDiscountAmount = BigDecimal.ZERO; 
            for (DiscountPlanItem discountPlanItem : discountItems) {
                BigDecimal DiscountLineAmount = BigDecimal.ZERO;
            	InvoiceLine discountInvoice = new InvoiceLine(entity, invoice);
            	discountInvoice.setStatus(entity.getStatus());
                if(discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.FIXED) {
                    totalDiscountAmount = totalDiscountAmount.add(discountPlanItem.getDiscountValue());
                } else {
                    BigDecimal taxPercent = entity.getTaxRate();
                    if(entity.getAccountingArticle() != null) {
                    	TaxInfo taxInfo = taxMappingService.determineTax(entity.getAccountingArticle().getTaxClass(), seller, billingAccount, null, invoice.getInvoiceDate(), false, false);
                            taxPercent = taxInfo.tax.getPercent();
                    }
                    BigDecimal discountAmount = discountPlanItemService.getDiscountAmount(entity.getUnitPrice(), discountPlanItem,null, Collections.emptyList());
                    if(discountAmount != null) {
                    	DiscountLineAmount = DiscountLineAmount.add(discountAmount);
            	  	}
                    BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(DiscountLineAmount, DiscountLineAmount, taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    var quantity = entity.getQuantity();
                    discountInvoice.setUnitPrice(DiscountLineAmount);
                    discountInvoice.setAmountWithoutTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[0]):BigDecimal.ZERO);
                    discountInvoice.setAmountWithTax(quantity.multiply(amounts[1]));
                    totalDiscountAmount = totalDiscountAmount.add(discountInvoice.getAmountWithoutTax().abs());
                    discountInvoice.setDiscountPlan(null);
                    discountInvoice.setDiscountAmount(BigDecimal.ZERO);
                    discountInvoice.setDiscountedInvoiceLine(entity);
                    discountInvoice.setAmountTax(quantity.multiply(amounts[2]));
                    discountInvoice.setTaxRate(taxPercent);
                	super.create(discountInvoice);
                }
            }
            entity.setDiscountAmount(totalDiscountAmount.compareTo(BigDecimal.ZERO) > 0
                    ? totalDiscountAmount : (totalDiscountAmount.multiply(entity.getQuantity())).abs());
    	}
    
    	
    }

    public List<InvoiceLine> listInvoiceLinesToInvoice(BillingRun billingRun, IBillableEntity entityToInvoice, Date firstTransactionDate,
                                                       Date lastTransactionDate, Filter filter,Map<String, Object> filterParams, int pageSize) throws BusinessException {
        if (filter != null) {
            return (List<InvoiceLine>) filterService.filteredListAsObjects(filter, filterParams);
		} else {
			TypedQuery<InvoiceLine> namedQuery = null;
			String byBr = billingRun != null ? "AndBR" : "";
			if (entityToInvoice instanceof Subscription) {
				namedQuery = getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceBySubscription" + byBr, InvoiceLine.class)
						.setParameter("subscriptionId", entityToInvoice.getId());
			} else if (entityToInvoice instanceof BillingAccount) {
				namedQuery = getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByBillingAccount" + byBr, InvoiceLine.class)
						.setParameter("billingAccountId", entityToInvoice.getId());
			} else if (entityToInvoice instanceof Order) {
				namedQuery = getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByOrderNumber" + byBr, InvoiceLine.class)
						.setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber());
			} else if (entityToInvoice instanceof CommercialOrder) {
				namedQuery = getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByCommercialOrder" + byBr, InvoiceLine.class)
						.setParameter("commercialOrderId", ((CommercialOrder) entityToInvoice).getId());
			} else if (entityToInvoice instanceof CpqQuote) {
				namedQuery = getEntityManager().createNamedQuery("InvoiceLine.listToInvoiceByQuote" + byBr, InvoiceLine.class)
						.setParameter("quoteId", entityToInvoice.getId());
			} else {
				return emptyList();
			}

			if (billingRun != null) {
				namedQuery.setParameter("billingRunId", billingRun.getId());
			} else {
				namedQuery.setParameter("firstTransactionDate", firstTransactionDate)
						.setParameter("lastTransactionDate", lastTransactionDate);
			}
			return namedQuery.setHint("org.hibernate.readOnly", true).setMaxResults(pageSize).getResultList();
		}
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


    public InvoiceLine createInvoiceLine(IBillableEntity entityToInvoice, AccountingArticle accountingArticle,
                                         ProductVersion productVersion, OrderLot orderLot, OfferTemplate offerTemplate,
                                         OrderOffer orderOffer, BigDecimal amountWithoutTaxToBeInvoiced,
                                         BigDecimal amountWithTaxToBeInvoiced, BigDecimal taxAmountToBeInvoiced, BigDecimal totalTaxRate) {
        BillingAccount billingAccount = null;
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setAccountingArticle(accountingArticle);
        invoiceLine.setLabel(accountingArticle.getDescription());
        invoiceLine.setProduct(productVersion.getProduct());
        invoiceLine.setProductVersion(productVersion);
        invoiceLine.setOrderLot(orderLot);
        invoiceLine.setOfferTemplate(offerTemplate);
        invoiceLine.setOrderOffer(orderOffer);
        ofNullable(orderOffer).ifPresent(offer -> invoiceLine.setQuoteOffer(offer.getQuoteOffer()));
        if (entityToInvoice instanceof CpqQuote) {
            entityToInvoice = cpqQuoteService.retrieveIfNotManaged((CpqQuote) entityToInvoice);
            CpqQuote quote = ((CpqQuote) entityToInvoice);
            invoiceLine.setQuote(quote);
            billingAccount = quote.getBillableAccount();
            invoiceLine.setBillingAccount(billingAccount);
        }
        CommercialOrder commercialOrder = null;
        if (entityToInvoice instanceof CommercialOrder) {
            entityToInvoice = commercialOrderService.retrieveIfNotManaged((CommercialOrder) entityToInvoice);
            commercialOrder = ((CommercialOrder) entityToInvoice);
            invoiceLine.setCommercialOrder(commercialOrder);
            invoiceLine.setOrderNumber(commercialOrder.getOrderNumber());
            billingAccount = commercialOrder.getBillingAccount();
            invoiceLine.setBillingAccount(billingAccount);
        }
        if (entityToInvoice instanceof BillingAccount) {
            entityToInvoice = billingAccountService.retrieveIfNotManaged((BillingAccount) entityToInvoice);
            billingAccount = ((BillingAccount) entityToInvoice);
            invoiceLine.setBillingAccount(billingAccount);

        }
        invoiceLine.setQuantity(BigDecimal.valueOf(1));
        amountWithoutTaxToBeInvoiced = (amountWithoutTaxToBeInvoiced != null) ? amountWithoutTaxToBeInvoiced : accountingArticle.getUnitPrice();
        invoiceLine.setUnitPrice(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithoutTax(amountWithoutTaxToBeInvoiced);
        invoiceLine.setAmountWithTax(amountWithTaxToBeInvoiced);
        invoiceLine.setAmountTax(taxAmountToBeInvoiced);
        invoiceLine.setTaxRate(totalTaxRate);

        invoiceLine.setValueDate(new Date());
        create(invoiceLine);
        commit();
        return invoiceLine;
    }

    private void setApplicableTax(AccountingArticle accountingArticle, Date operationDate, Seller seller, BillingAccount billingAccount, InvoiceLine invoiceLine) {
        Tax taxZero = (billingAccount.isExoneratedFromtaxes() != null && billingAccount.isExoneratedFromtaxes()) ? taxService.getZeroTax() : null;
        Boolean isExonerated = billingAccount.isExoneratedFromtaxes();
        if (isExonerated == null) {
            isExonerated = billingAccountService.isExonerated(billingAccount);
        }
        Object[] applicableTax = taxMappingService
                .checkIfTaxHasChanged(invoiceLine.getTax(), isExonerated, seller, billingAccount, operationDate, accountingArticle.getTaxClass(), null, taxZero);
        boolean taxRecalculated = (boolean) applicableTax[1];
        if (taxRecalculated) {
            Tax tax = (Tax) applicableTax[0];
            log.debug("Will update invoice line of Billing account {} and tax class {} with new tax from {}/{}% to {}/{}%", billingAccount.getId(),
                    accountingArticle.getTaxClass().getId(), invoiceLine.getTax() == null ? null : invoiceLine.getTax().getId(), tax == null ? null : tax.getPercent(), tax.getId(),
                    tax.getPercent());
            invoiceLine.setTax(tax);
            invoiceLine.setTaxRecalculated(taxRecalculated);
            
            if(!tax.getPercent().equals(invoiceLine.getTaxRate())) {
            	   invoiceLine.computeDerivedAmounts(appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode());
                   invoiceLine.setTaxRate(tax.getPercent());
            }
         
            
        }
    }

    public void calculateAmountsAndCreateMinAmountLines(IBillableEntity billableEntity, Date lastTransactionDate, Date invoiceUpToDate,
                                                        boolean calculateAndUpdateTotalAmounts, MinAmountForAccounts minAmountForAccounts) throws BusinessException {
        Amounts totalInvoiceableAmounts;
        List<InvoiceLine> minAmountLines = new ArrayList<>();
        List<ExtraMinAmount> extraMinAmounts = new ArrayList<>();
        Date minRatingDate = addDaysToDate(lastTransactionDate, -1);

        if (billableEntity instanceof Order) {
            if (calculateAndUpdateTotalAmounts) {
                totalInvoiceableAmounts = computeTotalOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
            }
        } else  if (billableEntity instanceof CpqQuote) {
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
                            lastTransactionDate, invoiceUpToDate, minRatingDate, extraMinAmounts, accountClass);
                    extraMinAmounts = minAmountsResults.getExtraMinAmounts();
                    minAmountLines.addAll(minAmountsResults.getMinAmountInvoiceLines());
                }
            }
            totalInvoiceableAmounts =
                    minAmountService.computeTotalInvoiceableAmount(billableEntity, new Date(0), lastTransactionDate, invoiceUpToDate, INVOICING_PROCESS_TYPE);
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
                                                   Date lastTransactionDate, Date invoiceUpToDate, Date minRatingDate, List<ExtraMinAmount> extraMinAmounts,
                                                   Class accountClass) throws BusinessException {
        MinAmountsResult minAmountsResult = new MinAmountsResult();
        Map<Long, MinAmountData> accountToMinAmount =
                minAmountService.getInvoiceableAmountDataPerAccount(billableEntity, billingAccount, lastTransactionDate, invoiceUpToDate, extraMinAmounts, accountClass, INVOICING_PROCESS_TYPE);
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
            TaxMappingService.TaxInfo taxInfo = taxMappingService
                    .determineTax(defaultMinAccountingArticle.getTaxClass(), seller, billingAccount, null, minRatingDate, null, true, false, null);
            InvoiceLine invoiceLine = createInvoiceLine(minAmountLabel, billableEntity, billingAccount, minRatingDate, entity, seller, defaultMinAccountingArticle, taxInfo, diff);
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
	 * @param invoiceLineResource
	 * @return
	 */
	public InvoiceLine create(Invoice invoice, org.meveo.apiv2.billing.InvoiceLine invoiceLineResource) {
		InvoiceLine invoiceLine = new InvoiceLine();
		invoiceLine.setInvoice(invoice);
		invoiceLine = initInvoiceLineFromResource(invoiceLineResource, invoiceLine);
		create(invoiceLine);
		return invoiceLine;
	}

	public InvoiceLine initInvoiceLineFromResource(org.meveo.apiv2.billing.InvoiceLine resource, InvoiceLine invoiceLine) {
		if(invoiceLine == null) {
			invoiceLine = new InvoiceLine();
		}
		ofNullable(resource.getPrestation()).ifPresent(invoiceLine::setPrestation);
		ofNullable(resource.getQuantity()).ifPresent(invoiceLine::setQuantity);
		ofNullable(resource.getUnitPrice()).ifPresent(invoiceLine::setUnitPrice);
		ofNullable(resource.getDiscountRate()).ifPresent(invoiceLine::setDiscountRate);
		ofNullable(resource.getTaxRate()).ifPresent(invoiceLine::setTaxRate);
		ofNullable(resource.getAmountTax()).ifPresent(invoiceLine::setAmountTax);
		ofNullable(resource.getOrderRef()).ifPresent(invoiceLine::setOrderRef);
		ofNullable(resource.getAccessPoint()).ifPresent(invoiceLine::setAccessPoint);
		ofNullable(resource.getValueDate()).ifPresent(invoiceLine::setValueDate);
		ofNullable(resource.getOrderNumber()).ifPresent(invoiceLine::setOrderNumber);
		ofNullable(resource.getDiscountAmount()).ifPresent(invoiceLine::setDiscountAmount);
		ofNullable(resource.getLabel()).ifPresent(invoiceLine::setLabel);
		ofNullable(resource.getRawAmount()).ifPresent(invoiceLine::setRawAmount);
		AccountingArticle accountingArticle=null;
		if (resource.getAccountingArticleCode() != null) {
			 accountingArticle = accountingArticleService.findByCode(resource.getAccountingArticleCode());
		}	
		
		if(invoiceLine.getQuantity() == null) {
            invoiceLine.setQuantity(new BigDecimal(1));
        }
		
		if(invoiceLine.getUnitPrice() == null) {
				if (accountingArticle != null && accountingArticle.getUnitPrice() != null) {
					invoiceLine.setUnitPrice(accountingArticle.getUnitPrice());
					if(resource.getQuantity() != null) {
						invoiceLine.setAmountWithoutTax(accountingArticle.getUnitPrice().multiply(resource.getQuantity()));
						invoiceLine.setAmountWithTax(accountingArticle.getUnitPrice().multiply(resource.getQuantity()));
						/****amountWithoutTax and amountWithTax will be recalculated bellow according to tax percent and the business model (b2b or b2c)*/
					}
				} else {
					throw new BusinessException("You cannot create an invoice line without a price if unit price is not set on article with code : "+resource.getAccountingArticleCode());
				}
		} else {
            invoiceLine.setAmountWithoutTax(invoiceLine.getUnitPrice().multiply(resource.getQuantity()));
            invoiceLine.setAmountWithTax(NumberUtils.computeTax(invoiceLine.getAmountWithoutTax(),
                    invoiceLine.getTaxRate(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode()));
        }

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
			invoiceLine.setTaxRate(invoiceLine.getTax().getPercent());
		}
		if(resource.getOrderLotCode()!=null) {
			invoiceLine.setOrderLot((OrderLot)tryToFindByEntityClassAndCode(OrderLot.class, resource.getOrderLotCode()));
		}
		if(resource.getBillingAccountCode()!=null) {
			invoiceLine.setBillingAccount((BillingAccount)tryToFindByEntityClassAndCode(BillingAccount.class, resource.getBillingAccountCode()));
        } else if(invoiceLine.getInvoice()!=null){
        	invoiceLine.setBillingAccount(invoiceLine.getInvoice().getBillingAccount());
        }
        if (resource.getOfferTemplateCode() != null) {
            invoiceLine.setOfferTemplate((OfferTemplate) tryToFindByEntityClassAndCode(OfferTemplate.class, resource.getOfferTemplateCode()));
        }

        if (resource.isTaxRecalculated() != null) {
            invoiceLine.setTaxRecalculated(resource.isTaxRecalculated());
        }

        var datePeriod = new DatePeriod();
        if (resource.getStartDate() != null) {
            datePeriod.setFrom(resource.getStartDate());
        }
        if (resource.getEndDate() != null) {
            datePeriod.setTo(resource.getEndDate());
        }
        

		if(invoiceLine.getTax()==null  && accountingArticle != null && invoiceLine.getBillingAccount()!=null)  {
			BillingAccount  billingAccount = billingAccountService.refreshOrRetrieve(invoiceLine.getBillingAccount());
			Boolean isExonerated = billingAccount.isExoneratedFromtaxes();
	        if (isExonerated == null) {
	            isExonerated = billingAccountService.isExonerated(billingAccount);
	        }
			  TaxInfo recalculatedTaxInfo = taxMappingService.determineTax(accountingArticle.getTaxClass(), 
					  billingAccount.getCustomerAccount().getCustomer().getSeller(), 
					  billingAccount, null, 
					  invoiceLine.getValueDate()!=null?invoiceLine.getValueDate():new Date(), null, 
				      isExonerated, false, invoiceLine.getTax());
			  invoiceLine.setTax(recalculatedTaxInfo.tax);
			  invoiceLine.setTaxRate(recalculatedTaxInfo.tax.getPercent());
		}
		
        /****recalculate amountWithoutTax and amountWithTax  according to tax percent and the business model (b2b or b2c)*/
        invoiceLine.computeDerivedAmounts(appProvider.isEntreprise(), appProvider.getRounding(), appProvider.getRoundingMode());

        invoiceLine.setValidity(datePeriod);
        invoiceLine.setProductVersion((ProductVersion) tryToFindByEntityClassAndId(ProductVersion.class, resource.getProductVersionId()));
        invoiceLine.setOfferServiceTemplate((OfferServiceTemplate) tryToFindByEntityClassAndId(OfferServiceTemplate.class, resource.getOfferServiceTemplateId()));
        invoiceLine.setCommercialOrder((CommercialOrder) tryToFindByEntityClassAndId(CommercialOrder.class, resource.getCommercialOrderId()));
        invoiceLine.setBillingRun((BillingRun) tryToFindByEntityClassAndId(BillingRun.class, resource.getBillingRunId()));

        return invoiceLine;
    }

	/**
	 * @param invoice
	 * @param invoiceLineResource
	 * @param invoiceLineId
	 */
	public void update(Invoice invoice, org.meveo.apiv2.billing.InvoiceLine invoiceLineResource, Long invoiceLineId) {
		InvoiceLine invoiceLine = findInvoiceLine(invoice, invoiceLineId);
		invoiceLine = initInvoiceLineFromResource(invoiceLineResource, invoiceLine);

		deleteByDiscountedPlan(invoiceLine);
		if(StringUtils.isNotBlank(invoiceLineResource.getDiscountPlanCode())) {
			BillingAccount billingAccount=invoiceLine.getBillingAccount();
	    	Seller seller=null;
	    	if(invoice!=null) {
	       	 seller=invoice.getSeller()!=null?invoice.getSeller():seller;
	       	 billingAccount=invoice.getBillingAccount();
	       	}
	    	billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
	    	if(seller==null) {
	    		 seller=invoiceLine.getCommercialOrder()!=null?invoiceLine.getCommercialOrder().getSeller():billingAccount.getCustomerAccount().getCustomer().getSeller();
	    	}
	    	var accountingArticle = invoiceLine.getAccountingArticle();
	    	Date date = invoiceLine.getValueDate()!=null ? invoiceLine.getValueDate() : new Date();
	    	 if (accountingArticle != null) {
	             seller = sellerService.refreshOrRetrieve(seller);
	             setApplicableTax(accountingArticle, date, seller, billingAccount, invoiceLine);
	         }
	    		 addDiscountPlanInvoice(invoiceLine.getDiscountPlan(), invoiceLine, invoiceLine.getBillingAccount(), invoice, accountingArticle, seller, invoiceLine);
	    	
		} else
   		 	invoiceLine.setDiscountPlan(null);
		
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
	 * @param lineId invoiceLine id
	 */
	public void remove(Invoice invoice, Long lineId) {
		InvoiceLine invoiceLine = findInvoiceLine(invoice, lineId);
        reduceDiscountAmounts(invoice, invoiceLine);
        deleteByDiscountedPlan(invoiceLine);
        remove(invoiceLine);
	}

    private void reduceDiscountAmounts(Invoice invoice, InvoiceLine invoiceLine) {
        if (invoiceLine.getDiscountPlan() != null
                && invoiceLine.getDiscountAmount() != null
                && !invoiceLine.getDiscountAmount().equals(BigDecimal.ZERO)) {
            invoice.setDiscountAmount(invoice.getDiscountAmount().subtract(invoiceLine.getDiscountAmount()));
        }
        if (invoice.getAmountWithoutTaxBeforeDiscount() != null
                && invoice.getAmountWithoutTaxBeforeDiscount().compareTo(invoice.getAmountWithoutTax()) != 0
                && invoice.getAmountWithoutTaxBeforeDiscount().compareTo(BigDecimal.ZERO) > 0
                && invoiceLine.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setAmountWithoutTaxBeforeDiscount(
                    invoice.getAmountWithoutTaxBeforeDiscount().subtract(invoiceLine.getAmountWithoutTax()));
        }
        if (invoiceLine.getDiscountedInvoiceLine() != null
                && invoiceLine.getAmountWithoutTax().compareTo(BigDecimal.ZERO) < 0) {
            InvoiceLine discountedInvoiceLine = invoiceLine.getDiscountedInvoiceLine();
            discountedInvoiceLine.setDiscountAmount(
                    discountedInvoiceLine.getDiscountAmount().add(invoiceLine.getAmountWithoutTax()));
            update(discountedInvoiceLine);
            invoice.setDiscountAmount(invoice.getDiscountAmount().subtract(discountedInvoiceLine.getDiscountAmount()));
        }
    }

    public List<Object[]> getTotalPositiveILAmountsByBR(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("InvoiceLine.sumPositiveILByBillingRun")
                .setParameter("billingRunId", billingRun.getId())
                .getResultList();
    }

    public void uninvoiceILs(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("InvoiceLine.unInvoiceByInvoiceIds")
                .setParameter("now", new Date())
                .setParameter("invoiceIds", invoicesIds)
                .executeUpdate();

    }

    public void cancelIlByInvoices(Collection<Long> invoicesIds) {
        getEntityManager().createNamedQuery("InvoiceLine.cancelByInvoiceIds")
        .setParameter("now", new Date())
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
            return new ArrayList<>();
        }
	}

    /**
     * Create Query builder from a map of filters
     * filters : Map of filters
     * Return : QueryBuilder
     */
    public QueryBuilder fromFilters(Map<String, String> filters) {
        QueryBuilder queryBuilder;
        if (filters.containsKey("SQL")) {
            queryBuilder = new QueryBuilder(filters.get("SQL"));
        } else {
            FilterConverter converter = new FilterConverter(RatedTransaction.class);
            PaginationConfiguration configuration = new PaginationConfiguration(converter.convertFilters(filters));
            queryBuilder = ratedTransactionService.getQuery(configuration);
        }
        return queryBuilder;
    }

    /**
     * Retrieve invoice lines associated to an invoice
     *
     * @param invoice Invoice
     * @return A list of invoice Lines
     */
    public List<InvoiceLine> getInvoiceLinesByInvoice(Invoice invoice, boolean includeFree) {
        if (invoice.getId() == null) {
            return new ArrayList<>();
        }
        if (includeFree) {
            return getEntityManager().createNamedQuery("InvoiceLine.listByInvoice", InvoiceLine.class)
                    .setParameter("invoice", invoice)
                    .getResultList();
        } else {
            return getEntityManager().createNamedQuery("InvoiceLine.listByInvoiceNotFree", InvoiceLine.class)
                    .setParameter("invoice", invoice)
                    .getResultList();
        }
    }
    
    public BasicStatistics createInvoiceLines(List<Map<String, Object>> groupedRTs,
            AggregationConfiguration configuration, JobExecutionResultImpl result) throws BusinessException {
        return createInvoiceLines(groupedRTs, configuration, result, null);
    }
    
    public BasicStatistics createInvoiceLines(List<Map<String, Object>> groupedRTs,
            AggregationConfiguration configuration, JobExecutionResultImpl result, BillingRun billingRun) throws BusinessException {
        InvoiceLinesFactory linesFactory = new InvoiceLinesFactory();
        BasicStatistics basicStatistics = new BasicStatistics();
        InvoiceLine invoiceLine = null;
        List<Long> associatedRtIds = null;
        for (Map<String, Object> record : groupedRTs) {
            invoiceLine = linesFactory.create(record, configuration, result, appProvider, billingRun);
            basicStatistics.addToAmountWithTax(invoiceLine.getAmountWithTax());
            basicStatistics.addToAmountWithoutTax(invoiceLine.getAmountWithoutTax());
            basicStatistics.addToAmountTax(invoiceLine.getAmountTax());
            create(invoiceLine);
            commit();
            associatedRtIds = stream(((String) record.get("rated_transaction_ids")).split(",")).map(Long::parseLong).collect(toList());
            basicStatistics.setCount(associatedRtIds.size());
            ratedTransactionService.linkRTsToIL(associatedRtIds, invoiceLine.getId());
        }
        return basicStatistics;
    }

	/**
	 * @param result
	 * @param aggregationConfiguration
	 * @param billingRun
	 * @param be billableEntity
	 * @param basicStatistics
	 * @return
	 */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createInvoiceLines(JobExecutionResultImpl result, AggregationConfiguration aggregationConfiguration, BillingRun billingRun, IBillableEntity be, BasicStatistics basicStatistics) {
	    BasicStatistics ilBasicStatistics = createInvoiceLines(ratedTransactionService.getGroupedRTsWithAggregation(aggregationConfiguration, billingRun, be, billingRun.getLastTransactionDate(), billingRun.getInvoiceDate(),
		        billingRun.isExceptionalBR() ? billingRunService.createFilter(billingRun, false) : null), aggregationConfiguration, result, billingRun);
	    basicStatistics.append(ilBasicStatistics);
	}

    public void deleteByBillingRun(long billingRunId) {
        List<Long> invoiceLinesIds = loadInvoiceLinesIdByBillingRun(billingRunId);
        if(!invoiceLinesIds.isEmpty()) {
            detachRatedTransactions(invoiceLinesIds);
            getEntityManager()
                    .createNamedQuery("InvoiceLine.deleteByBillingRun")
                    .setParameter("billingRunId", billingRunId)
                    .executeUpdate();
        }
    }

    public List<Long> loadInvoiceLinesIdByBillingRun(long billingRunId) {
        return getEntityManager().createNamedQuery("InvoiceLine.listByBillingRun")
                .setParameter("billingRunId", billingRunId)
                .getResultList();
    }

    public void detachRatedTransactions(List<Long> invoiceLinesIds) {
        ratedTransactionService.getEntityManager()
                .createNamedQuery("RatedTransaction.detachFromInvoiceLines")
                .setParameter("ids", invoiceLinesIds)
                .executeUpdate();
    }
    
    public List<Long> loadInvoiceLinesByBillingRunNotValidatedInvoices(long billingRunId) {
        return getEntityManager().createNamedQuery("InvoiceLine.listByBillingRunNotValidatedInvoices")
                .setParameter("billingRunId", billingRunId)
                .getResultList();
    }

	public void deleteInvoiceLines(BillingRun billingRun) {
		List<Long> invoiceLinesIds = loadInvoiceLinesIdByBillingRun(billingRun.getId());
        if(!invoiceLinesIds.isEmpty()) {
            detachRatedTransactions(invoiceLinesIds);
            getEntityManager()
                    .createNamedQuery("InvoiceLine.deleteByBillingRunNotValidatedInvoices")
                    .setParameter("billingRunId", billingRun.getId())
                    .executeUpdate();
        }
		
	}
	
	@SuppressWarnings("unchecked")
    private void deleteByDiscountedPlan(InvoiceLine invoiceLine) {
        if (invoiceLine == null || invoiceLine.getId() == null) {
            return;
        }
        QueryBuilder queryBuilder = new QueryBuilder("from InvoiceLine il ", "il");
        queryBuilder.addCriterionEntity("il.discountedInvoiceLine", invoiceLine);
        Query query = queryBuilder.getQuery(getEntityManager());
        List<InvoiceLine> invoiceLines = query.getResultList();
        if(invoiceLines != null && !invoiceLines.isEmpty()) {
            var ids = invoiceLines.stream()
                    .map(InvoiceLine::getId)
                    .collect(toSet());
            remove(ids);
        }
    }

    public List<Long> getDiscountLines(Long id) {
        return getEntityManager()
                    .createNamedQuery("InvoiceLine.listDiscountLines")
                    .setParameter("invoiceLineId", id)
                    .getResultList();
    }
}