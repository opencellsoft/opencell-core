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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;

/**
 * Information about MEVEO inputs. Applications like Medina, Vertina or Oudaya
 * receives input then processes it and then provide output. Source of input can
 * be files, webservices, JMS, database etc. Input usually has number of tickets
 * that has to be processed. So this class holds information about number of
 * tickets parsed from input and how much of them were successfully processed
 * and how much were rejected. If application specific input has more
 * information it extends this entity.
 */
@Entity
@Table(name = "ADM_INPUT_HISTORY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INPUT_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("NOT_SPECIFIED")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_INPUT_HISTORY_SEQ")
public class InputHistory extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "NAME")
	private String name;

	@Column(name = "START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date analysisStartDate;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date analysisEndDate;

	@Column(name = "PARSED_TICKETS")
	private Integer parsedTickets;

	@Column(name = "SUCCEEDED_TICKETS")
	private Integer succeededTickets;

	@Column(name = "IGNORED_TICKETS")
	private Integer ignoredTickets;

	@Column(name = "REJECTED_TICKETS")
	private Integer rejectedTickets;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getAnalysisStartDate() {
		return analysisStartDate;
	}

	public void setAnalysisStartDate(Date analysisStartDate) {
		this.analysisStartDate = analysisStartDate;
	}

	public Date getAnalysisEndDate() {
		return analysisEndDate;
	}

	public void setAnalysisEndDate(Date analysisEndDate) {
		this.analysisEndDate = analysisEndDate;
	}

	public Integer getParsedTickets() {
		return parsedTickets;
	}

	public void setParsedTickets(Integer parsedTickets) {
		this.parsedTickets = parsedTickets;
	}

	public Integer getSucceededTickets() {
		return succeededTickets;
	}

	public void setSucceededTickets(Integer succeededTickets) {
		this.succeededTickets = succeededTickets;
	}

	public Integer getRejectedTickets() {
		return rejectedTickets;
	}

	public void setRejectedTickets(Integer rejectedTickets) {
		this.rejectedTickets = rejectedTickets;
	}

	public Integer getIgnoredTickets() {
		return ignoredTickets;
	}

	public void setIgnoredTickets(Integer ignoredTickets) {
		this.ignoredTickets = ignoredTickets;
	}

}
