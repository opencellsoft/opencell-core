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

package newer;

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

/**
 * Discount entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "DISCOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "NEWER_DISCOUNT_SEQ")

public class Discount {
	
	@Column(name = "ID")
	private Integer id;
	
	
	@Column(name = "PROVIDER_ID")
	private Integer providerId;
	
	
	@Column(name = "DISCOUNT_CODE", length = 20)
	private String discountCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED")
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;
	
	
	@Column(name = "PR_DESCRIPTION", length = 100)
	private String prDescription;
	
	
	@Column(name = "POURCENT")
	private Integer pourcent;
	
	
	@Column(name = "CREATOR_ID")
	private Integer creatorId;
	
	
	@Column(name = "UPDATER_ID")
	private Integer updaterId;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getProviderId() {
		return providerId;
	}


	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}


	public String getDiscountCode() {
		return discountCode;
	}


	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
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


	public String getPrDescription() {
		return prDescription;
	}


	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}


	public Integer getPourcent() {
		return pourcent;
	}


	public void setPourcent(Integer pourcent) {
		this.pourcent = pourcent;
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
