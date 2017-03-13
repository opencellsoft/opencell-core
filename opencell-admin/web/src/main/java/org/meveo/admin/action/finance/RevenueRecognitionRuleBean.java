package org.meveo.admin.action.finance;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.RevenueRecognitionRuleService;

@Named
@ViewScoped
public class RevenueRecognitionRuleBean extends BaseBean<RevenueRecognitionRule> {

	private static final long serialVersionUID = 1L;

	@Inject
	RevenueRecognitionRuleService revenueRecognitionRuleService;

	public RevenueRecognitionRuleBean() {
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
}