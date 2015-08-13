package org.meveo.admin.action.filter;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.DiscriminatorValue;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.filter.FilterSelectorService;
import org.meveo.service.filter.FilterService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class FilterBean extends BaseBean<Filter> {

	private static final long serialVersionUID = 6689238784280187702L;

	@Inject
	private FilterService filterService;

	@Inject
	private FilterSelectorService filterSelectorService;

	public FilterBean() {
		super(Filter.class);
	}

	@Override
	protected IPersistenceService<Filter> getPersistenceService() {
		return filterService;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		Filter filter = filterService.parse(entity.getInputXml());

		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(getCurrentUser());

		if (filter.getOrderCondition() != null) {
			filter.getOrderCondition().setProvider(getCurrentProvider());
			entity.setOrderCondition(filter.getOrderCondition());
		}

		if (filter.getPrimarySelector() != null) {
			filter.getPrimarySelector().setProvider(getCurrentProvider());
			entity.setPrimarySelector(filter.getPrimarySelector());
		}

		if (filter.getSecondarySelectors() != null) {
			if (entity.getSecondarySelectors() == null) {
				entity.setSecondarySelectors(new ArrayList<FilterSelector>());
			}
			for (FilterSelector filterSelector : filter.getSecondarySelectors()) {
				filterSelector.setProvider(getCurrentProvider());
				filterSelectorService.create(filterSelector);
				entity.getSecondarySelectors().add(filterSelector);
			}
		}

		// process filterCondition
		if (filter.getFilterCondition() != null) {
			entity.setFilterCondition(setProviderToFilterCondition(filter.getFilterCondition()));
		}

		return super.saveOrUpdate(killConversation);
	}

	private FilterCondition setProviderToFilterCondition(FilterCondition filterCondition) {
		filterCondition.setProvider(getCurrentProvider());

		if (filterCondition.getFilterConditionType().equals(
				AndCompositeFilterCondition.class.getAnnotation(DiscriminatorValue.class).value())) {
			AndCompositeFilterCondition andCompositeFilterCondition = (AndCompositeFilterCondition) filterCondition;
			if (andCompositeFilterCondition.getFilterConditions() != null) {
				for (FilterCondition filterConditionLoop : andCompositeFilterCondition.getFilterConditions()) {
					setProviderToFilterCondition(filterConditionLoop);
				}
			}
		}

		if (filterCondition.getFilterConditionType().equals(
				OrCompositeFilterCondition.class.getAnnotation(DiscriminatorValue.class).value())) {
			OrCompositeFilterCondition orCompositeFilterCondition = (OrCompositeFilterCondition) filterCondition;
			if (orCompositeFilterCondition.getFilterConditions() != null) {
				for (FilterCondition filterConditionLoop : orCompositeFilterCondition.getFilterConditions()) {
					setProviderToFilterCondition(filterConditionLoop);
				}
			}
		}

		return filterCondition;
	}
}
