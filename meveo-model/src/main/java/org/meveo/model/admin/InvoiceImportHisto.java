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
package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.bi.JobHistory;

@Entity
@DiscriminatorValue(value = "INVOICE_IMPORT")
public class InvoiceImportHisto extends JobHistory {

	private static final long serialVersionUID = 1L;

	@Column(name = "NB_INVOICES_IGNORED")
	private Integer nbInvoicesIgnored;

	public InvoiceImportHisto() {

	}

	public void setNbInvoicesIgnored(Integer nbInvoicesIgnored) {
		this.nbInvoicesIgnored = nbInvoicesIgnored;
	}

	public Integer getNbInvoicesIgnored() {
		return nbInvoicesIgnored;
	}

}
