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
package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.mediation.Access;

/**
 * Bean for EDR data.
 * 
 * @author seb
 * @created Aug 6, 2012
 */

@Entity
@Table(name = "RATING_EDR")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RATING_EDR_SEQ")
public class EDR  extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1278336655583933747L;
	
	@Column(name="EVENT_TYPE")
	private String eventType;
	//data from Access, zoning plan, time plan and numbering plan
	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	private Access originAccess;
	@ManyToOne(fetch=FetchType.LAZY,optional=true)
	private Access targetAccess;
	
	@Column(name="CUSTOM_TEXT1")
	private String customText1;
	
	@Column(name="CUSTOM_TEXT2")
    private String customText2;
	
	@Column(name="CUSTOM_TEXT3")
    private String customText3;
	
	@Column(name="CUSTOM_TEXT4")
    private String customText4;
	
	@Column(name="CUSTOM_NUMBER1")
    private BigDecimal customNumber1;

	@Column(name="CUSTOM_NUMBER2")
    private BigDecimal customNumber2;

	@Column(name="CUSTOM_NUMBER3")
	private BigDecimal customNumber3;

	@Column(name="CUSTOM_NUMBER4")
    private BigDecimal customNumber4;
	
	@Column(name="CUSTOM_DATE1")
    private Date customDate1;

	@Column(name="CUSTOM_DATE2")
    private Date customDate2;
	
	@Column(name="CUSTOM_DATE3")
    private Date customDate3;
	
	@Column(name="CUSTOM_DATE4")
    private Date customDate4;
	
	@Column(name="CUSTOM_BOOLEAN1")
    private Boolean customBoolean1;
	
	@Column(name="CUSTOM_BOOLEAN2")
    private Boolean customBoolean2;
	
	@Column(name="CUSTOM_BOOLEAN3")
    private Boolean customBoolean3;
	
	@Column(name="CUSTOM_BOOLEAN4")
    private Boolean customBoolean4;
    
    
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Access getOriginAccess() {
		return originAccess;
	}
	public void setOriginAccess(Access originAccess) {
		this.originAccess = originAccess;
	}
	public Access getTargetAccess() {
		return targetAccess;
	}
	public void setTargetAccess(Access targetAccess) {
		this.targetAccess = targetAccess;
	}
	public String getCustomText1() {
		return customText1;
	}
	public void setCustomText1(String customText1) {
		this.customText1 = customText1;
	}
	public String getCustomText2() {
		return customText2;
	}
	public void setCustomText2(String customText2) {
		this.customText2 = customText2;
	}
	public String getCustomText3() {
		return customText3;
	}
	public void setCustomText3(String customText3) {
		this.customText3 = customText3;
	}
	public String getCustomText4() {
		return customText4;
	}
	public void setCustomText4(String customText4) {
		this.customText4 = customText4;
	}
	public BigDecimal getCustomNumber1() {
		return customNumber1;
	}
	public void setCustomNumber1(BigDecimal customNumber1) {
		this.customNumber1 = customNumber1;
	}
	public BigDecimal getCustomNumber2() {
		return customNumber2;
	}
	public void setCustomNumber2(BigDecimal customNumber2) {
		this.customNumber2 = customNumber2;
	}
	public BigDecimal getCustomNumber3() {
		return customNumber3;
	}
	public void setCustomNumber3(BigDecimal customNumber3) {
		this.customNumber3 = customNumber3;
	}
	public BigDecimal getCustomNumber4() {
		return customNumber4;
	}
	public void setCustomNumber4(BigDecimal customNumber4) {
		this.customNumber4 = customNumber4;
	}
	public Date getCustomDate1() {
		return customDate1;
	}
	public void setCustomDate1(Date customDate1) {
		this.customDate1 = customDate1;
	}
	public Date getCustomDate2() {
		return customDate2;
	}
	public void setCustomDate2(Date customDate2) {
		this.customDate2 = customDate2;
	}
	public Date getCustomDate3() {
		return customDate3;
	}
	public void setCustomDate3(Date customDate3) {
		this.customDate3 = customDate3;
	}
	public Date getCustomDate4() {
		return customDate4;
	}
	public void setCustomDate4(Date customDate4) {
		this.customDate4 = customDate4;
	}
	public Boolean getCustomBoolean1() {
		return customBoolean1;
	}
	public void setCustomBoolean1(Boolean customBoolean1) {
		this.customBoolean1 = customBoolean1;
	}
	public Boolean getCustomBoolean2() {
		return customBoolean2;
	}
	public void setCustomBoolean2(Boolean customBoolean2) {
		this.customBoolean2 = customBoolean2;
	}
	public Boolean getCustomBoolean3() {
		return customBoolean3;
	}
	public void setCustomBoolean3(Boolean customBoolean3) {
		this.customBoolean3 = customBoolean3;
	}
	public Boolean getCustomBoolean4() {
		return customBoolean4;
	}
	public void setCustomBoolean4(Boolean customBoolean4) {
		this.customBoolean4 = customBoolean4;
	}
    
}
