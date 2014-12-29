package org.meveo.admin.action.crm;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Named
@ConversationScoped
public class CustomFieldTemplateBean extends StatelessBaseBean<CustomFieldTemplate> {

	private static final long serialVersionUID = 9099292371182275568L;

	@Inject
	private CustomFieldTemplateService cftService;

	public CustomFieldTemplateBean() {
		super(CustomFieldTemplate.class);
	}

	@Override
	protected IPersistenceService<CustomFieldTemplate> getPersistenceService() {
		return cftService;
	}

	@Override
	protected String getListViewName() {
		return "customFieldTemplates";
	}

	@Override
	public String getNewViewName() {
		return "customFieldTemplateDetail";
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
