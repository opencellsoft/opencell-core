package org.meveo.admin.action.filter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.FastHashMap;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.filter.FilterConditionService;
import org.meveo.service.filter.FilterService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class FilterBean extends BaseBean<Filter> {

	private static final long serialVersionUID = 6689238784280187702L;

	private FilterCondition filterCondition = new FilterCondition();

	@Inject
	private FilterService filterService;

	@Inject
	private FilterConditionService filterConditionService;

	private List<FilterCondition> filterConditions;

	public FilterBean() {
		super(Filter.class);
	}

	@Override
	protected IPersistenceService<Filter> getPersistenceService() {
		return filterService;
	}

	public FilterCondition getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(FilterCondition filterCondition) {
		this.filterCondition = filterCondition;
	}

	public void newFilterCondition() {
		filterCondition = new FilterCondition();
	}

	public void saveFilterCondition() {
		if (filterCondition != null) {
			try {
				if (filterCondition.isTransient()) {
					// save
					filterCondition.setFilter(entity);
					filterConditionService.create(filterCondition);
					entity.getFilterConditions().put(filterCondition.getOperand(), filterCondition);
					messages.info(new BundleKey("messages", "save.successful"));
				} else {
					// update
					filterConditionService.update(filterCondition);
					messages.info(new BundleKey("messages", "update.successful"));
				}
			} catch (BusinessException e) {
				log.error("exception when saving filter condition!", e);
			}
		}

		filterCondition = new FilterCondition();
		updateFilterConditions();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updateFilterConditions() {
		if (entity.getFilterConditions() != null) {
			FastHashMap filterConditionMap = new FastHashMap(entity.getFilterConditions());
			filterConditions = new ArrayList(filterConditionMap.values());
		}
	}

	public List<FilterCondition> getFilterConditions() {
		return filterConditions;
	}

	public void deleteFilterCondition(FilterCondition e) {
		filterConditionService.remove(e);
		entity.getFilterConditions().remove(e);
		updateFilterConditions();

		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void editFilterCondition(FilterCondition e) {
		this.filterCondition = e;
	}
}
