package org.meveo.admin.action.filter;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.crm.CustomFieldTemplateListBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.XmlUtil;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.*;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.filter.FilterSelectorService;
import org.meveo.service.filter.FilterService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
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

	public void removeParameter(CustomFieldTemplate customField) {
		if(customField != null){
			try {
				final int listIndex = parameters.indexOf(customField);
				final CustomFieldTemplate cfDuplicate = customFieldTemplateService.findByCodeAndAppliesTo(customField.getCode(), customField.getAppliesTo(), getCurrentProvider());
				removeCustomField(customField);
				updateCustomField(cfDuplicate, getFilterCftCodePrefix());
				parameters.remove(listIndex);
				forceUpdateParameters = true;
				messages.info(new BundleKey("messages", "delete.successful"));
			} catch (BusinessException e) {
				log.error("Failed to remove custom field.", e);
				messages.error(new BundleKey("messages", "error.action.failed"), e.getMessage());
			}
		}
	}

	public List<CustomFieldTemplate> getParameters() {
		if (parameters == null || forceUpdateParameters) {
			log.info("Initializing filter parameters.");
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
			} catch (CustomFieldException e) {
				log.error(e.getMessage(), e);
				messages.error(e.getMessage());
			}
		}
		return parameters;
	}

	public CustomFieldTemplate getCustomField() {
		return null;
	}

	public void setCustomField(CustomFieldTemplate customField) {
		if(customField != null){
			try {
				CustomFieldTemplate cfDuplicate = customFieldTemplateService.findByCodeAndAppliesTo(customField.getCode(), customField.getAppliesTo(), getCurrentProvider());
				removeCustomField(customField);
				updateCustomField(cfDuplicate, customFieldTemplateService.calculateAppliesToValue(this.getEntity()));
				parameters.add(cfDuplicate);
				forceUpdateParameters = true;
				messages.info(new BundleKey("messages", "update.successful"));
			} catch (CustomFieldException | BusinessException e) {
				log.error("Failed to add custom field.", e);
				messages.error(new BundleKey("messages", "error.action.failed"), e.getMessage());
			}
		}
	}

	public LazyDataModel<CustomFieldTemplate> getFilterTypeCustomFields(){
		customFieldTemplateListBean.getFilters().put("appliesTo", getFilterCftCodePrefix());
		return  customFieldTemplateListBean.getLazyDataModel();
	}

	private String getFilterCftCodePrefix() {
		if (cftCodePrefix == null) {
			CustomFieldEntity cfeAnnotation = entity.getClass().getAnnotation(CustomFieldEntity.class);
			cftCodePrefix = cfeAnnotation.cftCodePrefix();
		}
		return cftCodePrefix;
	}

	private void updateCustomField(CustomFieldTemplate cfDuplicate, String appliesTo) throws BusinessException {
		cfDuplicate.setAppliesTo(appliesTo);
		customFieldTemplateService.update(cfDuplicate, getCurrentUser());
	}

	private void removeCustomField(CustomFieldTemplate customField) {
		EntityManager entityManager = customFieldTemplateService.getEntityManager();
		if(!entityManager.contains(customField)){
			customField = entityManager.merge(customField);
		}
		customFieldTemplateService.remove(customField);
	}

}
