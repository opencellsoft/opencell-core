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
package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.bi.JobHistory;

@Entity
@DiscriminatorValue(value = "CUSTOMER_IMPORT")
public class CustomerImportHisto extends JobHistory {

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "NB_CUSTOMERS")
	private Integer nbCustomers;

	@Column(name = "NB_CUSTOMERS_ERROR")
	private Integer nbCustomersError;

	@Column(name = "NB_CUSTOMERS_WARNING")
	private Integer nbCustomersWarning;

	@Column(name = "NB_CUSTOMERS_IGNORED")
	private Integer nbCustomersIgnored;

	@Column(name = "NB_CUSTOMERS_CREATED")
	private Integer nbCustomersCreated;

	@Column(name = "NB_CUSTOMER_ACCOUNTS")
	private Integer nbCustomerAccounts;

	@Column(name = "NB_CUSTOMER_ACCOUNTS_ERROR")
	private Integer nbCustomerAccountsError;

	@Column(name = "NB_CUSTOMER_ACOUNTS_WARNING")
	private Integer nbCustomerAccountsWarning;

	@Column(name = "NB_CUSTOMER_ACOUNTS_IGNORED")
	private Integer nbCustomerAccountsIgnored;

	@Column(name = "NB_CUSTOMER_ACCOUNTS_CREATED")
	private Integer nbCustomerAccountsCreated;

	public CustomerImportHisto() {

	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the nbCustomers
	 */
	public Integer getNbCustomers() {
		return nbCustomers;
	}

	/**
	 * @param nbCustomers
	 *            the nbCustomers to set
	 */
	public void setNbCustomers(Integer nbCustomers) {
		this.nbCustomers = nbCustomers;
	}

	/**
	 * @return the nbCustomersError
	 */
	public Integer getNbCustomersError() {
		return nbCustomersError;
	}

	/**
	 * @param nbCustomersError
	 *            the nbCustomersError to set
	 */
	public void setNbCustomersError(Integer nbCustomersError) {
		this.nbCustomersError = nbCustomersError;
	}

	/**
	 * @return the nbCustomersWarning
	 */
	public Integer getNbCustomersWarning() {
		return nbCustomersWarning;
	}

	/**
	 * @param nbCustomersWarning
	 *            the nbCustomersWarning to set
	 */
	public void setNbCustomersWarning(Integer nbCustomersWarning) {
		this.nbCustomersWarning = nbCustomersWarning;
	}

	/**
	 * @return the nbCustomersIgnored
	 */
	public Integer getNbCustomersIgnored() {
		return nbCustomersIgnored;
	}

	/**
	 * @param nbCustomersIgnored
	 *            the nbCustomersIgnored to set
	 */
	public void setNbCustomersIgnored(Integer nbCustomersIgnored) {
		this.nbCustomersIgnored = nbCustomersIgnored;
	}

	/**
	 * @return the nbCustomersCreated
	 */
	public Integer getNbCustomersCreated() {
		return nbCustomersCreated;
	}

	/**
	 * @param nbCustomersCreated
	 *            the nbCustomersCreated to set
	 */
	public void setNbCustomersCreated(Integer nbCustomersCreated) {
		this.nbCustomersCreated = nbCustomersCreated;
	}

	/**
	 * @return the nbCustomerAccounts
	 */
	public Integer getNbCustomerAccounts() {
		return nbCustomerAccounts;
	}

	/**
	 * @param nbCustomerAccounts
	 *            the nbCustomerAccounts to set
	 */
	public void setNbCustomerAccounts(Integer nbCustomerAccounts) {
		this.nbCustomerAccounts = nbCustomerAccounts;
	}

	/**
	 * @return the nbCustomerAccountsError
	 */
	public Integer getNbCustomerAccountsError() {
		return nbCustomerAccountsError;
	}

	/**
	 * @param nbCustomerAccountsError
	 *            the nbCustomerAccountsError to set
	 */
	public void setNbCustomerAccountsError(Integer nbCustomerAccountsError) {
		this.nbCustomerAccountsError = nbCustomerAccountsError;
	}

	/**
	 * @return the nbCustomerAccountsWarning
	 */
	public Integer getNbCustomerAccountsWarning() {
		return nbCustomerAccountsWarning;
	}

	/**
	 * @param nbCustomerAccountsWarning
	 *            the nbCustomerAccountsWarning to set
	 */
	public void setNbCustomerAccountsWarning(Integer nbCustomerAccountsWarning) {
		this.nbCustomerAccountsWarning = nbCustomerAccountsWarning;
	}

	/**
	 * @return the nbCustomerAccountsIgnored
	 */
	public Integer getNbCustomerAccountsIgnored() {
		return nbCustomerAccountsIgnored;
	}

	/**
	 * @param nbCustomerAccountsIgnored
	 *            the nbCustomerAccountsIgnored to set
	 */
	public void setNbCustomerAccountsIgnored(Integer nbCustomerAccountsIgnored) {
		this.nbCustomerAccountsIgnored = nbCustomerAccountsIgnored;
	}

	/**
	 * @return the nbCustomerAccountsCreated
	 */
	public Integer getNbCustomerAccountsCreated() {
		return nbCustomerAccountsCreated;
	}

	/**
	 * @param nbCustomerAccountsCreated
	 *            the nbCustomerAccountsCreated to set
	 */
	public void setNbCustomerAccountsCreated(Integer nbCustomerAccountsCreated) {
		this.nbCustomerAccountsCreated = nbCustomerAccountsCreated;
	}
}
