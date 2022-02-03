/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.v2.invoicing.RefactoredInvoicingJob;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.I18nDescripted;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ReferenceDateEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.invoicing.impl.BillingAccountDetailsItem;
import org.meveo.service.billing.invoicing.impl.DiscountPlanSummary;
import org.meveo.service.billing.invoicing.impl.InvoicingItem;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.billing.TaxScriptService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

import com.google.common.collect.Lists;

/**
 * The Class InvoicingService.
 *
 * 
 */
@Stateless
public class InvoicingService extends PersistenceService<Invoice> {

    private final static BigDecimal HUNDRED = new BigDecimal("100");
    /** The billing account service. */
    @Inject
    private BillingAccountService billingAccountService;
    /** The rejected billing account service. */
    @Inject
    private RejectedBillingAccountService rejectedBillingAccountService;
    @Inject
    private ServiceSingleton serviceSingleton;
    /** The invoice type service. */
    @Inject
    private InvoiceTypeService invoiceTypeService;
    @Inject
    private TaxMappingService taxMappingService;
    @Inject
    private TaxScriptService taxScriptService;
    @Inject
    private TaxService taxService;
    /** date format. */
    private String DATE_PATERN = "yyyy.MM.dd";
    
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;
    
    private static final int MAX_RT_TO_UPDATE_PER_TRANSACTION = 100000;
    
    @Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;
    
    private Map<Long, Tax> taxes=new TreeMap<Long, Tax>();
    private Map<Long, TradingLanguage> tradingLanguages=new TreeMap<Long, TradingLanguage>();
    private Map<Long, DiscountPlan> discountPlans=new TreeMap<Long, DiscountPlan>();
    /**
     * Description translation map.
     */
    private Map<String, String> descriptionMap = new HashMap<>();
    
    /**
     * Creates the aggregates and invoice async. One entity at a time in a separate transaction.
     *
     * @param invoicingItemsList             the entity objects
     * @param billingRun           the billing run
     * @param jobInstanceId        the job instance id
     * @param minAmountForAccounts Check if min amount is enabled in any account level
     * @param lastCurrentUser      Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *                             expirations), current user might be lost, thus there is a need to reestablish.
     * @param isFullAutomatic 
     * @param expectMassRtsPerInvoice 
     * @return the future
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> createAgregatesAndInvoiceForJob(BillingRun billingRun, BillingCycle billingCycle, List<BillingAccountDetailsItem> invoicingItemsList, Long jobInstanceId, MeveoUser lastCurrentUser, boolean isFullAutomatic) {
        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        List<Invoice> invoices = processData(billingRun, invoicingItemsList, jobInstanceId, isFullAutomatic, billingCycle);
        if(!CollectionUtils.isEmpty(invoices)) {
        	writeInvoicingData(billingRun, isFullAutomatic, invoices, billingCycle);
        }
        return new AsyncResult<String>("OK");
    }

	private List<Invoice> processData(BillingRun billingRun, List<BillingAccountDetailsItem> invoicingItemsList, Long jobInstanceId, boolean isFullAutomatic, BillingCycle billingCycle) {
		List<Invoice> invoices = new ArrayList<Invoice>();
		for (BillingAccountDetailsItem billingAccountDetailsItem : invoicingItemsList) {
            if (jobInstanceId != null && !jobExecutionService.isJobRunningOnThis(jobInstanceId)) {
                break;
            }
            final long billingAccountId = billingAccountDetailsItem.getBillingAccountId();
            try {
            	//group by invoicingKey and prepare invoice
            	billingAccountDetailsItem.getInvoicingItems().stream().collect(Collectors.groupingBy(InvoicingItem::getInvoiceKey)).values().stream().forEach(x->createAggregatesAndInvoiceFromInvoicingItems(billingAccountDetailsItem, x, billingRun, invoices, billingCycle));
            } catch (Exception e) {
                log.error("Failed to create invoices for entity {}", billingAccountId, e);
                rejectedBillingAccountService.create(billingAccountId, billingRun, e.getMessage());
            }
        }
		return invoices;
	}
    
    /**
	 * @param billingAccountDetailsItem 
     * @param invoicingItems
	 * @param billingRun
     * @param invoices 
     * @param billingCycle 
     * @param isFullAutomatic 
	 * @return
	 */
	private void createAggregatesAndInvoiceFromInvoicingItems(BillingAccountDetailsItem billingAccountDetailsItem, List<InvoicingItem> invoicingItems, BillingRun billingRun, List<Invoice> invoices, BillingCycle billingCycle){
		final InvoicingItem firstItem = invoicingItems.get(0);
		BillingAccount billingAccount = getEntityManager().getReference(BillingAccount.class, firstItem.getBillingAccountId());
		final Invoice invoice = initInvoice(billingAccountDetailsItem, firstItem, billingRun, billingAccount, billingCycle);
		Set<SubCategoryInvoiceAgregate> invoiceSCAs = createInvoiceAgregates(billingAccountDetailsItem, invoicingItems, billingAccount, invoice);
		if (!invoice.isPrepaid()) {
			BigDecimal thresholdAfterDiscount = getThresholdByInvoice(billingAccountDetailsItem, ThresholdOptionsEnum.AFTER_DISCOUNT, billingCycle);
			if (thresholdAfterDiscount != null) {
				BigDecimal amount = (appProvider.isEntreprise()) ? invoice.getAmountWithoutTax() : invoice.getAmountWithTax();
				if (thresholdAfterDiscount.compareTo(amount) > 0) {
					rejectedBillingAccountService.create(billingAccount.getId(), billingRun, "Billing account did not reach invoicing threshold");
					return;
				}
			}
		}
 
        evalDueDate(invoice, billingCycle, null, billingAccountDetailsItem.getCaDueDateDelayEL());
        invoice.setSubCategoryInvoiceAgregate(invoiceSCAs);
        invoices.add(invoice);
	}

	private void writeInvoicingData(BillingRun billingRun, boolean isFullAutomatic, List<Invoice> invoices, BillingCycle billingCycle) {
		log.info("======== CREATING {} INVOICES ========", invoices.size());
		invoices.stream().forEach(invoice->assignNumberAndCreate(billingRun, isFullAutomatic, invoice, billingCycle));
        getEntityManager().flush();//to be able to update Rts
        getEntityManager().clear();
        log.info("======== UPDATING RTs ========");
        invoices.stream().forEach(i ->i.getSubCategoryInvoiceAgregate().stream().forEach(sca->updateRatedTransactions(i,billingRun,sca)));
	}

	private void assignNumberAndCreate(BillingRun billingRun, boolean isFullAutomatic, Invoice invoice, BillingCycle billingCycle) {
		if(!isFullAutomatic) {
        	invoice.setTemporaryInvoiceNumber(serviceSingleton.getTempInvoiceNumber(billingRun.getId()));
        } else {
        	serviceSingleton.assignInvoiceNumberVirtual(invoice);
        	incrementBAInvoiceDate(billingRun, invoice.getBillingAccount(), invoice.getNextInvoiceDate(), billingCycle);
        }
        this.create(invoice);
        postCreate(invoice);
	}
	
	private void updateRatedTransactions(Invoice invoice, BillingRun billingRun, SubCategoryInvoiceAgregate sca) {
		final List<Long> largeList = sca.getRtIDs();
		final long min=largeList.stream().min(Long::compare).get();
		final long max=largeList.stream().max(Long::compare).get();
		Date lastTransactionDate = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false")) ?
				DateUtils.setDateToEndOfDay(billingRun.getLastTransactionDate()) :DateUtils.setDateToStartOfDay(billingRun.getLastTransactionDate());

		final Integer numberOfRTs = sca.getItemNumber();
		if(numberOfRTs>RefactoredInvoicingJob.LIMIT_UPDATE_BY_ID && numberOfRTs>2) {
			final long intervalSize = max-min+1;
			List<Long[]>subIntervals = getIntervals(min, max, numberOfRTs);
			int updatedRTs=0;
			if((intervalSize)==numberOfRTs) {
				for(Long[] interval :subIntervals) {
					Query query = getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoUsingInterval").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("invoiceAgregateF", sca).setParameter("minId", interval[0]).setParameter("maxId", interval[1]);
					updatedRTs+=query.executeUpdate();
					if(intervalSize>1000) {
						getEntityManager().flush();
					}
				}
			} else {
				for (Long[] interval : subIntervals) {
					Query query = getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfoUsingScKey").setParameter("lastTransactionDate", lastTransactionDate).setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("invoiceAgregateF", sca).setParameter("minId", interval[0]).setParameter("maxId", interval[1]).setParameter("baId", invoice.getBillingAccount().getId()).setParameter("sellerId", invoice.getSeller().getId()).setParameter("walletId", sca.getWallet().getId()).setParameter("scId", sca.getInvoiceSubCategory().getId()).setParameter("uaId", sca.getUserAccount().getId());
					updatedRTs+=query.executeUpdate();
					if(intervalSize>1000) {
						getEntityManager().flush();
					}
				}
			}
			if(updatedRTs!=numberOfRTs) {
				String updateKey="["+min+"-"+ max+"]  ba : "+invoice.getBillingAccount().getId()+" seller : "+ invoice.getSeller()+" walletId : "+ sca.getWallet().getId()+"scId : "+ sca.getInvoiceSubCategory().getId()+" uaId : "+  sca.getUserAccount().getId();
				throw new BusinessException("TOTAL UPDATED RTs ("+updatedRTs+") IS NOT THE EXPECTED NUMBER :"+numberOfRTs + " FOR KEY : "+updateKey) ;
			}
		} else {
			for (List<Long> rtIds : Lists.partition(largeList, MAX_RT_TO_UPDATE_PER_TRANSACTION)) {
				Query query = getEntityManager().createNamedQuery("RatedTransaction.massUpdateWithInvoiceInfo").setParameter("billingRun", billingRun).setParameter("invoice", invoice).setParameter("invoiceAgregateF", sca).setParameter("ids", rtIds);
				query.executeUpdate();
			}
		}
	}

	private List<Long[]> getIntervals(final long min, final long max, Integer numberOfRTs) {
		List<Long[]> intervals = new ArrayList<>();
		if(MAX_RT_TO_UPDATE_PER_TRANSACTION<numberOfRTs) {
			intervals.add(new Long[]{min, max+1});
		} else {
			List<Long> checksums = new ArrayList<Long>();
			checksums.add(min);
			Long next=min+MAX_RT_TO_UPDATE_PER_TRANSACTION;
			while(next<max) {
				checksums.add(next);
				next=next+MAX_RT_TO_UPDATE_PER_TRANSACTION;
			}
			checksums.add(max+1);
			int index = 0; 
	
			while(index+1<checksums.size()) {
				long intervalMin=checksums.get(index++);
				long intervalMax=checksums.get(index);
				intervals.add(new Long[]{intervalMin, intervalMax});
			}
		}
		return intervals;
	}
	
    private void incrementBAInvoiceDate(BillingRun billingRun, BillingAccount billingAccount, Date nextInvoiceDate, BillingCycle billingCycle) throws BusinessException {
		Date nextCalendarDate = billingCycle.getNextCalendarDate(getReferenceDate(billingRun, nextInvoiceDate, billingCycle));
		if(nextCalendarDate!=null && (nextInvoiceDate==null || nextCalendarDate.compareTo(nextInvoiceDate)!=0)) {
			billingAccount.setNextInvoiceDate(nextCalendarDate);
	        billingAccount.updateAudit(currentUser);
	        billingAccountService.update(billingAccount);
		}
    }
    
    private Date getReferenceDate(BillingRun billingRun, Date nextInvoiceDate, BillingCycle billingCycle) {
        Date referenceDate = new Date();
        ReferenceDateEnum referenceDateEnum = null;
        if (billingRun != null) {
            referenceDateEnum = billingRun.getReferenceDate();
        }
        if (referenceDateEnum == null && billingCycle != null) {
            referenceDateEnum = billingCycle.getReferenceDate();
        }
        if (referenceDateEnum != null) {
            switch (referenceDateEnum) {
            case TODAY:
                referenceDate = new Date();
                break;
            case NEXT_INVOICE_DATE:
				referenceDate = nextInvoiceDate;
                break;
            case LAST_TRANSACTION_DATE:
                referenceDate = billingRun.getLastTransactionDate();
                break;
            case END_DATE:
                referenceDate = billingRun.getEndDate();
                break;
            default:
                break;
            }
        }
        return referenceDate;
    }

	/**
	 * @param invoicingItems
	 * @param billingAccount
	 * @param invoice
	 * @param isExonerated 
	 * @return 
	 */
	private Set<SubCategoryInvoiceAgregate> createInvoiceAgregates(BillingAccountDetailsItem billingAccountDetailsItem, List<InvoicingItem> invoicingItems, BillingAccount billingAccount, Invoice invoice) {
        String languageCode = getTradingLanguageCode(billingAccountDetailsItem.getTradingLanguageId());
        boolean calculateTaxOnSubCategoryLevel = invoice.getInvoiceType().getTaxScript() == null;
		Map<SubCategoryInvoiceAgregate, List<InvoicingItem>> itemsBySubCategory = createInvoiceSubCategories(invoicingItems, invoice);
		
        List<DiscountPlanItem> applicableDiscountPlanItems = getApplicableDiscounts(billingAccountDetailsItem, invoice);
        final Map<String, List<SubCategoryInvoiceAgregate>> scMap = itemsBySubCategory.keySet().stream().collect(Collectors.groupingBy(SubCategoryInvoiceAgregate::getCategoryAggKey));
		for (List<SubCategoryInvoiceAgregate> scAggregateList : scMap.values()) {
			CategoryInvoiceAgregate cAggregate = initInvoiceCategoryAgg(billingAccount, invoice, languageCode,scAggregateList);
			for (SubCategoryInvoiceAgregate scAggregate : scAggregateList) {
				cAggregate.addSubCategoryInvoiceAggregate(scAggregate);
				if (!BigDecimal.ZERO.equals(scAggregate.getAmount())) {
	                for (DiscountPlanItem discountPlanItem : applicableDiscountPlanItems) {
						initDiscountAggregates(billingAccount, invoice, scAggregate, itemsBySubCategory.get(scAggregate), cAggregate, discountPlanItem);
	                }
	    		}
			}
		}
        initTaxAggregations(billingAccountDetailsItem, invoice, calculateTaxOnSubCategoryLevel, billingAccount, languageCode, invoicingItems);
        invoice.setNetToPay(invoice.getAmountWithTax().add(invoice.getDueBalance() != null ? invoice.getDueBalance() : BigDecimal.ZERO));
        return itemsBySubCategory.keySet();
	}

	private CategoryInvoiceAgregate initInvoiceCategoryAgg(BillingAccount billingAccount, Invoice invoice, String languageCode, List<SubCategoryInvoiceAgregate> scAggregateList) {
		final SubCategoryInvoiceAgregate firstSCIA = scAggregateList.get(0);
		final InvoiceSubCategory invoiceSubCategory = firstSCIA.getInvoiceSubCategory();
		final InvoiceCategory invoiceCategory = invoiceSubCategory.getInvoiceCategory();

		CategoryInvoiceAgregate cAggregate = new CategoryInvoiceAgregate(invoiceCategory, billingAccount, firstSCIA.getUserAccount(), invoice);
		cAggregate.updateAudit(currentUser);
		addTranslatedDescription(languageCode, invoiceCategory, cAggregate,"C");
		invoice.addInvoiceAggregate(cAggregate);
		return cAggregate;
	}

	private Map<SubCategoryInvoiceAgregate, List<InvoicingItem>> createInvoiceSubCategories(List<InvoicingItem> invoicingItems, Invoice invoice) {
		final Map<String, List<InvoicingItem>> scaGroup = invoicingItems.stream().collect(Collectors.groupingBy(InvoicingItem::getScaKey));
		Map<SubCategoryInvoiceAgregate, List<InvoicingItem>> itemsBySubCategory = new HashMap<SubCategoryInvoiceAgregate, List<InvoicingItem>>();
		scaGroup.values().stream().forEach(items->initSubCategoryInvoiceAggregate(items, invoice, itemsBySubCategory));
		return itemsBySubCategory;
	}
	
	/**
	 * @param item
	 * @param invoice
	 * @param subCategoryAggregates
	 * @return
	 */
	private void initSubCategoryInvoiceAggregate(List<InvoicingItem> items, Invoice invoice, Map<SubCategoryInvoiceAgregate, List<InvoicingItem>> itemsBySubCategory) {
        final InvoicingItem invoicingItem = items.get(0);
		InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.finFromMap(invoicingItem.getInvoiceSubCategoryId());
        SubCategoryInvoiceAgregate scAggregate = new SubCategoryInvoiceAgregate(invoiceSubCategory, invoice.getBillingAccount(), getEntityManager().getReference(UserAccount.class, invoicingItem.getUserAccountId()), getEntityManager().getReference(WalletInstance.class, invoicingItem.getWalletId()), invoice, invoiceSubCategory.getAccountingCode());
		scAggregate.updateAudit(currentUser);
		addTranslatedDescription(getTradingLanguageCode(invoicingItem.getBillingAccountId()), invoiceSubCategory, scAggregate,"");
		setAggregationAmounts(items, scAggregate, invoicingItem);
		addInvoiceAggregateWithAmounts(invoice, scAggregate);
		itemsBySubCategory.put(scAggregate,items);
	}

	/**
	 * @param item
	 * @param billingRun
	 * @param billingAccount 
	 * @param billingCycle 
	 * @return
	 */
	private Invoice initInvoice(BillingAccountDetailsItem billingAccountDetailsItem, InvoicingItem item, BillingRun billingRun, BillingAccount billingAccount, BillingCycle billingCycle) {
		final boolean isPrepaid = item.isPrepaid();
		InvoiceType invoiceType = determineInvoiceType(isPrepaid, false, billingCycle, billingRun, billingAccount);
		Invoice invoice = new Invoice();
		invoice.setBillingAccount(billingAccount);
		invoice.setSeller(getEntityManager().getReference(Seller.class, item.getSellerId()));
		invoice.setStatus(InvoiceStatusEnum.CREATED);
		invoice.setInvoiceType(invoiceType);
		invoice.setPrepaid(isPrepaid);
		invoice.setInvoiceDate(billingRun.getInvoiceDate());
		if (billingRun != null) {
			invoice.setBillingRun(billingRun);
		}
		/*#MEL manage order.subscription cases
		if (entity instanceof Order) {
			Order order =(Order) entity;
			invoice.setOrder(order);

		} else if (entity instanceof Subscription) {
			invoice.setSubscription((Subscription) entity);
		}*/
		
		if (billingAccountDetailsItem.getPaymentMethodId() != null) {
			invoice.setPaymentMethodType(billingAccountDetailsItem.getPaymentMethodType());
			invoice.setPaymentMethod(getEntityManager().getReference(PaymentMethod.class, billingAccountDetailsItem.getPaymentMethodId()));
		}
		// Set due balance
		invoice.setDueBalance(billingAccountDetailsItem.getDueBalance().setScale(getInvoiceRounding(), getRoundingMode()));
        if (billingAccountDetailsItem.getElectronicBillingEnabled()) {
            invoice.setDontSend(true);
        }
		return invoice;
	}

    /**
	 * @param billingAccount
	 * @return
	 */
	private BigDecimal getThresholdByInvoice(BillingAccountDetailsItem billingAccountDetailsItem, ThresholdOptionsEnum type, BillingCycle bc) {
		BigDecimal threshold = null;
		if (billingAccountDetailsItem.getInvoicingThreshold() != null && (billingAccountDetailsItem.getCheckThreshold() == null || type == billingAccountDetailsItem.getCheckThreshold())) {
			threshold = billingAccountDetailsItem.getInvoicingThreshold();
		} else if ( !bc.isThresholdPerEntity() && bc.getInvoicingThreshold() != null && (billingAccountDetailsItem.getCheckThreshold() == null || type == bc.getCheckThreshold())) {
			threshold = bc.getInvoicingThreshold();
		}
		return threshold;
	}

    /**
     * Find by billing run.
     *
     * @param billingRun billing run
     * @return list of invoice for given billing run
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> findByBillingRun(BillingRun billingRun) {
        QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
        qb.addCriterionEntity("billingRun", billingRun);
        try {
            return (List<Invoice>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to find by billingRun", e);
            return null;
        }
    }

    /**
     * Format invoice date.
     *
     * @param invoiceDate invoice date
     * @return invoice date as string
     */
    public String formatInvoiceDate(Date invoiceDate) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATERN);
        return dateFormat.format(invoiceDate);
    }

    /**
     * Evaluate prefix el expression.
     *
     * @param prefix prefix of EL expression
     * @param invoice invoice
     * @return evaluated value
     * @throws BusinessException business exception
     */
    public static String evaluatePrefixElExpression(String prefix, Invoice invoice) throws BusinessException {
        if (StringUtils.isBlank(prefix)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (prefix.indexOf("entity") >= 0) {
            userMap.put("entity", invoice);
        }
        if (prefix.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }
        String result = ValueExpressionWrapper.evaluateExpression(prefix, userMap, String.class);
        return result;
    }

    /**
     * Evaluate integer expression.
     *
     * @param expression expression as string
     * @param billingAccount billing account
     * @param invoice which is used to evaluate
     * @param order order related to invoice.
     * @return result of evaluation
     * @throws BusinessException business exception.
     */
    public Integer evaluateDueDelayExpression(String expression, BillingAccount billingAccount, Invoice invoice, Order order) throws BusinessException {
        Integer result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("order") >= 0) {
            userMap.put("order", order);
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Integer.class);
        try {
            result = (Integer) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to Integer but " + res);
        }
        return result;
    }

    /**
     * Evaluate billing template name.
     *
     * @param expression the expression
     * @param invoice the invoice
     * @return the string
     */
    public String evaluateBillingTemplateName(String expression, Invoice invoice) {
        String billingTemplateName = null;
        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("invoice", invoice);
            try {
                String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);

                if (value != null) {
                    billingTemplateName = value;
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
            }
        }
        billingTemplateName = StringUtils.normalizeFileName(billingTemplateName);
        return billingTemplateName;
    }

    /**
     * Determine invoice type given the following criteria
     * 
     * If is a prepaid invoice, default prepaid type is used.<br/>
     * If is a draft invoice, default draft type is used.<br/>
     * Otherwise invoice type is determined in the following order:<br/>
     * 1. billingCycle.invoiceTypeEl expression evaluated with billingRun and billingAccount a parameters, <br/>
     * 2. bilingCycle.invoiceType, <br/>
     * 3. Default commercial invoice type
     * 
     * @param isPrepaid Is it for prepaid invoice. If True, default prepaid type is used. Excludes other criteria.
     * @param isDraft Is it a draft invoice. If true, default draft type is used. Excludes other criteria.
     * @param billingCycle Billing cycle
     * @param billingRun Billing run
     * @param billingAccount Billing account
     * @return Applicable invoice type
     * @throws BusinessException General business exception
     */
    private InvoiceType determineInvoiceType(boolean isPrepaid, boolean isDraft, BillingCycle billingCycle, BillingRun billingRun, BillingAccount billingAccount) throws BusinessException {
        InvoiceType invoiceType = null;
        if (isPrepaid) {
            invoiceType = invoiceTypeService.getDefaultPrepaid();
        } else if (isDraft) {
            invoiceType = invoiceTypeService.getDefaultDraft();
        } else {
            if (!StringUtils.isBlank(billingCycle.getInvoiceTypeEl())) {
                String invoiceTypeCode = evaluateInvoiceType(billingCycle.getInvoiceTypeEl(), billingRun, billingAccount);
                invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
            }
            if (invoiceType == null) {
                invoiceType = billingCycle.getInvoiceType();
            }
            if (invoiceType == null) {
                invoiceType = invoiceTypeService.getDefaultCommertial();
            }
        }
        return invoiceType;
    }

    public String evaluateInvoiceType(String expression, BillingRun billingRun, BillingAccount billingAccount) {
        String invoiceTypeCode = null;

        if (!StringUtils.isBlank(expression)) {
            Map<Object, Object> contextMap = new HashMap<>();
            contextMap.put("br", billingRun);
            contextMap.put("ba", billingAccount);
            try {
                String value = ValueExpressionWrapper.evaluateExpression(expression, contextMap, String.class);
                if (value != null) {
                    invoiceTypeCode = (String) value;
                }
            } catch (BusinessException e) {
                // Ignore exceptions here - a default pdf filename will be used instead. Error is logged in EL evaluation
            }
        }
        return invoiceTypeCode;
    }

    /**
     * Determine an invoice template to use. Rule for selecting an invoiceTemplate is: InvoiceType &gt; BillingCycle &gt; default.
     *
     * @param invoice invoice
     * @param billingCycle Billing cycle
     * @param invoiceType Invoice type
     * @return Invoice template name
     */
    public String getInvoiceTemplateName(Invoice invoice, BillingCycle billingCycle, InvoiceType invoiceType) {
        String billingTemplateName = "default";
        if (invoiceType != null && !StringUtils.isBlank(invoiceType.getBillingTemplateNameEL())) {
            billingTemplateName = evaluateBillingTemplateName(invoiceType.getBillingTemplateNameEL(), invoice);
        } else if (billingCycle != null && !StringUtils.isBlank(billingCycle.getBillingTemplateNameEL())) {
            billingTemplateName = evaluateBillingTemplateName(billingCycle.getBillingTemplateNameEL(), invoice);
        } else if (invoiceType != null && !StringUtils.isBlank(invoiceType.getBillingTemplateName())) {
            billingTemplateName = invoiceType.getBillingTemplateName();
        } else if (billingCycle != null && billingCycle.getInvoiceType() != null && !StringUtils.isBlank(billingCycle.getInvoiceType().getBillingTemplateName())) {
            billingTemplateName = billingCycle.getInvoiceType().getBillingTemplateName();
        } else if (billingCycle != null && !StringUtils.isBlank(billingCycle.getBillingTemplateName())) {
            billingTemplateName = billingCycle.getBillingTemplateName();
        }
        return billingTemplateName;
    }

    /**
     * Get a summarized information for invoice numbering. Contains grouping by invoice type, seller, invoice date and a number of invoices.
     *
     * @param billingRunId Billing run id
     * @return A list of invoice identifiers
     */
    @SuppressWarnings("unchecked")
    public List<InvoicesToNumberInfo> getInvoicesToNumberSummary(Long billingRunId) {
        List<InvoicesToNumberInfo> invoiceSummaries = new ArrayList<>();
        List<Object[]> summary = getEntityManager().createNamedQuery("Invoice.invoicesToNumberSummary").setParameter("billingRunId", billingRunId).getResultList();
        for (Object[] summaryInfo : summary) {
            invoiceSummaries.add(new InvoicesToNumberInfo((Long) summaryInfo[0], (Long) summaryInfo[1], (Date) summaryInfo[2], (Long) summaryInfo[3]));
        }
        return invoiceSummaries;
    }

    /**
     * Nullify BR's invoices file names (xml and pdf).
     *
     * @param billingRun the billing run
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void nullifyInvoiceFileNames(BillingRun billingRun) {
        getEntityManager().createNamedQuery("Invoice.nullifyInvoiceFileNames").setParameter("billingRun", billingRun).executeUpdate();
    }

    /**
     * A first part of invoiceService.create() method. Does not call PersistenceService.create(), Need to call InvoiceService.postCreate() separately
     *
     * @param invoice Invoice entity
     * @throws BusinessException General business exception
     */
    @Override
    public void create(Invoice invoice) throws BusinessException {
        invoice.updateAudit(currentUser);
        if (invoice instanceof ICustomFieldEntity) {
            customFieldInstanceService.scheduleEndPeriodEvents((ICustomFieldEntity) invoice);
        }
        getEntityManager().persist(invoice);
        log.trace("end of create {}. entity id={}.", invoice.getClass().getSimpleName(), invoice.getId());
    }

    /**
     * A second part of invoiceService.create() method.
     *
     * @param invoice Invoice entity
     * @throws BusinessException General business exception
     */
    public void postCreate(Invoice invoice) throws BusinessException {
        entityCreatedEventProducer.fire((BaseEntity) invoice);
        cfValueAccumulator.entityCreated(invoice);
        log.trace("end of post create {}. entity id={}.", invoice.getClass().getSimpleName(), invoice.getId());
    }

	private boolean recalculateTaxIfNeeded(BillingAccount billingAccount, InvoiceType invoiceType,  
			Boolean isExonerated, RatedTransaction ratedTransaction, BillingRun billingRun) {
		if (isExonerated == null) {
		    isExonerated = billingAccountService.isExonerated(billingAccount);
		}
        int rtRounding = appProvider.getRounding();
        RoundingModeEnum rtRoundingMode = appProvider.getRoundingMode();
        // InvoiceType.taxScript will calculate all tax aggregates at once.
        boolean calculateTaxOnSubCategoryLevel = invoiceType.getTaxScript() == null;
        // Should tax calculation on subcategory level be done externally
        //boolean calculatesExternalTax = "YES".equalsIgnoreCase((String) appProvider.getCfValue("OPENCELL_ENABLE_TAX_CALCULATION"));
        // Tax change mapping. Key is ba.id_taxClass.id and value is an array of [Tax to apply, True/false if tax has changed]
        Map<String, Object[]> taxChangeMap = new HashMap<>();
		Tax tax = ratedTransaction.getTax();
		boolean taxWasRecalculated=false;
		// Check if tax has to be recalculated. Does not apply to RatedTransactions that had tax explicitly set/overridden
		if (calculateTaxOnSubCategoryLevel && !ratedTransaction.isTaxOverriden()) {
		    TaxClass taxClass = ratedTransaction.getTaxClass();
		    final Long taxClassId = ratedTransaction.getTaxClass().getId();
			String taxChangeKey = billingAccount.getId() + "_" + taxClassId;

		    Object[] changedToTax = taxChangeMap.get(taxChangeKey);
		    if (changedToTax == null) {
		        Object[] applicableTax = getApplicableTax(tax, isExonerated, taxClass, ratedTransaction.getUserAccount(), billingRun.getInvoiceDate(), ratedTransaction.getSeller(), billingAccount);
		        changedToTax = applicableTax;
		        taxChangeMap.put(taxChangeKey, changedToTax);
		        if ((boolean) changedToTax[1]) {
		            log.debug("Will update rated transactions of Billing account {} and tax class {} with new tax from {}/{}% to {}/{}%", billingAccount.getId(), taxClassId, tax.getId(), tax.getPercent(),
		                ((Tax) changedToTax[0]).getId(), ((Tax) changedToTax[0]).getPercent());
		        }
		    }
		    taxWasRecalculated = (boolean) changedToTax[1];
		    if (taxWasRecalculated) {
		        tax = (Tax) changedToTax[0];
		        ratedTransaction.setTaxRecalculated(true);
		    }
		}
		if (taxWasRecalculated) {
		    ratedTransaction.setTax(tax);
		    ratedTransaction.setTaxPercent(tax.getPercent());
		    ratedTransaction.computeDerivedAmounts(isEntreprise(), rtRounding, rtRoundingMode);
		}
		return taxWasRecalculated;
	}

	private void addTranslatedDescription(String languageCode, I18nDescripted invoiceSubCategory, InvoiceAgregate aggregate, String prefix) {
		String translationKey = prefix+invoiceSubCategory.getId() + "_" + languageCode;
		String descTranslated = descriptionMap.get(translationKey);
		if (descTranslated == null) {
		    descTranslated = invoiceSubCategory.getDescriptionOrCode();
		    if ((invoiceSubCategory.getDescriptionI18n() != null) && (invoiceSubCategory.getDescriptionI18n().get(languageCode) != null)) {
		        descTranslated = invoiceSubCategory.getDescriptionI18n().get(languageCode);
		    }
		    descriptionMap.put(translationKey, descTranslated);
		}
		aggregate.setDescription(descTranslated);
	}

	private void initTaxAggregations(BillingAccountDetailsItem BillingAccountDetailsItem, Invoice invoice, boolean calculateTaxOnSubCategoryLevel, BillingAccount billingAccount, String languageCode, List<InvoicingItem> invoicingItems) {
		InvoicingItem firstItem = invoicingItems.get(0);
		Boolean isExonerated = billingAccountService.isExonerated(billingAccount, BillingAccountDetailsItem.getExoneratedFromTaxes(), BillingAccountDetailsItem.getExonerationTaxEl());
        if (isExonerated) {
        	return;
        }
		if (calculateTaxOnSubCategoryLevel) {
			Map<Long, List<InvoicingItem>> itemsByTax = invoicingItems.stream().collect(Collectors.groupingBy(InvoicingItem::getTaxId));
			//tax aggregations will be used to override amoounts 
			invoice.initAmounts();
	        for(List<InvoicingItem> items: itemsByTax.values()) {
				final Tax tax = getTax(firstItem.getTaxId());
				TaxInvoiceAgregate taxAggregate = new TaxInvoiceAgregate(billingAccount, tax, tax.getPercent(), invoice);
	            taxAggregate.updateAudit(currentUser);
	            addTranslatedDescription(languageCode, getTax(firstItem.getTaxId()), taxAggregate, "T");
	            setAggregationAmounts(items, taxAggregate, firstItem);
	            addInvoiceAggregateWithAmounts(invoice, taxAggregate);
	    		
			}
		} else {
    		 // If tax calculation is not done at subcategory level, then call a global script to do calculation for the whole invoice
    		if ((invoice.getInvoiceType() != null) && (invoice.getInvoiceType().getTaxScript() != null)) {
    			Map<String, TaxInvoiceAgregate> taxAggregates = taxScriptService.createTaxAggregates(invoice.getInvoiceType().getTaxScript().getCode(), invoice);
                if (taxAggregates != null) {
                    for (TaxInvoiceAgregate taxAggregate : taxAggregates.values()) {
                        taxAggregate.setInvoice(invoice);
                        invoice.addInvoiceAggregate(taxAggregate);
                    }
                }
            }
        }
	}

	/**
	 * @param taxId
	 * @return
	 */
	private Tax getTax(Long taxId) {
		if(taxes.isEmpty()) {
			taxes =  getEntityManager().createNamedQuery("Tax.getAllTaxes",Tax.class).getResultList().stream().collect(Collectors.toMap(Tax::getId, Function.identity()));
		}
		return taxes.get(taxId);
	}
	
	private DiscountPlan getDiscountPlan(long discountPlanId) {
		if(discountPlans.isEmpty()) {
			//final BinaryOperator<DiscountPlan> mergeFunction = (x1, x2) -> {x1.getDiscountPlanItems().addAll(x2.getDiscountPlanItems());return x1;};
			discountPlans =  getEntityManager().createNamedQuery("DiscountPlan.getAll",DiscountPlan.class).getResultList().stream().collect(Collectors.toMap(DiscountPlan::getId, Function.identity(), (x1, x2) -> x1));
		}
		return discountPlans.get(discountPlanId);
	}
	
	private String getTradingLanguageCode(Long tradingLanguageId) {
		if(tradingLanguages.isEmpty()) {
			tradingLanguages =  getEntityManager().createNamedQuery("TradingLanguage.findAll",TradingLanguage.class).getResultList().stream().collect(Collectors.toMap(TradingLanguage::getId, Function.identity()));
		}
		return tradingLanguages.get(tradingLanguageId)!=null?tradingLanguages.get(tradingLanguageId).getLanguageCode():"";
	}

	private void setAggregationAmounts(List<InvoicingItem> items, InvoiceAgregate invoiceAggregate, InvoicingItem invoicingItem) {
		InvoicingItem summuryItem = new InvoicingItem(items);
		invoiceAggregate.setItemNumber(summuryItem.getCount());
		Tax tax = getTax(invoicingItem.getTaxId());
		BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(summuryItem.getAmountWithoutTax(), summuryItem.getAmountWithTax(), tax.getPercent(), isEntreprise() , getInvoiceRounding(), getRoundingMode());
        invoiceAggregate.setAmountWithoutTax(amounts[0]);
        invoiceAggregate.setAmountWithTax(amounts[1]);
		invoiceAggregate.setAmountTax(amounts[2]);
		if(invoiceAggregate instanceof SubCategoryInvoiceAgregate) {
			((SubCategoryInvoiceAgregate)invoiceAggregate).addRTs(summuryItem.getrtIDs());
		}
	}
	
	private List<DiscountPlanItem> getApplicableDiscounts(BillingAccountDetailsItem billingAccountDetailsItem, Invoice invoice) {
		// Determine which discount plan items apply to this invoice
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
    	//#MEL subscription!=null only if billing by subscription
       /* Subscription subscription = invoice.getSubscription();
        if (subscription == null) {
            List<DiscountPlanItem> result = getApplicableDiscountPlanItems(billingAccount, subscriptionDiscountPlanInstancesfromBillingAccount(billingAccount), invoice);
            ofNullable(result).ifPresent(discountPlans -> applicableDiscountPlanItems.addAll(discountPlans));
        } else if ( subscription.getDiscountPlanInstances() != null && !subscription.getDiscountPlanInstances().isEmpty()) {
        	applicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccount, subscription.getDiscountPlanInstances(), invoice));
        }*/
        final List<DiscountPlanSummary> discountPlanInstances = billingAccountDetailsItem.getdiscountPlanSummaries();
		if (discountPlanInstances != null && !discountPlanInstances.isEmpty()) {
        	applicableDiscountPlanItems.addAll(getApplicableDiscountPlanItems(billingAccountDetailsItem, discountPlanInstances,invoice));
        }
		return applicableDiscountPlanItems;
	}

    private List<DiscountPlanInstance> subscriptionDiscountPlanInstancesfromBillingAccount(BillingAccount billingAccount) {
        return billingAccount.getUsersAccounts().stream().map(userAccount -> userAccount.getSubscriptions())
                .map(this::addSubscriptionDiscountPlan).flatMap(Collection::stream).collect(toList());
    }

    private List<DiscountPlanInstance> addSubscriptionDiscountPlan(List<Subscription> subscriptions) {
        return subscriptions.stream().map(Subscription::getDiscountPlanInstances)
                .flatMap(Collection::stream).collect(toList());
    }

    private SubCategoryInvoiceAgregate initDiscountAggregates(BillingAccount billingAccount, Invoice invoice, 
            SubCategoryInvoiceAgregate scAggregate, List<InvoicingItem> itemsBySubCategory, CategoryInvoiceAgregate cAggregate, DiscountPlanItem discountPlanItem) throws BusinessException {
        BigDecimal amountToApplyDiscountOn = isEntreprise() ? scAggregate.getAmountWithoutTax() : scAggregate.getAmountWithTax();
        if (BigDecimal.ZERO.compareTo(amountToApplyDiscountOn) == 0) {
            return null;
        }
        // Apply discount if matches the category, subcategory, or applies to any category
        if (!((discountPlanItem.getInvoiceCategory() == null && discountPlanItem.getInvoiceSubCategory() == null)
                || (discountPlanItem.getInvoiceSubCategory() != null && discountPlanItem.getInvoiceSubCategory().getId().equals(scAggregate.getInvoiceSubCategory().getId()))
                || (discountPlanItem.getInvoiceCategory() != null && discountPlanItem.getInvoiceSubCategory() == null && discountPlanItem.getInvoiceCategory().getId().equals(scAggregate.getInvoiceSubCategory().getInvoiceCategory().getId())))) {
            return null;
        }
        BigDecimal discountValue = getDiscountAmountOrPercent(invoice, scAggregate, amountToApplyDiscountOn, discountPlanItem);
        if (BigDecimal.ZERO.compareTo(discountValue) == 0) {
            return null;
        }

        BigDecimal discountAmount = null;
        // Percent based discount
        if (discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.PERCENTAGE) {
        	itemsBySubCategory.stream().forEach(item->apllyDiscountPercent(item, discountValue.abs().divide(HUNDRED)));
        	discountAmount = applyDiscount(amountToApplyDiscountOn, discountValue.abs().divide(HUNDRED));
        } else {
            discountAmount = discountValue;
            dispatchDiscountBetweenItems(itemsBySubCategory, discountAmount, amountToApplyDiscountOn);
        }

        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        SubCategoryInvoiceAgregate discountAggregate = new SubCategoryInvoiceAgregate(scAggregate.getInvoiceSubCategory(), billingAccount, scAggregate.getUserAccount(), scAggregate.getWallet(), invoice, null);
        discountAggregate.updateAudit(currentUser);
        discountAggregate.setItemNumber(scAggregate.getItemNumber());
        discountAggregate.setCategoryInvoiceAgregate(cAggregate);
        discountAggregate.setDiscountAggregate(true);
        if (discountPlanItem.getDiscountPlanItemType().equals(DiscountPlanItemTypeEnum.PERCENTAGE)) {
            discountAggregate.setDiscountPercent(discountValue);
        }
        discountAggregate.setDiscountPlanItem(discountPlanItem);
        discountAggregate.setDescription(discountPlanItem.getCode());
        
        InvoicingItem discountedItemSum= new InvoicingItem(itemsBySubCategory);
        discountAggregate.setAmountWithoutTax(discountedItemSum.getAmountWithoutTax().subtract(scAggregate.getAmountWithoutTax()));
        discountAggregate.setAmountWithTax(discountedItemSum.getAmountWithTax().subtract(scAggregate.getAmountWithTax()));
        discountAggregate.setAmountTax(discountedItemSum.getAmountTax().subtract(scAggregate.getAmountTax()));
        addInvoiceAggregateWithAmounts(invoice, discountAggregate);
        return discountAggregate;
    }

	public void addInvoiceAggregateWithAmounts(Invoice invoice, InvoiceAgregate invoiceAgg) {
		invoice.addInvoiceAggregate(invoiceAgg);
		invoice.addAmountTax(invoiceAgg.getAmountTax());
		invoice.addAmountWithTax(invoiceAgg.getAmountWithTax());
		invoice.addAmountWithoutTax(invoiceAgg.getAmountWithoutTax());
	}

	private BigDecimal applyDiscount(BigDecimal amountToApplyDiscountOn, BigDecimal discountValue) {
		return amountToApplyDiscountOn.multiply(BigDecimal.ONE.subtract(discountValue.abs())).setScale(getInvoiceRounding(), getRoundingMode());
	}
    
    private void dispatchDiscountBetweenItems(List<InvoicingItem> taxItems, BigDecimal disountAmount, BigDecimal amount){
    	BigDecimal percent = disountAmount.divide(amount, 12, getRoundingMode());
    	taxItems.stream().forEach(item->apllyDiscountPercent(item, percent));
    	//TODO #MEL adjust delta
    }

	private void apllyDiscountPercent(InvoicingItem taxItem, BigDecimal percent) {
		BigDecimal[] amounts = getAppliedDiscount(taxItem.getAmountWithoutTax(), taxItem.getAmountWithTax(), percent);
		taxItem.setAmountWithTax(amounts[0]);
		taxItem.setAmountWithoutTax(amounts[1]);
		taxItem.setAmountTax(amounts[2]);
	}

	private BigDecimal[] getAppliedDiscount(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal percent) {
		amountWithoutTax = applyDiscount(amountWithoutTax, percent);
		amountWithTax = applyDiscount(amountWithTax, percent);
        return new BigDecimal[] { amountWithTax, amountWithoutTax, amountWithTax.subtract(amountWithoutTax) };
	}

	private RoundingMode getRoundingMode() {
		return appProvider.getInvoiceRoundingMode().getRoundingMode();
	}

	private int getInvoiceRounding() {
		return appProvider.getInvoiceRounding();
	}
	
	private boolean isEntreprise() {
		return appProvider.isEntreprise();
	}

	/**
     * Determine a discount amount or percent to apply
     *
     * @param invoice Invoice to apply discount on
     * @param scAggregate Subcategory aggregate to apply discount on
     * @param amount Amount to apply discount on
     * @param discountPlanItem Discount configuration
     * @return A discount percent (0-100)
     */
    private BigDecimal getDiscountAmountOrPercent(Invoice invoice, SubCategoryInvoiceAgregate scAggregate, BigDecimal amount, DiscountPlanItem discountPlanItem) {
        BigDecimal computedDiscount = discountPlanItem.getDiscountValue();
        final String dpValueEL = discountPlanItem.getDiscountValueEL();
        if (isNotBlank(dpValueEL)) {
        	//#MEL TODO
            final BigDecimal evalDiscountValue = evaluateDiscountPercentExpression(dpValueEL, scAggregate.getBillingAccount(), scAggregate.getWallet(), invoice, amount);
            log.debug("for discountPlan {} percentEL -> {}  on amount={}", discountPlanItem.getCode(), computedDiscount, amount);
            if (computedDiscount != null) {
                computedDiscount = evalDiscountValue;
            }
        }
        if (computedDiscount == null || amount == null) {
            return BigDecimal.ZERO;
        }
        return computedDiscount;
    }

    private List<DiscountPlanItem> getApplicableDiscountPlanItems(BillingAccountDetailsItem billingAccountDetailsItem, List<DiscountPlanSummary> discountPlansum, Invoice invoice)
            throws BusinessException {
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>();
        for (DiscountPlanSummary dps : discountPlansum) {
            final DiscountPlan discountPlan = getDiscountPlan(dps.getDiscountPlanId());
			if (!(isEffective(dps, invoice.getInvoiceDate()) && discountPlan.isActive())) {
                continue;
            }
            for (DiscountPlanItem discountPlanItem : discountPlan.getDiscountPlanItems()) {
                if (discountPlanItem.isActive() && matchDiscountPlanItemExpression(discountPlanItem.getExpressionEl(), invoice, dps, discountPlan)) {
                    applicableDiscountPlanItems.add(discountPlanItem);
                }
            }
        }
        return applicableDiscountPlanItems;
    }

	private boolean isEffective(DiscountPlanSummary dpi, Date invoiceDate) {
		return (dpi.getStartDate()==null || invoiceDate.compareTo(dpi.getStartDate()) >= 0) && (dpi.getEndDate()==null || invoiceDate.before(dpi.getEndDate()));
	}

	/**
     * @param expression EL exprestion
     * @param customerAccount customer account
     * @param billingAccount billing account
     * @param invoice invoice
     * @param dpi the discount plan instance
     * @return true/false
     * @throws BusinessException business exception.
     */
    private boolean matchDiscountPlanItemExpression(String expression, Invoice invoice, DiscountPlanSummary dps, DiscountPlan discountPlan) throws BusinessException {
        Boolean result = true;

        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        BillingAccount billingAccount = retrieveIfNotManaged(invoice).getBillingAccount();
		if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount .getCustomerAccount());
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("iv") >= 0) {
            userMap.put("iv", invoice);
        }
        if (expression.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("dpi") >= 0) {
            DiscountPlanInstance dpi = new DiscountPlanInstance();
            dpi.setDiscountPlan(discountPlan);
            dpi.setStartDate(dps.getStartDate());
            dpi.setEndDate(dps.getEndDate());
			userMap.put("dpi", dpi );
        }
        if (expression.indexOf("su") >= 0) {
            userMap.put("su", invoice.getSubscription());
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @param expression     el expression
     * @param billingAccount billing account
     * @param wallet         wallet
     * @param invoice        invoice
     * @param subCatTotal    total of sub category
     * @return amount
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateDiscountPercentExpression(String expression, BillingAccount billingAccount, WalletInstance wallet, Invoice invoice, BigDecimal subCatTotal)
            throws BusinessException {
    	//#MEL
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("ca", billingAccount.getCustomerAccount());
        userMap.put("ba", billingAccount);
        userMap.put("iv", invoice);
        userMap.put("invoice", invoice);
        userMap.put("wa", wallet);
        userMap.put("amount", subCatTotal);
        BigDecimal result = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        return result;
    }
    
    private void evalDueDate(Invoice invoice, BillingCycle billingCycle, String orderDueDateDelayEL, String caDueDateDelayEL) {
        BillingAccount billingAccount = invoice.getBillingAccount();
        Order order = invoice.getOrder();
        // Determine invoice due date delay either from Order, Customer account or Billing cycle
        Integer delay = billingCycle.getDueDateDelay();
        String dueDateDelayEL = (order != null && !StringUtils.isBlank(orderDueDateDelayEL)) ? orderDueDateDelayEL:
        	(!StringUtils.isBlank(caDueDateDelayEL)) ? caDueDateDelayEL : billingCycle.getDueDateDelayEL();
        delay = dueDateDelayEL == null ? billingCycle.getDueDateDelay() : evaluateDueDelayExpression(dueDateDelayEL, billingAccount, invoice, order);
        Date dueDate = invoice.getInvoiceDate();
        if (delay != null) {
            dueDate = DateUtils.addDaysToDate(invoice.getInvoiceDate(), delay);
        } else {
            throw new BusinessException("Due date delay is null");
        }
        invoice.setDueDate(dueDate);
    }

    /**
     * Recalculate tax to see if it has changed
     * 
     * @param tax Previous tax
     * @param isExonerated Is Billing account exonerated from taxes
     * @param invoice Invoice in reference
     * @param taxClass Tax class
     * @param userAccount User account to calculate tax by external program
     * @param invoiceDate 
     * @param seller 
     * @param billingAccount 
     * @param taxZero Zero tax to apply if Billing account is exonerated
     * @param calculateExternalTax Should tax be calculated by an external program if invoiceSubCategory has such script set
     * @return An array containing applicable tax and True/false if tax % has changed from a previous tax
     * @throws BusinessException Were not able to determine a tax
     */
    private Object[] getApplicableTax(Tax tax, boolean isExonerated, TaxClass taxClass, UserAccount userAccount, Date invoiceDate, Seller seller, BillingAccount billingAccount) throws BusinessException {
        if (isExonerated) {
        	Tax taxZero = taxService.getZeroTax() ;
            return new Object[] { taxZero, false };
        } else {
            TaxInfo recalculatedTaxInfo = taxMappingService.determineTax(taxClass, seller, billingAccount, userAccount, invoiceDate, false, false);
            Tax recalculatedTax = recalculatedTaxInfo.tax;
            return new Object[] { recalculatedTax, !tax.getId().equals(recalculatedTax.getId()) };
        }
    }

    /**
     * Retrun the total of positive rated transaction grouped by billing account for a billing run.
     *
     * @param billingRun the billing run
     * @return a map of positive rated transaction grouped by billing account.
     */
    public List<Object[]> getTotalInvoiceableAmountByBR(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("Invoice.sumInvoiceableAmountByBR").setParameter("billingRunId", billingRun.getId()).getResultList();
    }
    
	/**
	 * @param billingRun
	 * @param max
	 * @param min 
	 */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void checkDirtyTaxes(BillingRun billingRun) {
    	List<Object[]> results = getEntityManager().createNamedQuery("RatedTransaction.getUsedTaxesSummary").setParameter("billingRunId", billingRun.getId()).getResultList();
    	for(Object[] result : results) {
    		int i=0;
    		Long taxClassId =(Long)result[i++];
    		Long taxCategoryId=(Long)result[i++];
    		Long sellerCountryId=(Long)result[i++];
    		Long buyerCountryId=(Long)result[i++];
    		Long taxId = (Long)result[i++];
    		Tax taxToUse = taxMappingService.trySimpleTaxMappingMatch(taxClassId, taxCategoryId, buyerCountryId, sellerCountryId, billingRun.getInvoiceDate());
    		//tax mapping found with the easy way
    		if(taxToUse!=null && taxToUse.getId()==taxId) {
        			continue;
    		} 
    		recalculateTaxes(billingRun);
    		return;
    	}
	}
    
	public void recalculateTaxes(BillingRun billingRun) {
    	List<Object[]> results = getEntityManager().createNamedQuery("RatedTransaction.getRecalculableRTDetails").setParameter("billingRunId", billingRun.getId()).getResultList();
    	for(Object[] result : results) {
    		//#MEL should be optimized later
    		RatedTransaction rt = (RatedTransaction) result[0];
    		final BillingAccount billingAccount = rt.getBillingAccount();
			final InvoiceType invoiceType = determineInvoiceType(rt.isPrepaid(), false, billingRun.getBillingCycle(), billingRun, billingAccount);
			recalculateTaxIfNeeded(billingAccount, invoiceType, appProvider.isEntreprise(), rt, billingRun);
    	}
	}
}