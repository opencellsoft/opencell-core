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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.EnableEntity;

/**
 * Zonning Plan.
 * 
 * @author seb
 * @created Aug 6, 2012
 */
@Entity
@Table(name = "MEDINA_ZONNING_PLAN")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_ZONNING_PLAN_SEQ")
public class ZonningPlan extends EnableEntity {
	
	private static final long serialVersionUID = 1L;

	//input
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name="PRIORITY")
	private Integer priority;
	
	@Column(name = "DISCRIMINATOR_CODE", length = 50)
    private String discriminatorCode;

    @Column(name = "ORIGIN_PREFIX", length = 50)
    private String originPrefix;
    
   
    
    //output
	@Column(name = "ZONE_CODE", length = 50)
    private String zoneCode;
    
    //output
	@Column(name = "SUB_ZONE_CODE", length = 50)
    private String subZoneCode;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getDiscriminatorCode() {
		return discriminatorCode;
	}

	public void setDiscriminatorCode(String discriminatorCode) {
		this.discriminatorCode = discriminatorCode;
	}

	public String getOriginPrefix() {
		return originPrefix;
	}

	public void setOriginPrefix(String originPrefix) {
		this.originPrefix = originPrefix;
	}

 

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public String getSubZoneCode() {
		return subZoneCode;
	}

	public void setSubZoneCode(String subZoneCode) {
		this.subZoneCode = subZoneCode;
	}
    
	
}
