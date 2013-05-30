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
