package org.meveo.service.mediation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MediationsettingService extends PersistenceService<MediationSetting>{

	private Logger log = LoggerFactory.getLogger(MediationsettingService.class);
	@Inject
	private WalletOperationService walletOperationService;
	@Inject
	private RatedTransactionService ratedTransactionService;
	@Inject
	private UsageRatingService usageRatingService;
	
	public String getEventKeyFromEdrVersionRule(EDR edr) {
		var mediationSettings = this.list();
		if(CollectionUtils.isNotEmpty(mediationSettings) && mediationSettings.size() > 1)
    		throw new BusinessException("More than one Mediation setting is found");
    	if(CollectionUtils.isEmpty(mediationSettings)) return null;
    	if(!mediationSettings.get(0).isEnableEdrVersioning()) return null;
    	Comparator<EdrVersioningRule> sortByPriority = (EdrVersioningRule edrV1, EdrVersioningRule edrV2) -> edrV1.getPriority().compareTo(edrV2.getPriority()); 
    	return Optional.ofNullable(
    			mediationSettings.get(0).getRules().stream()
				.sorted(sortByPriority)
				.filter(edrVersion -> {
					try {
						return ValueExpressionWrapper.evaluateExpression(edrVersion.getCriteriaEL(), Boolean.class, edr);
					}catch(Exception e) {
						log.warn("cant evaluate expression : " + edrVersion.getCriteriaEL() , e);
					}
					return false;
				})
				.findFirst()
				).map(edrVersion -> {
					try {
						return edrVersion != null ? ValueExpressionWrapper.evaluateExpression(edrVersion.get().getKeyEL(), String.class, edr) : null;
					}catch(Exception e) {
						log.warn("cant evaluate expression : " + edrVersion.get().getKeyEL() , e);
					}
					return null;
				}).get();
	}
	
	
    @SuppressWarnings("unchecked")
	public void applyEdrVersioningRule(List<EDR> edrs, CDR cdr){
log.info("start applyEdrVersioningRule HERE");
    	var mediationSettings = this.list();
    	if(CollectionUtils.isNotEmpty(mediationSettings) && mediationSettings.size() > 1)
    		throw new BusinessException("More than one Mediation setting is found");
    	if(CollectionUtils.isEmpty(mediationSettings)) return ;
    	if(!mediationSettings.get(0).isEnableEdrVersioning()) return;
    	Comparator<EdrVersioningRule> sortByPriority = (EdrVersioningRule edrV1, EdrVersioningRule edrV2) -> edrV1.getPriority().compareTo(edrV2.getPriority()); 
    	var edrIterate = edrs.iterator();
    	while(edrIterate.hasNext()) {
    		var edr = edrIterate.next();
log.info("----------START each edr HERE {}", edr);
    		if(edr.getId() == null)
    		    edrService.create(edr);
    		var  errorMessage = "Error evaluating %s  [id= %d, \"%s\"] for CDR: [%s] : %s";
        	var edrVersionRuleOption = mediationSettings.get(0).getRules().stream()
					.sorted(sortByPriority)
					.filter(edrVersion -> {
						var errorMsg = String.format(errorMessage, "criteriaEL", edrVersion.getId(), edrVersion.getCriteriaEL(), cdr, "%s");
						var eval = (Boolean) evaluateEdrVersion(edrVersion.getId(), edrVersion.getCriteriaEL(),edr, cdr, errorMsg, Boolean.class, edrIterate); 
						return eval == null ? false : eval;
					})
					.findFirst();
        	if(edrVersionRuleOption.isPresent()) {
        		var edrVersionRule = edrVersionRuleOption.get();
log.info("edrVersionRule HERE {}", edrVersionRule);
				var errorMsg = String.format(errorMessage, "eventKeyEl", edrVersionRule.getId(), edrVersionRule.getCriteriaEL(), cdr, "%s");
        		String keyEvent =  (String) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getKeyEL(),edr, cdr, errorMsg , String.class, edrIterate);
        		if(StringUtils.isNotEmpty(keyEvent) && edr.getRejectReason() == null) { // test si cdr est rejete
					edr.setEventKey(keyEvent);		
    				errorMsg = String.format(errorMessage, "isNewVersionEL", edrVersionRule.getId(), edrVersionRule.getCriteriaEL(), cdr, "%s");
    				var previousEdrs = this.findByEventKey(keyEvent);
    				if(CollectionUtils.isEmpty(previousEdrs)) {
    					edr.setEventVersion(1);
    					continue;
    				}
					var previousEdr = previousEdrs.get(0);
        			boolean isNewVersion = (boolean) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getIsNewVersionEL(),edr, cdr, errorMsg, Boolean.class, previousEdr, edrIterate);    				
        			if(isNewVersion) {
log.info("edrVersionRule HERE isNewVersion TRUE");
        				 // liste des edr versioning 
    					if(previousEdr.getStatus() != EDRStatusEnum.RATED) { // all status : OPEN, CANCELLED, REJECTED
log.info("all status : OPEN, CANCELLED, REJECTED HERE");
        					previousEdr.setStatus(EDRStatusEnum.CANCELLED);
        					previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
        					edr.setEventVersion(previousEdr.getEventVersion() + 1);
    					}else { // for status RATED
log.info("for status RATED HERE");
    						// check if  wallet operation related to EDR is treated
    						var wos = (List<WalletOperation>) walletOperationService.getEntityManager().createQuery("from WalletOperation wo where wo.edr.id=:edrId and  wo.status in ('TREATED', 'TO_RERATE', 'OPEN', 'SCHEDULED' )")
    																	.setParameter("edrId", previousEdr.getId())
    																	.getResultList();
for (WalletOperation wo : wos) {
	log.info("wo HERE {}", wo);
}
    							var billedTransaction = wos.stream().anyMatch(wo -> wo.getRatedTransaction() != null && wo.getRatedTransaction().getStatus() ==  RatedTransactionStatusEnum.BILLED);
    							if(billedTransaction) {
log.info("billedTransaction HERE TRUE");
    								cdr.setStatus(CDRStatusEnum.DISCARDED);
    								cdr.setRejectReason("EDR[id="+previousEdr.getId()+", eventKey="+keyEvent+"] has already been invoiced");
    								if(edr.getId() != null)
    									edrService.remove(edr);
    								edrIterate.remove();
    								continue;
    							}else { // find all wallet operation that have a status OPEN
log.info("billedTransaction HERE FALSE");
									edr.setStatus(EDRStatusEnum.RATED);
									edr.setEventVersion(previousEdr.getEventVersion() + 1);
									previousEdr.setStatus(EDRStatusEnum.CANCELLED);
									previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
									wos.forEach(wo -> {
									    RatingResult rating = usageRatingService.rateUsage(edr, true, false, 0, 0, null, false);
									    if(rating.getWalletOperations().size() == 0 ) {
									        throw new BusinessException("Error while rating new Edr version : "  + edr.getEventVersion());
									    }
									    WalletOperation woToRetate = rating.getWalletOperations().get(0);
										wo.setStatus(WalletOperationStatusEnum.TO_RERATE);
										wo.setEdr(edr);
										wo.setAccountingArticle(woToRetate.getAccountingArticle());
										wo.setAccountingCode(woToRetate.getAccountingCode());
										wo.setAmountTax(woToRetate.getAmountTax());
										wo.setAmountWithoutTax(woToRetate.getAmountWithoutTax());
										wo.setAmountWithTax(woToRetate.getAmountWithTax());
										wo.setBillingAccount(woToRetate.getBillingAccount());
										wo.setChargeInstance(woToRetate.getChargeInstance());
										wo.setChargeMode(woToRetate.getChargeMode());
										wo.setParameter1(woToRetate.getParameter1());
										wo.setParameter2(woToRetate.getParameter2());
										wo.setParameter3(woToRetate.getParameter3());
										wo.setParameterExtra(woToRetate.getParameterExtra());
										wo.setTax(woToRetate.getTax());
										wo.setTaxClass(woToRetate.getTaxClass());
										wo.setTaxPercent(woToRetate.getTaxPercent());
										wo.setUnitAmountTax(woToRetate.getUnitAmountTax());
										wo.setUnitAmountWithTax(woToRetate.getUnitAmountWithTax());
										wo.setUnitAmountWithTax(woToRetate.getUnitAmountWithTax());
										wo.setSubscriptionDate(woToRetate.getSubscriptionDate());
										wo.setInvoiceSubCategory(woToRetate.getInvoiceSubCategory());
										wo.setUserAccount(woToRetate.getUserAccount());
										wo.setType(woToRetate.getType());
										wo.setSubscription(woToRetate.getSubscription());
										wo.setCurrency(woToRetate.getCurrency());
										wo.setCounter(woToRetate.getCounter());
										wo.setDescription(woToRetate.getDescription());
										wo.setInputUnitDescription(woToRetate.getInputUnitDescription());
										wo.setInputUnitOfMeasure(woToRetate.getInputUnitOfMeasure());
										wo.setInputQuantity(woToRetate.getInputQuantity());
										wo.setQuantity(woToRetate.getQuantity());
										walletOperationService.update(wo);
										if(wo.getRatedTransaction() != null) {
											wo.getRatedTransaction().setStatus(RatedTransactionStatusEnum.CANCELED);
											ratedTransactionService.update(wo.getRatedTransaction());
										}
									});
								
								}
    						}
        			}else {
log.info("edrVersionRule HERE isNewVersion FALSE");
						cdr.setStatus(CDRStatusEnum.DISCARDED);
						var msgError = "Newer version already exists EDR[id="+previousEdrs.get(0).getId()+"]";
						cdr.setRejectReason(msgError);
						if(edr.getId() != null)
							edrService.remove(edr);
						edrIterate.remove();
        			}
        		}
        		
        	}
			log.info("----------END each edr HERE {}", edr);
		}
    }
    
    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, EDR previousEdr, Iterator<EDR> edrIterate) {
    	Object evaluted = null;
    	Map<Object, Object> context = new HashMap<>();
    	context.put("edr", edr);
    	if(previousEdr != null)
    		context.put("previous", previousEdr);
    	try {
    		evaluted = ValueExpressionWrapper.evaluateExpression(expression, context, result);
		}catch(Exception e) {
			msg = String.format(msg, e.getMessage());
			cdr.setRejectReason(msg);
			cdr.setStatus(CDRStatusEnum.ERROR);
			if(edr.getId() != null) {
				edrService.remove(edr);
		}
			edrIterate.remove();
		}
    	return evaluted;
    }

    @Inject
    private EdrService edrService;

    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, Iterator<EDR> edrIterate) {
    	return evaluateEdrVersion(idEdrVersion, expression, edr, cdr, msg, result, null, edrIterate);
    }
    
    @SuppressWarnings("unchecked")
	public List<EDR> findByEventKey(String eventKey) {
    	return getEntityManager().createNamedQuery("EDR.findEDREventVersioning").setParameter("eventKey", eventKey).setMaxResults(1).getResultList();
    }
}
