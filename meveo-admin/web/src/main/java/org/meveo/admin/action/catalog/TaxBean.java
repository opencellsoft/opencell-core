/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.Tax;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.TaxService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Tax} (extends {@link BaseBean} that provides
 * almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF
 * components.
 */
@Named
@ViewScoped
public class TaxBean extends BaseBean<Tax> {
	private static final long serialVersionUID = 1L;
	/**
	 * Injected @{link Tax} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private TaxService taxService;

	private String[] accountingCodeFields = new String[7];
	private String separator;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public TaxBean() {
		super(Tax.class);
		ParamBean param = ParamBean.getInstance();
		separator = param
				.getProperty("reporting.accountingCode.separator", ",");
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public Tax initEntity() {
		log.debug("start conversation id: {}", conversation.getId());
		Tax tax = super.initEntity();

		parseAccountingCode();
		return tax;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Tax> getPersistenceService() {
		return taxService;
	}

	public void test() throws BatchUpdateException {
		throw new BatchUpdateException();
	}

	/**
	 * Constructs cost accounting code
	 */
	public String generateAccountingCode() {
		return accountingCodeFields[0] + separator + accountingCodeFields[1]
				+ separator + accountingCodeFields[2] + separator
				+ accountingCodeFields[3] + separator + accountingCodeFields[4]
				+ separator + accountingCodeFields[5] + separator
				+ accountingCodeFields[6];
	}

	/**
	 * Parses cost accounting code
	 * 
	 */
	public void parseAccountingCode() {
		if (entity.getAccountingCode() != null) {
			String[] accountingCodeValues = entity.getAccountingCode().split(
					separator);
			if (accountingCodeValues != null) {
				for (int i = 0; i < accountingCodeFields.length; i++) {
					if (i < accountingCodeValues.length) {
						accountingCodeFields[i] = accountingCodeValues[i];
					}
				}
			}
		}
	}

	public String getAccountingCodeField1() {
		return accountingCodeFields[0];
	}

	public void setAccountingCodeField1(String accountingCodeField1) {
		this.accountingCodeFields[0] = accountingCodeField1;
	}

	public String getAccountingCodeField2() {
		return accountingCodeFields[1];
	}

	public void setAccountingCodeField2(String accountingCodeField2) {
		this.accountingCodeFields[1] = accountingCodeField2;
	}

	public String getAccountingCodeField3() {
		return accountingCodeFields[2];
	}

	public void setAccountingCodeField3(String accountingCodeField3) {
		this.accountingCodeFields[2] = accountingCodeField3;
	}

	public String getAccountingCodeField4() {
		return accountingCodeFields[3];
	}

	public void setAccountingCodeField4(String accountingCodeField4) {
		this.accountingCodeFields[3] = accountingCodeField4;
	}

	public String getAccountingCodeField5() {
		return accountingCodeFields[4];
	}

	public void setAccountingCodeField5(String accountingCodeField5) {
		this.accountingCodeFields[4] = accountingCodeField5;
	}

	public String getAccountingCodeField6() {
		return accountingCodeFields[5];
	}

	public void setAccountingCodeField6(String accountingCodeField6) {
		this.accountingCodeFields[5] = accountingCodeField6;
	}

	public String getAccountingCodeField7() {
		return accountingCodeFields[6];
	}

	public void setAccountingCodeField7(String accountingCodeField7) {
		this.accountingCodeFields[6] = accountingCodeField7;
	}

	@Override
	protected String getListViewName() {
		return "taxes";
	}

	/**
	 * Fetch customer field so no LazyInitialize exception is thrown when we
	 * access it from account edit view.
	 * 
	 * @see org.manaty.beans.base.BaseBean#getFormFieldsToFetch()
	 */
	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

}
