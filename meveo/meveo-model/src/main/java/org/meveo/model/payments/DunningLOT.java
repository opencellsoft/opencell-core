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
