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
package org.meveo.model.scripts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "MEVEO_SCRIPT_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE","PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_SCRIPT_INSTANCE_SEQ")
@NamedQueries({			
@NamedQuery(name = "ScriptInstance.countScriptInstanceOnError", 
	           query = "select count (*) from ScriptInstance o where o.error=:isError and o.provider=:provider"),
	           
@NamedQuery(name = "ScriptInstance.getScriptInstanceOnError", 
	           query = "from ScriptInstance o where o.error=:isError and o.provider=:provider")
	})
public class ScriptInstance extends BusinessEntity  {

	private static final long serialVersionUID = -5517252645289726288L;

	@Column(name = "SCRIPT", nullable = false, length = 10000)
	@Size(max = 10000)
	private String script;

	@Enumerated(EnumType.STRING)
	@Column(name = "SRC_TYPE")
	ScriptTypeEnum scriptTypeEnum = ScriptTypeEnum.JAVA;
	
	@OneToMany(mappedBy = "scriptInstance", orphanRemoval=true,fetch = FetchType.EAGER)
	private List<ScriptInstanceError> scriptInstanceErrors = new ArrayList<ScriptInstanceError>();

	
	@Column(name = "IS_ERROR")
	private Boolean error = false;
	
	public ScriptInstance(){

	}

	/**
	 * @return the scriptTypeEnum
	 */
	public ScriptTypeEnum getScriptTypeEnum() {
		return scriptTypeEnum;
	}


	/**
	 * @param scriptTypeEnum the scriptTypeEnum to set
	 */
	public void setScriptTypeEnum(ScriptTypeEnum scriptTypeEnum) {
		this.scriptTypeEnum = scriptTypeEnum;
	}



	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}



	/**
	 * @param script the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * @return the scriptInstanceErrors
	 */
	public List<ScriptInstanceError> getScriptInstanceErrors() {
		return scriptInstanceErrors;
	}

	/**
	 * @param scriptInstanceErrors the scriptInstanceErrors to set
	 */
	public void setScriptInstanceErrors(List<ScriptInstanceError> scriptInstanceErrors) {
		this.scriptInstanceErrors = scriptInstanceErrors;
	}

	/**
	 * @return the error
	 */
	public Boolean getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Boolean error) {
		this.error = error;
	}




}