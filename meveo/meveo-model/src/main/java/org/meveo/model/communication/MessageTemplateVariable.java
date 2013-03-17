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
package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name="COM_MSG_TMPL_VARIABLE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_MSG_TMPL_VAR_SEQ")
public class MessageTemplateVariable extends BusinessEntity{

	private static final long serialVersionUID = -8541728044647573746L;
	
	@ManyToOne
	@JoinColumn(name="TEMPLATE_ID")
	private MessageTemplate messageTemplate;

	@Column(name="MANDATORY")
	private boolean mandatory;
	
	
	public MessageTemplate getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(MessageTemplate messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	
}
