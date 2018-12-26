/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.meveo.model.bi.JobHistory;

@Entity
@DiscriminatorValue(value = "CUSTOMER_IMPORT")
public class CustomerImportHisto extends JobHistory {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", length = 255)
    @Size(max = 255)
    private String fileName;

    @Column(name = "nb_sellers")
    private Integer nbSellers;

    @Column(name = "nb_sellers_error")
    private Integer nbSellersError;

    @Column(name = "nb_sellers_warning")
    private Integer nbSellersWarning;

    @Column(name = "nb_sellers_ignored")
    private Integer nbSellersIgnored;

    @Column(name = "nb_sellers_created")
    private Integer nbSellersCreated;

    @Column(name = "nb_customers")
    private Integer nbCustomers;

    @Column(name = "nb_customers_error")
    private Integer nbCustomersError;

    @Column(name = "nb_customers_warning")
    private Integer nbCustomersWarning;

    @Column(name = "nb_customers_ignored")
    private Integer nbCustomersIgnored;

    @Column(name = "nb_customers_created")
    private Integer nbCustomersCreated;

    @Column(name = "nb_customer_accounts")
    private Integer nbCustomerAccounts;

    @Column(name = "nb_customer_accounts_error")
    private Integer nbCustomerAccountsError;

    @Column(name = "nb_customer_acounts_warning")
    private Integer nbCustomerAccountsWarning;

    @Column(name = "nb_customer_acounts_ignored")
    private Integer nbCustomerAccountsIgnored;

    @Column(name = "nb_customer_accounts_created")
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
     * @param fileName the fileName to set
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
     * @param nbCustomers the nbCustomers to set
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
     * @param nbCustomersError the nbCustomersError to set
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
     * @param nbCustomersWarning the nbCustomersWarning to set
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
     * @param nbCustomersIgnored the nbCustomersIgnored to set
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
     * @param nbCustomersCreated the nbCustomersCreated to set
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
     * @param nbCustomerAccounts the nbCustomerAccounts to set
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
     * @param nbCustomerAccountsError the nbCustomerAccountsError to set
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
     * @param nbCustomerAccountsWarning the nbCustomerAccountsWarning to set
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
     * @param nbCustomerAccountsIgnored the nbCustomerAccountsIgnored to set
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
     * @param nbCustomerAccountsCreated the nbCustomerAccountsCreated to set
     */
    public void setNbCustomerAccountsCreated(Integer nbCustomerAccountsCreated) {
        this.nbCustomerAccountsCreated = nbCustomerAccountsCreated;
    }
}
