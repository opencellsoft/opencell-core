/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.wf;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

@Entity
@ModuleItem
@ExportIdentifier({ "code", "provider" })
@Table(name = "WF_WORKFLOW", uniqueConstraints = @UniqueConstraint(columnNames = {"PROVIDER_ID", "CODE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "WF_WORKFLOW_SEQ")
public class Workflow extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "WF_TYPE")
	@NotNull
    @Size(max = 255)
	String wfType = null;
	
	@OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    @OrderBy("priority ASC")
	private List<WFTransition> transitions = new ArrayList<WFTransition>();
	
	
	@Column(name = "ENABLE_HOSTORY")
	boolean enableHistory;

	/**
	 * @return the wfType
	 */
	public String getWfType() {
		return wfType;
	}

	/**
	 * @param wfType the wfType to set
	 */
	public void setWfType(String wfType) {
		this.wfType = wfType;
	}

	/**
	 * @return the transitions
	 */
	public List<WFTransition> getTransitions() {
		return transitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	public void setTransitions(List<WFTransition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * @return the enbaleHistory
	 */
	public boolean isEnableHistory() {
		return enableHistory;
	}

	/**
	 * @param enbaleHistory the enbaleHistory to set
	 */
	public void setEnableHistory(boolean enbaleHistory) {
		this.enableHistory = enbaleHistory;
	}
		
	@Override
	public String toString() {
		return "Workflow [code=" + code + ", description=" + description + "]";
	}

}
