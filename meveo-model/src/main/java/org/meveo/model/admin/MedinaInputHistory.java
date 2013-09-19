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

/**
 * Information about inputs Medina received and processed.
 */
@Entity
@DiscriminatorValue("MEDINA")
public class MedinaInputHistory extends InputHistory {

	private static final long serialVersionUID = 1L;

	@Column(name = "EDR_NAME")
	private String edrOutputName;

	public MedinaInputHistory() {
	}

	/**
	 * Convert from {@link InputHistory} to {@link MedinaInputHistory}.
	 * 
	 * @param inputInfo
	 *            {@link InputHistory} superclass.
	 */
	public MedinaInputHistory(InputHistory inputInfo) {
		this.setId(inputInfo.getId());
		this.setVersion(inputInfo.getVersion());
		this.setName(inputInfo.getName());
		this.setAnalysisEndDate(inputInfo.getAnalysisEndDate());
		this.setAnalysisStartDate(inputInfo.getAnalysisStartDate());
		this.setParsedTickets(inputInfo.getParsedTickets());
		this.setSucceededTickets(inputInfo.getSucceededTickets());
		this.setRejectedTickets(inputInfo.getRejectedTickets());
		this.setIgnoredTickets(inputInfo.getIgnoredTickets());
		this.setProvider(inputInfo.getProvider());
	}

	public String getEdrOutputName() {
		return edrOutputName;
	}

	public void setEdrOutputName(String edrOutputName) {
		this.edrOutputName = edrOutputName;
	}

}
