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
