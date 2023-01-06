/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.payments;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.OCCTemplateService;

/**
 * Standard backing bean for {@link OCCTemplate} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class OccTemplateBean extends BaseBean<OCCTemplate> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link OCCTemplate} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private OCCTemplateService occTemplateService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public OccTemplateBean() {
		super(OCCTemplate.class);
		showDeprecatedWarning(DEPRECATED_ADMIN_MESSAGE);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * @return account operation template
	 * 
	 */
	@Produces
	@Named("occTemplate")
	public OCCTemplate init() {
		return initEntity();
	}
	
	@Override
	public String getNewViewName() {
		return "occTemplateDetail";
	}

	@Override
	protected String getListViewName() {
		return "occTemplates";
	}

	@Override
	public String getEditViewName() {
		return "occTemplateDetail";
	}

	public List<OCCTemplate> listOCCTemplate() {
		return (List<OCCTemplate>) occTemplateService
				.getListOccSortedByName();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<OCCTemplate> getPersistenceService() {
		return occTemplateService;
	}

}
