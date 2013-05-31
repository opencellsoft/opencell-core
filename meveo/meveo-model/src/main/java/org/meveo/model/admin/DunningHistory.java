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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.meveo.model.bi.JobHistory;
import org.meveo.model.payments.DunningLOT;

/**
 * Information about batch BAYAD_DUNNING
 */
@Entity
@DiscriminatorValue(value = "DUNNING")
public class DunningHistory extends JobHistory {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "dunningHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DunningLOT> dunningLots;

    public DunningHistory() {
    }

    public void setDunningLots(List<DunningLOT> dunningLots) {
        this.dunningLots = dunningLots;
    }

    public List<DunningLOT> getDunningLots() {
        return dunningLots;
    }
}
