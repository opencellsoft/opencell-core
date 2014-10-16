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
@DiscriminatorValue(value = "SUBSCRIPTION_IMPORT")
public class SubscriptionImportHisto extends JobHistory {

    private static final long serialVersionUID = 1L;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "NB_SUBSCRIPTIONS")
    private Integer nbSubscriptions;

    @Column(name = "NB_SUBSCRIPTIONS_ERROR")
    private Integer nbSubscriptionsError;

    @Column(name = "NB_SUBSCRIPTIONS_IGNORED")
    private Integer nbSubscriptionsIgnored;

    @Column(name = "NB_SUBSCRIPTIONS_CREATED")
    private Integer nbSubscriptionsCreated;

    @Column(name = "NB_SUBSCRIPTIONS_TERMINATED")
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
     * @param fileName
     *            the fileName to set
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
     * @param nbSubscriptions
     *            the nbSubscriptions to set
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
     * @param nbSubscriptionsError
     *            the nbSubscriptionsError to set
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
     * @param nbSubscriptionsIgnored
     *            the nbSubscriptionsIgnored to set
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
     * @param nbSubscriptionsCreated
     *            the nbSubscriptionsCreated to set
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
     * @param nbSubscriptionsTerminated
     *            the nbSubscriptionsTerminated to set
     */
    public void setNbSubscriptionsTerminated(Integer nbSubscriptionsTerminated) {
        this.nbSubscriptionsTerminated = nbSubscriptionsTerminated;
    }

}
