package org.meveo.service.mediation;

import java.util.Comparator;
import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.model.rating.EDR;
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
}
