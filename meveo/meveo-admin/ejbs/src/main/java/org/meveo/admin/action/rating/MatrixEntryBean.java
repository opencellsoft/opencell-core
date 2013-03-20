/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.rating;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.rating.MatrixEntry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.rating.impl.MatrixEntryService;
import org.meveo.service.rating.impl.MatrixService;

/**
 * Standard backing bean for {@link MatrixEntry} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Named
@ConversationScoped
public class MatrixEntryBean extends BaseBean<MatrixEntry> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link MatrixEntry} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private MatrixEntryService matrixEntryService;

	@Inject
	private MatrixService matrixService;

	/**
	 * Matrix Definition Id passed as a parameter. Used when creating new Matrix
	 * entry from matrix definition window, so default matrix will be set on
	 * newly created matrix entry.
	 */
	@Inject
	@RequestParam
	private Instance<Long> matrixId;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public MatrixEntryBean() {
		super(MatrixEntry.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("matrixEntry")
	public MatrixEntry init() {
		initEntity();
		if (getMatrixId() != null) {
			entity.setMatrixDefinition(matrixService.findById(getMatrixId()));
		}
		return entity;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @return
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Produces
	@Named("matrixEntries")
	@ConversationScoped
	public PaginationDataModel<MatrixEntry> list() {
		return super.list();
	}

	/**
	 * Override default list view name. (By default view name is class name
	 * starting lower case + ending 's').
	 * 
	 * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
	 */
	protected String getDefaultViewName() {
		return "matrixEntries";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<MatrixEntry> getPersistenceService() {
		return matrixEntryService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	 */
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("matrixDefinition");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("matrixDefinition");
	}

	private Long getMatrixId() {
		return matrixId.get();
	}
}
