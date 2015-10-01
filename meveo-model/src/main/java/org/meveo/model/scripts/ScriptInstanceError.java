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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "id" })
@Table(name = "MEVEO_SCRIPT_INSTANCE_ERROR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_SCRIPT_INSTANCE_ERROR_SEQ")
public class ScriptInstanceError extends AuditableEntity  {

	private static final long serialVersionUID = -5517252645289726288L;
	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "LINE_NUMBER")
	private long lineNumber;
	
	@Column(name = "COLUMN_NUMBER")
	private long columnNumber;
	
	@Column(name = "SOURCE_FILE")
	private String sourceFile;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "SCRIPT_INSTANCE_ID")
	private  ScriptInstance scriptInstance;
	
	public ScriptInstanceError(){
		
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the columnNUmber
	 */
	public long getColumnNumber() {
		return columnNumber;
	}

	/**
	 * @param columnNUmber the columnNUmber to set
	 */
	public void setColumnNumber(long columnNUmber) {
		this.columnNumber = columnNUmber;
	}

	/**
	 * @return the sourceFile
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile the sourceFile to set
	 */
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the scriptInstance
	 */
	public ScriptInstance getScriptInstance() {
		return scriptInstance;
	}

	/**
	 * @param scriptInstance the scriptInstance to set
	 */
	public void setScriptInstance(ScriptInstance scriptInstance) {
		this.scriptInstance = scriptInstance;
	}
	
	
	
}