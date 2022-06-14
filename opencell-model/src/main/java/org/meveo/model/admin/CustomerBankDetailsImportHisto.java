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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.meveo.model.bi.JobHistory;

@Entity
@DiscriminatorValue(value = "CUSTOMER_BANK_DETAILS_IMPORT")
public class CustomerBankDetailsImportHisto extends JobHistory {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", length = 255)
    @Size(max = 255)
    private String fileName;
    
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

    public CustomerBankDetailsImportHisto() {

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
