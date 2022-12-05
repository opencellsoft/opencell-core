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
@DiscriminatorValue(value = "SUBSCRIPTION_IMPORT")
public class SubscriptionImportHisto extends JobHistory {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name", length = 255)
    @Size(max = 255)
    private String fileName;

    @Column(name = "nb_subscriptions")
    private Integer nbSubscriptions;

    @Column(name = "nb_subscriptions_error")
    private Integer nbSubscriptionsError;

    @Column(name = "nb_subscriptions_ignored")
    private Integer nbSubscriptionsIgnored;

    @Column(name = "nb_subscriptions_created")
    private Integer nbSubscriptionsCreated;

    @Column(name = "nb_subscriptions_terminated")
    private Integer nbSubscriptionsTerminated;

    public SubscriptionImportHisto() {

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
     * @return the nbSubscriptions
     */
    public Integer getNbSubscriptions() {
        return nbSubscriptions;
    }

    /**
     * @param nbSubscriptions the nbSubscriptions to set
     */
    public void setNbSubscriptions(Integer nbSubscriptions) {
        this.nbSubscriptions = nbSubscriptions;
    }

    /**
     * @return the nbSubscriptionsError
     */
    public Integer getNbSubscriptionsError() {
        return nbSubscriptionsError;
    }

    /**
     * @param nbSubscriptionsError the nbSubscriptionsError to set
     */
    public void setNbSubscriptionsError(Integer nbSubscriptionsError) {
        this.nbSubscriptionsError = nbSubscriptionsError;
    }

    /**
     * @return the nbSubscriptionsIgnored
     */
    public Integer getNbSubscriptionsIgnored() {
        return nbSubscriptionsIgnored;
    }

    /**
     * @param nbSubscriptionsIgnored the nbSubscriptionsIgnored to set
     */
    public void setNbSubscriptionsIgnored(Integer nbSubscriptionsIgnored) {
        this.nbSubscriptionsIgnored = nbSubscriptionsIgnored;
    }

    /**
     * @return the nbSubscriptionsCreated
     */
    public Integer getNbSubscriptionsCreated() {
        return nbSubscriptionsCreated;
    }

    /**
     * @param nbSubscriptionsCreated the nbSubscriptionsCreated to set
     */
    public void setNbSubscriptionsCreated(Integer nbSubscriptionsCreated) {
        this.nbSubscriptionsCreated = nbSubscriptionsCreated;
    }

    /**
     * @return the nbSubscriptionsTerminated
     */
    public Integer getNbSubscriptionsTerminated() {
        return nbSubscriptionsTerminated;
    }

    /**
     * @param nbSubscriptionsTerminated the nbSubscriptionsTerminated to set
     */
    public void setNbSubscriptionsTerminated(Integer nbSubscriptionsTerminated) {
        this.nbSubscriptionsTerminated = nbSubscriptionsTerminated;
    }

}
