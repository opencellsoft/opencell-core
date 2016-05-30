package org.meveo.admin.action.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.XmlUtil;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;
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

	@Inject
	private Validator validator;
	
	public FilterBean() {
		super(Filter.class);
	}

	@Override
	protected IPersistenceService<Filter> getPersistenceService() {
		return filterService;
	}

	@Override
    @ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		if (entity.getInputXml() != null && !StringUtils.isBlank(entity.getInputXml())) {
			if (!XmlUtil.validate(entity.getInputXml())) {
				messages.error(new BundleKey("messages", "message.filter.invalidXml"));
				return "";
			}

			Filter filter = filterService.parse(entity.getInputXml());
			if (filter != null) {
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
						filterSelectorService.create(filterSelector, getCurrentUser());
						entity.getSecondarySelectors().add(filterSelector);
					}
				}

				// process filterCondition
				if (filter.getFilterCondition() != null) {
					entity.setFilterCondition(filterService.setProviderToFilterCondition(filter.getFilterCondition()));
				}
			}
			
			try {
				validate(entity);
			} catch (ConstraintViolationException e) {
				messages.error(new BundleKey("messages", "message.filter.invalidXml"));
				return "";
			}
		}

		return super.saveOrUpdate(killConversation);
	}

	private void validate(Filter filter) throws ConstraintViolationException {
		if (filter != null) {
			Set<ConstraintViolation<Filter>> violations = validator.validate(filter);
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
			}
		}
		if (filter.getOrderCondition() != null) {
			Set<ConstraintViolation<OrderCondition>> violations = validator.validate(filter.getOrderCondition());
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
			}
		}
		if (filter.getPrimarySelector() != null) {
			Set<ConstraintViolation<FilterSelector>> violations = validator.validate(filter.getPrimarySelector());
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
			}
		}
		if (filter.getSecondarySelectors() != null) {
			for (FilterSelector fs : filter.getSecondarySelectors()) {
				Set<ConstraintViolation<FilterSelector>> violations = validator.validate(fs);
				if (!violations.isEmpty()) {
					throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
				}
			}
		}
		// filterCondition
		if (filter.getFilterCondition() != null) {
			validateFilterCondition(filter.getFilterCondition());
		}
	}

	private void validateFilterCondition(FilterCondition filterCondition) throws ConstraintViolationException {
		if (filterCondition instanceof OrCompositeFilterCondition) {
			OrCompositeFilterCondition tempFilter = (OrCompositeFilterCondition) filterCondition;

			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					validateFilterCondition(fc);
				}
			}
		} else if (filterCondition instanceof AndCompositeFilterCondition) {
			AndCompositeFilterCondition tempFilter = (AndCompositeFilterCondition) filterCondition;

			if (tempFilter.getFilterConditions() != null) {
				for (FilterCondition fc : tempFilter.getFilterConditions()) {
					validateFilterCondition(fc);
				}
			}
		} else if (filterCondition instanceof PrimitiveFilterCondition) {
			PrimitiveFilterCondition tempFilter = (PrimitiveFilterCondition) filterCondition;

			Set<ConstraintViolation<PrimitiveFilterCondition>> violations = validator.validate(tempFilter);
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
			}
		} else if (filterCondition instanceof NativeFilterCondition) {
			NativeFilterCondition tempFilter = (NativeFilterCondition) filterCondition;

			Set<ConstraintViolation<NativeFilterCondition>> violations = validator.validate(tempFilter);
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
			}
		}
	}
}
