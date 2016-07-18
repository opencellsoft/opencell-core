package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class BundleTemplateBean extends CustomFieldBean<BundleTemplate> {

	private static final long serialVersionUID = -2076286547281668406L;

	@Inject
	protected BundleTemplateService bundleTemplateService;

	public BundleTemplateBean() {
		super(BundleTemplate.class);
	}

	@Override
	protected IPersistenceService<BundleTemplate> getPersistenceService() {
		return bundleTemplateService;
	}

}
