/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
@DiscriminatorValue(value = "CUSTOMER_IMPORT")
public class CustomerImportHisto extends JobHistory {

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "NB_SELLERS")
	private Integer nbSellers;

	@Column(name = "NB_SELLERS_ERROR")
	private Integer nbSellersError;

	@Column(name = "NB_SELLERS_WARNING")
	private Integer nbSellersWarning;

	@Column(name = "NB_SELLERS_IGNORED")
	private Integer nbSellersIgnored;

	@Column(name = "NB_SELLERS_CREATED")
	private Integer nbSellersCreated;

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

	public Integer getNbSellers() {
		return nbSellers;
	}

	public void setNbSellers(Integer nbSellers) {
		this.nbSellers = nbSellers;
	}

	public Integer getNbSellersError() {
		return nbSellersError;
	}

	public void setNbSellersError(Integer nbSellersError) {
		this.nbSellersError = nbSellersError;
	}

	public Integer getNbSellersWarning() {
		return nbSellersWarning;
	}

	public void setNbSellersWarning(Integer nbSellersWarning) {
		this.nbSellersWarning = nbSellersWarning;
	}

	public Integer getNbSellersIgnored() {
		return nbSellersIgnored;
	}

	public void setNbSellersIgnored(Integer nbSellersIgnored) {
		this.nbSellersIgnored = nbSellersIgnored;
	}

	public Integer getNbSellersCreated() {
		return nbSellersCreated;
	}

	public void setNbSellersCreated(Integer nbSellersCreated) {
		this.nbSellersCreated = nbSellersCreated;
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
