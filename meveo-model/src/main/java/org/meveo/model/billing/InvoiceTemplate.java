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
package org.meveo.model.billing;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "BILLING_INVOICE_TEMPLATE", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }),
		@UniqueConstraint(columnNames = { "FILE_NAME", "PROVIDER_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_TEMPLATE_SEQ")
public class InvoiceTemplate extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "CODE", nullable = false, length = 20)
	private String code;

	@Column(name = "TEMPLATE_VERSION", nullable = false)
	private String templateVersion;

	@Column(name = "VALIDITY_START_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date validityStartDate;

	@Column(name = "VALIDITY_END_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date validityEndDate;

	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
	}

	public Date getValidityStartDate() {
		return validityStartDate;
	}

	public void setValidityStartDate(Date validityStartDate) {
		this.validityStartDate = validityStartDate;
	}

	public Date getValidityEndDate() {
		return validityEndDate;
	}

	public void setValidityEndDate(Date validityEndDate) {
		this.validityEndDate = validityEndDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
