package org.meveo.service.mediation;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
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
	

    public void applyEdrVersioningRule(List<EDR> edrs, CDR cdr){
    	var mediationSettings = this.list();
    	if(CollectionUtils.isNotEmpty(mediationSettings) && mediationSettings.size() > 1)
    		throw new BusinessException("More than one Mediation setting is found");
    	if(CollectionUtils.isEmpty(mediationSettings)) return ;
    	if(!mediationSettings.get(0).isEnableEdrVersioning()) return;
    	Comparator<EdrVersioningRule> sortByPriority = (EdrVersioningRule edrV1, EdrVersioningRule edrV2) -> edrV1.getPriority().compareTo(edrV2.getPriority()); 
    	for (EDR edr : edrs) {
    		var errorMessage = "Error evaluating criteriaEL  [id= %d, \"%s\"] for CDR: %s";
        	var edrVersionRuleOption = mediationSettings.get(0).getRules().stream()
					.sorted(sortByPriority)
					.filter(edrVersion -> {
						try {
							return ValueExpressionWrapper.evaluateExpression(edrVersion.getCriteriaEL(), Boolean.class, edr);
						}catch(Exception e) {
							var msg = String.format(errorMessage, edrVersion.getId(), edrVersion.getCriteriaEL(), e.getMessage());
							edr.setRejectReason(msg);
							cdr.setRejectReason(msg);
		        			edr.setStatus(EDRStatusEnum.REJECTED);
		        			cdr.setStatus(CDRStatusEnum.ERROR);
						}
						return false;
					})
					.findFirst();
        	if(edrVersionRuleOption.isPresent()) {
        		var edrVersionRule = edrVersionRuleOption.get();
        		try {
        		String keyEvent = ValueExpressionWrapper.evaluateExpression(edrVersionRule.getKeyEL(), String.class, edr);
				if(StringUtils.isNotEmpty(keyEvent))
					edr.setEventKey(keyEvent);
        		}catch(Exception e) {
        			var msg = String.format(errorMessage, edrVersionRule.getId(), edrVersionRule.getKeyEL(), e.getMessage());
					edr.setRejectReason(msg);
        			cdr.setRejectReason(msg);
        			edr.setStatus(EDRStatusEnum.REJECTED);
        			cdr.setStatus(CDRStatusEnum.ERROR);
				}
        	}
        	
		}
    	
    }
}
