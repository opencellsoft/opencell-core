package org.meveo.admin.action.filter;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.crm.CustomFieldTemplateListBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.XmlUtil;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.*;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.filter.FilterSelectorService;
import org.meveo.service.filter.FilterService;
import org.omnifaces.cdi.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

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

	@Inject
	private CustomFieldTemplateListBean customFieldTemplateListBean;

	private List<CustomFieldTemplate> parameters;

	private boolean forceUpdateParameters;

	private String cftCodePrefix;

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
			filterService.validateUnmarshalledFilter(filter);
			filterService.updateFilterDetails(filter, this.entity, getCurrentUser());

			try {
				validate(entity);
			} catch (ConstraintViolationException e) {
				messages.error(new BundleKey("messages", "message.filter.invalidXml"));
				return "";
			}
		}
		filterService.persistCustomFieldTemplates(this.entity, getCurrentUser());
		forceUpdateParameters = true;
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

	public List<CustomFieldTemplate> getParameters() {
		if (parameters == null || forceUpdateParameters) {
			log.trace("Initializing filter parameters.");
			forceUpdateParameters = false;
			parameters = new ArrayList<>();
			try {
				if(this.getEntity() != null){
					String appliesTo = customFieldTemplateService.calculateAppliesToValue(this.getEntity());
					Map<String, CustomFieldTemplate> customFieldTemplateMap = customFieldTemplateService.findByAppliesTo(appliesTo, currentUser.getProvider());
					for (Map.Entry<String, CustomFieldTemplate> customFieldTemplateEntry : customFieldTemplateMap.entrySet()) {
						parameters.add(customFieldTemplateEntry.getValue());
					}
				}
				log.trace("Filter parameters initialized.");
			} catch (CustomFieldException e) {
				log.error(e.getMessage(), e);
				messages.error(e.getMessage());
			}
		}
		return parameters;
	}

}
