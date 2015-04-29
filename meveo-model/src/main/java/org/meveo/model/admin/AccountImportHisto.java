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
@DiscriminatorValue(value = "ACCOUNT_IMPORT")
public class AccountImportHisto extends JobHistory {

    private static final long serialVersionUID = 1L;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "NB_BILLING_ACCOUNTS")
    private Integer nbBillingAccounts;

    @Column(name = "NB_BILLING_ACCOUNTS_ERROR")
    private Integer nbBillingAccountsError;

    @Column(name = "NB_BILLING_ACCOUNTS_WARNING")
    private Integer nbBillingAccountsWarning;

    @Column(name = "NB_BILLING_ACCOUNTS_IGNORED")
    private Integer nbBillingAccountsIgnored;

    @Column(name = "NB_BILLING_ACCOUNTS_CREATED")
    private Integer nbBillingAccountsCreated;

    @Column(name = "NB_USER_ACCOUNTS")
    private Integer nbUserAccounts;

    @Column(name = "NB_USER_ACCOUNTS_ERROR")
    private Integer nbUserAccountsError;

    @Column(name = "NB_USER_ACOUNTS_WARNING")
    private Integer nbUserAccountsWarning;

    @Column(name = "NB_USER_ACOUNTS_IGNORED")
    private Integer nbUserAccountsIgnored;

    @Column(name = "NB_USER_ACCOUNTS_CREATED")
    private Integer nbUserAccountsCreated;

    public AccountImportHisto() {
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
     * @return the nbBillingAccounts
     */
    public Integer getNbBillingAccounts() {
        return nbBillingAccounts;
    }

    /**
     * @param nbBillingAccounts
     *            the nbBillingAccounts to set
     */
    public void setNbBillingAccounts(Integer nbBillingAccounts) {
        this.nbBillingAccounts = nbBillingAccounts;
    }

    /**
     * @return the nbBillingAccountsError
     */
    public Integer getNbBillingAccountsError() {
        return nbBillingAccountsError;
    }

    /**
     * @param nbBillingAccountsError
     *            the nbBillingAccountsError to set
     */
    public void setNbBillingAccountsError(Integer nbBillingAccountsError) {
        this.nbBillingAccountsError = nbBillingAccountsError;
    }

    /**
     * @return the nbBillingAccountsWarning
     */
    public Integer getNbBillingAccountsWarning() {
        return nbBillingAccountsWarning;
    }

    /**
     * @param nbBillingAccountsWarning
     *            the nbBillingAccountsWarning to set
     */
    public void setNbBillingAccountsWarning(Integer nbBillingAccountsWarning) {
        this.nbBillingAccountsWarning = nbBillingAccountsWarning;
    }

    /**
     * @return the nbBillingAccountsIgnored
     */
    public Integer getNbBillingAccountsIgnored() {
        return nbBillingAccountsIgnored;
    }

    /**
     * @param nbBillingAccountsIgnored
     *            the nbBillingAccountsIgnored to set
     */
    public void setNbBillingAccountsIgnored(Integer nbBillingAccountsIgnored) {
        this.nbBillingAccountsIgnored = nbBillingAccountsIgnored;
    }

    /**
     * @return the nbBillingAccountsCreated
     */
    public Integer getNbBillingAccountsCreated() {
        return nbBillingAccountsCreated;
    }

    /**
     * @param nbBillingAccountsCreated
     *            the nbBillingAccountsCreated to set
     */
    public void setNbBillingAccountsCreated(Integer nbBillingAccountsCreated) {
        this.nbBillingAccountsCreated = nbBillingAccountsCreated;
    }

    /**
     * @return the nbUserAccounts
     */
    public Integer getNbUserAccounts() {
        return nbUserAccounts;
    }

    /**
     * @param nbUserAccounts
     *            the nbUserAccounts to set
     */
    public void setNbUserAccounts(Integer nbUserAccounts) {
        this.nbUserAccounts = nbUserAccounts;
    }

    /**
     * @return the nbUserAccountsError
     */
    public Integer getNbUserAccountsError() {
        return nbUserAccountsError;
    }

    /**
     * @param nbUserAccountsError
     *            the nbUserAccountsError to set
     */
    public void setNbUserAccountsError(Integer nbUserAccountsError) {
        this.nbUserAccountsError = nbUserAccountsError;
    }

    /**
     * @return the nbUserAccountsWarning
     */
    public Integer getNbUserAccountsWarning() {
        return nbUserAccountsWarning;
    }

    /**
     * @param nbUserAccountsWarning
     *            the nbUserAccountsWarning to set
     */
    public void setNbUserAccountsWarning(Integer nbUserAccountsWarning) {
        this.nbUserAccountsWarning = nbUserAccountsWarning;
    }

    /**
     * @return the nbUserAccountsIgnored
     */
    public Integer getNbUserAccountsIgnored() {
        return nbUserAccountsIgnored;
    }

    /**
     * @param nbUserAccountsIgnored
     *            the nbUserAccountsIgnored to set
     */
    public void setNbUserAccountsIgnored(Integer nbUserAccountsIgnored) {
        this.nbUserAccountsIgnored = nbUserAccountsIgnored;
    }

    /**
     * @return the nbUserAccountsCreated
     */
    public Integer getNbUserAccountsCreated() {
        return nbUserAccountsCreated;
    }

    /**
     * @param nbUserAccountsCreated
     *            the nbUserAccountsCreated to set
     */
    public void setNbUserAccountsCreated(Integer nbUserAccountsCreated) {
        this.nbUserAccountsCreated = nbUserAccountsCreated;
    }

}
