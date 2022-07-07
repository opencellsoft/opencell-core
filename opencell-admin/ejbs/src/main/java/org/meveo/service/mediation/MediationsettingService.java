package org.meveo.service.mediation;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
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
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MediationsettingService extends PersistenceService<MediationSetting>{

	private Logger log = LoggerFactory.getLogger(MediationsettingService.class);
	
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
						return ValueExpressionWrapper.evaluateExpression(edrVersion.getCriterialEL(), Boolean.class, edr);
					}catch(Exception e) {
						log.warn("cant evaluate expression : " + edrVersion.getCriterialEL() , e);
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
	
	@Inject
	private WalletOperationService walletOperationService;
	
    @SuppressWarnings("unchecked")
	public void applyEdrVersioningRule(List<EDR> edrs, CDR cdr){
    	var mediationSettings = this.list();
    	if(CollectionUtils.isNotEmpty(mediationSettings) && mediationSettings.size() > 1)
    		throw new BusinessException("More than one Mediation setting is found");
    	if(CollectionUtils.isEmpty(mediationSettings)) return ;
    	if(!mediationSettings.get(0).isEnableEdrVersioning()) return;
    	Comparator<EdrVersioningRule> sortByPriority = (EdrVersioningRule edrV1, EdrVersioningRule edrV2) -> edrV1.getPriority().compareTo(edrV2.getPriority()); 
    	for (EDR edr : edrs) {
    		var  errorMessage = "Error evaluating %s  [id= %d, \"%s\"] for CDR: [%s] : %s";
        	var edrVersionRuleOption = mediationSettings.get(0).getRules().stream()
					.sorted(sortByPriority)
					.filter(edrVersion -> {
						var errorMsg = String.format(errorMessage, "criteriaEL", edrVersion.getId(), edrVersion.getCriterialEL(), cdr, "%s");
						var eval = (Boolean) evaluateEdrVersion(edrVersion.getId(), edrVersion.getCriterialEL(),edr, cdr, errorMsg, Boolean.class); 
						return eval == null ? false : eval;
					})
					.findFirst();
        	if(edrVersionRuleOption.isPresent()) {
        		var edrVersionRule = edrVersionRuleOption.get();
				var errorMsg = String.format(errorMessage, "eventKeyEl", edrVersionRule.getId(), edrVersionRule.getCriterialEL(), cdr, "%s");
        		String keyEvent =  (String) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getKeyEL(),edr, cdr, errorMsg , String.class);
        		if(StringUtils.isNotEmpty(keyEvent) && edr.getRejectReason() == null) { // test si cdr est rejete
					edr.setEventKey(keyEvent);		
    				errorMsg = String.format(errorMessage, "isNewVersionEL", edrVersionRule.getId(), edrVersionRule.getCriterialEL(), cdr, "%s");
    				var previousEdrs = this.findByEventKey(keyEvent);
    				if(CollectionUtils.isEmpty(previousEdrs)) {
    					edr.setEventVersion(1);
    					continue;
    				}
					var previousEdr = previousEdrs.get(0);
					boolean tmpTestComparaison = edr.getDecimalParam1().intValue() > previousEdr.getDecimalParam1().intValue();
        			boolean isNewVersion = (boolean) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getIsNewVersionEL(),edr, cdr, errorMsg, Boolean.class, previousEdr);    				
        			if(isNewVersion) {
        				 // liste des edr versioning 
    					if(previousEdr.getStatus() != EDRStatusEnum.RATED) { // all status : OPEN, CANCELLED, REJECTED
        					previousEdr.setStatus(EDRStatusEnum.CANCELLED);
        					previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
        					edr.setEventVersion(previousEdr.getEventVersion() + 1);
    					}else { // for status RATED
    						// check if  wallet operation related to EDR is treated
    						var wos = (List<WalletOperation>) walletOperationService.getEntityManager().createQuery("from WalletOperation wo where wo.edr.id=:edrId and  wo.status in ('TREATED', 'TO_RERATE', 'OPEN', 'SCHEDULED' )")
    																	.setParameter("edrId", previousEdr.getId())
    																	.getResultList();
    						if(CollectionUtils.isNotEmpty(wos)) { // wo already treated. find all rated
    							var billedTransaction = wos.stream().anyMatch(wo -> wo.getRatedTransaction() != null && wo.getRatedTransaction().getStatus() ==  RatedTransactionStatusEnum.BILLED);
    							if(billedTransaction) {
    								edr.setStatus(EDRStatusEnum.REJECTED);
    								cdr.setStatus(CDRStatusEnum.DISCARDED);
    								var msgError = "EDR[id="+previousEdr.getId()+", eventKey="+keyEvent+"] has already been invoiced";
    								edr.setRejectReason(msgError);
    								cdr.setRejectReason(msgError);
    								continue;
    							}else { // find all wallet operation that have a status OPEN 
									edr.setStatus(EDRStatusEnum.RATED);
									edr.setEventVersion(previousEdr.getEventVersion() + 1);
									previousEdr.setStatus(EDRStatusEnum.CANCELLED);
									previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
									wos.forEach(wo -> {
										wo.setStatus(WalletOperationStatusEnum.TO_RERATE);
										wo.setEdr(edr);
										walletOperationService.update(wo);
									});
								
								}
    						}
    					}
        			}else {
						cdr.setStatus(CDRStatusEnum.DISCARDED);
						edr.setStatus(EDRStatusEnum.REJECTED);
						var msgError = "Newer version already exists EDR[id="+previousEdrs.get(0).getId()+"]";
						edr.setRejectReason(msgError);
						cdr.setRejectReason(msgError);
						edr.setEventVersion(null);
        			}
        		}
        		
        	}
        	
		}
    	
    }
    
    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, EDR previousEdr) {
    	Object evaluted = null;
    	Map<Object, Object> context = new HashMap<>();
    	context.put("edr", edr);
    	if(previousEdr != null)
    		context.put("previous", previousEdr);
    	try {
    		evaluted = ValueExpressionWrapper.evaluateExpression(expression, context, result);
		}catch(Exception e) {
			msg = String.format(msg, e.getMessage());
			edr.setRejectReason(msg);
			cdr.setRejectReason(msg);
			edr.setStatus(EDRStatusEnum.REJECTED);
			cdr.setStatus(CDRStatusEnum.ERROR);
		}
    	return evaluted;
    }

    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result) {
    	return evaluateEdrVersion(idEdrVersion, expression, edr, cdr, msg, result, null);
    }
    
    @SuppressWarnings("unchecked")
	public List<EDR> findByEventKey(String eventKey) {
    	return getEntityManager().createNamedQuery("EDR.findEDREventVersioning").setParameter("eventKey", eventKey).setMaxResults(1).getResultList();
    }
}
