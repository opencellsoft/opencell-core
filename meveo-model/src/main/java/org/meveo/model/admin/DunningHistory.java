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
