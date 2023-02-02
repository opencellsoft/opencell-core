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
package org.meveo.admin.action.catalog;

import java.sql.BatchUpdateException;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.Tax;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * Standard backing bean for {@link Tax} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Named
@ViewScoped
public class TaxBean extends CustomFieldBean<Tax> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link Tax} service. Extends {@link PersistenceService}.
     */
    @Inject
    private TaxService taxService;

    private String[] accountingCodeFields = new String[7];
    private String separator;
    
    @PostConstruct
    private void init() {
        ParamBean param = paramBeanFactory.getInstance();
        separator = param.getProperty("reporting.accountingCode.separator", ",");
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TaxBean() {
        super(Tax.class);
        showDeprecatedWarning(DEPRECATED_ADMIN_MESSAGE);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return tax
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
     * @return tax service
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
     * 
     * @return Cost accounting code
     */
    public String generateAccountingCode() {
        return accountingCodeFields[0] + separator + accountingCodeFields[1] + separator + accountingCodeFields[2] + separator + accountingCodeFields[3] + separator
                + accountingCodeFields[4] + separator + accountingCodeFields[5] + separator + accountingCodeFields[6];
    }

	/**
	 * Parses cost accounting code
	 * 
	 */
	public void parseAccountingCode() {
		if (entity.getAccountingCode() != null) {
			String[] accountingCodeValues = entity.getAccountingCode().getCode().split(
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
    protected String getDefaultSort() {
        return "code";
    }

}
