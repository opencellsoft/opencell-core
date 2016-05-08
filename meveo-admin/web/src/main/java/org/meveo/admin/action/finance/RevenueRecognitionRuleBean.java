package org.meveo.admin.action.finance;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.payments.RevenueRecognitionRule;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.RevenueRecognitionRuleService;

public class RevenueRecognitionRuleBean extends BaseBean<RevenueRecognitionRule>{

	private static final long serialVersionUID = 1L;

	@Inject
	RevenueRecognitionRuleService revenueRecognitionRuleService;
	
	public RevenueRecognitionRuleBean(){
		super(RevenueRecognitionRule.class);
	}
	
	
	@Override
	protected IPersistenceService<RevenueRecognitionRule> getPersistenceService() {
		return revenueRecognitionRuleService;
	}
	

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}


}
