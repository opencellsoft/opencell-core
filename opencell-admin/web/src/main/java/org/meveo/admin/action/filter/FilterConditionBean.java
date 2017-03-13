package org.meveo.admin.action.filter;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.filter.FilterCondition;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.filter.FilterConditionService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class FilterConditionBean extends BaseBean<FilterCondition> {

	@Inject
	private FilterConditionService filterConditionService;

	private static final long serialVersionUID = 3771540342963246243L;

	public FilterConditionBean() {
		super(FilterCondition.class);
	}

	@Override
	protected IPersistenceService<FilterCondition> getPersistenceService() {
		return filterConditionService;
	}

}
