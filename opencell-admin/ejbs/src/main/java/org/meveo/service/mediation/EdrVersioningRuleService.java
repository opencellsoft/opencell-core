package org.meveo.service.mediation;

import java.util.ArrayList;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.service.base.PersistenceService;

@Stateless
public class EdrVersioningRuleService extends PersistenceService<EdrVersioningRule>{


	public void checkField(EdrVersioningRule edrV) {
		var fieldNames = new ArrayList<String>();
		if(edrV.getPriority() == null)
			fieldNames.add("priority");
		if(StringUtils.isEmpty(edrV.getCriteriaEL()))
			fieldNames.add("criteriaEL");
		if(StringUtils.isEmpty(edrV.getKeyEL()))
			fieldNames.add("KeyEL");
		if(StringUtils.isEmpty(edrV.getIsNewVersionEL()))
			fieldNames.add("isNewVersionEL");
		if(fieldNames.size() > 0) {
			throw new MissingParameterException("One of theses field is mandatory : " + fieldNames.toString());
		}
			
	}
}
