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
package org.meveo.model.admin;

import org.meveo.model.bi.JobHistory;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue(value = "ACCOUNT_IMPORT")
public class AccountImportHisto extends JobHistory {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", length = 255)
    @Size(max = 255)
    private String fileName;

    @Column(name = "nb_billing_accounts")
    private Integer nbBillingAccounts;

    @Column(name = "nb_billing_accounts_error")
    private Integer nbBillingAccountsError;

    @Column(name = "nb_billing_accounts_warning")
    private Integer nbBillingAccountsWarning;

    @Column(name = "nb_billing_accounts_ignored")
    private Integer nbBillingAccountsIgnored;

    @Column(name = "nb_billing_accounts_created")
    private Integer nbBillingAccountsCreated;

    @Column(name = "nb_user_accounts")
    private Integer nbUserAccounts;

    @Column(name = "nb_user_accounts_error")
    private Integer nbUserAccountsError;

    @Column(name = "nb_user_acounts_warning")
    private Integer nbUserAccountsWarning;

    @Column(name = "nb_user_acounts_ignored")
    private Integer nbUserAccountsIgnored;

    @Column(name = "nb_user_accounts_created")
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
     * @param fileName the fileName to set
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
     * @param nbBillingAccounts the nbBillingAccounts to set
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
     * @param nbBillingAccountsError the nbBillingAccountsError to set
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
     * @param nbBillingAccountsWarning the nbBillingAccountsWarning to set
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
     * @param nbBillingAccountsIgnored the nbBillingAccountsIgnored to set
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
     * @param nbBillingAccountsCreated the nbBillingAccountsCreated to set
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
     * @param nbUserAccounts the nbUserAccounts to set
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
     * @param nbUserAccountsError the nbUserAccountsError to set
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
     * @param nbUserAccountsWarning the nbUserAccountsWarning to set
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
     * @param nbUserAccountsIgnored the nbUserAccountsIgnored to set
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
     * @param nbUserAccountsCreated the nbUserAccountsCreated to set
     */
    public void setNbUserAccountsCreated(Integer nbUserAccountsCreated) {
        this.nbUserAccountsCreated = nbUserAccountsCreated;
    }

}
