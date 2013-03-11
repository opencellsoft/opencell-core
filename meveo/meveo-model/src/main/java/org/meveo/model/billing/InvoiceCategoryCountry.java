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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jboss.seam.annotations.AutoCreate;
import org.meveo.model.AuditableEntity;

/**
 * InvoiceCategoryCountry entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "INVOICE_CATEGORY_ COUNTRY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_CATEGORY_COUNTRY_SEQ")

public class InvoiceCategoryCountry  extends AuditableEntity{
	private static final long serialVersionUID = 1L;

	
	@Column(name = "INVOICE_CATEGORY_ID")
	private Integer invoiceCategoryId;
	
	
	@Column(name = "COUNTRY_CODE", length = 2)
	private String countryCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED")
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;
	
	
	@Column(name = "DISCOUNT_CODE", length = 20)
	private String discountCode;
	
	
	@Column(name = "CREATOR_ID")
	private Integer creatorId;
	
	
	@Column(name = "UPDATER_ID")
	private Integer updaterId;




	public Integer getInvoiceCategoryId() {
		return invoiceCategoryId;
	}


	public void setInvoiceCategoryId(Integer invoiceCategoryId) {
		this.invoiceCategoryId = invoiceCategoryId;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	public Date getCreated() {
		return created;
	}


	public void setCreated(Date created) {
		this.created = created;
	}


	public Date getUpdated() {
		return updated;
	}


	public void setUpdated(Date updated) {
		this.updated = updated;
	}


	public String getDiscountCode() {
		return discountCode;
	}


	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}


	public Integer getCreatorId() {
		return creatorId;
	}


	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}


	public Integer getUpdaterId() {
		return updaterId;
	}


	public void setUpdaterId(Integer updaterId) {
		this.updaterId = updaterId;
	}


	
	
}
