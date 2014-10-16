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
package org.meveo.model.payments;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.DunningHistory;

@Entity
@Table(name = "AR_DUNNING_LOT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_DUNNING_LOT_SEQ")
public class DunningLOT extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE")
	private DunningActionTypeEnum actionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DUNNING_HISTORY_ID")
	private DunningHistory dunningHistory;

	@OneToMany(mappedBy = "dunningLOT", fetch = FetchType.LAZY)
	private List<ActionDunning> actions = new ArrayList<ActionDunning>();

	public DunningLOT() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DunningActionTypeEnum getActionType() {
		return actionType;
	}

	public void setActionType(DunningActionTypeEnum actionType) {
		this.actionType = actionType;
	}

	public List<ActionDunning> getActions() {
		return actions;
	}

	public void setActions(List<ActionDunning> actions) {
		this.actions = actions;
	}

	public void setDunningHistory(DunningHistory dunningHistory) {
		this.dunningHistory = dunningHistory;
	}

	public DunningHistory getDunningHistory() {
		return dunningHistory;
	}

}
