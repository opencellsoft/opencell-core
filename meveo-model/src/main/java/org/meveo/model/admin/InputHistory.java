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
