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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.meveo.model.EnableEntity;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PriceCode;
import org.meveo.model.crm.Customer;

@Entity
@Table(name = "RATING_EDR_PLAN")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RATING_EDR_PLAN_SEQ")
public class EDRRatingPlan extends EnableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5877988223215597208L;

	//input
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	
	@Column(name="PRIORITY")
	private Integer priority;

	@Column(name="EVENT_TYPE")
	private String eventType;
	
	//test if access belong to the customer
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ORIGIN_CUSTOMER")
	private Customer originCustomer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="TARGET_CUSTOMER")
	private Customer targetCustomer;
	
	//test text value for prefix
	@Column(name="TEXT1_PREFIX")
	private String customText1Prefix;

	@Column(name="TEXT2_PREFIX")
    private String customText2Prefix;

	@Column(name="TEXT3_PREFIX")
    private String customText3Prefix;

	@Column(name="TEXT4_PREFIX")
    private String customText4Prefix;

	//test text value for regex
	@Column(name="TEXT1_REGEX")
	private String customText1Regex;

	@Column(name="TEXT2_REGEX")
    private String customText2Regex;

	@Column(name="TEXT3_REGEX")
    private String customText3Regex;

	@Column(name="TEXT4_REGEX")
    private String customText4Regex;

	//test number value for min (data>=value)
	@Column(name="NUMBER1_MIN")
    private BigDecimal customNumber1Min;
	
	@Column(name="NUMBER2_MIN")
    private BigDecimal customNumber2Min;
    
	@Column(name="NUMBER3_MIN")
    private BigDecimal customNumber3Min;
    
	@Column(name="NUMBER4_MIN")
    private BigDecimal customNumber4Min;

    //test number value for max (data<value)
	@Column(name="NUMBER1_MAX")
    private BigDecimal customNumber1Max;

	@Column(name="NUMBER2_MAX")
	private BigDecimal customNumber2Max;

	@Column(name="NUMBER3_MAX")
    private BigDecimal customNumber3Max;

	@Column(name="NUMBER4_MAX")
    private BigDecimal customNumber4Max;

	//test Date value for min (data.time>=value.time)
	@Column(name="DATE1_MIN")
    private Date customDate1Min;
	
	@Column(name="DATE2_MIN")
    private Date customDate2Min;

	@Column(name="DATE3_MIN")
    private Date customDate3Min;

	@Column(name="DATE4_MIN")
    private Date customDate4Min;
    
	//test Date value for max (data.time<value.max)
	@Column(name="DATE1_MAX")
    private Date customDate1Max;

	@Column(name="DATE2_MAX")
	private Date customDate2Max;
    
	@Column(name="DATE3_MAX")
	private Date customDate3Max;

	@Column(name="DATE4_MAX")
	private Date customDate4Max;
    
    //test Boolean value 
	@Column(name="BOOLEAN1")
    private Boolean customBoolean1;

	@Column(name="BOOLEAN2")
    private Boolean customBoolean2;
	
	@Column(name="BOOLEAN3")
    private Boolean customBoolean3;
	
	@Column(name="BOOLEAN4")
    private Boolean customBoolean4;
    
    
    //output
    
    //if PriceCode is set, then the charges and 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODE", nullable = false)
	@Size(max = 20)
    private PriceCode priceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARGE_ID_IN")
    private OneShotChargeTemplate chargeTemplateIn;

    @Column(name = "CHARGE_IN_PRICE_1", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal chargeInPrice1;

    @Column(name = "CHARGE_IN_PRICE_2", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal chargeInPrice2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARGE_ID_OUT")
    private OneShotChargeTemplate chargeTemplateOut;

    @Column(name = "CHARGE_OUT_PRICE_1", precision = 23, scale = 12, nullable = true)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal chargeOutPrice1;

    @Column(name = "CHARGE_OUT_PRICE_2", precision = 23, scale = 12, nullable = true)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal chargeOutPrice2;
    
    //indicates if after getting the price or priceCode vertina should continue the rating process
    boolean continueRating=false;
    
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
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Customer getOriginCustomer() {
		return originCustomer;
	}
	public void setOriginCustomer(Customer originCustomer) {
		this.originCustomer = originCustomer;
	}
	public Customer getTargetCustomer() {
		return targetCustomer;
	}
	public void setTargetCustomer(Customer targetCustomer) {
		this.targetCustomer = targetCustomer;
	}
	public String getCustomText1Prefix() {
		return customText1Prefix;
	}
	public void setCustomText1Prefix(String customText1Prefix) {
		this.customText1Prefix = customText1Prefix;
	}
	public String getCustomText2Prefix() {
		return customText2Prefix;
	}
	public void setCustomText2Prefix(String customText2Prefix) {
		this.customText2Prefix = customText2Prefix;
	}
	public String getCustomText3Prefix() {
		return customText3Prefix;
	}
	public void setCustomText3Prefix(String customText3Prefix) {
		this.customText3Prefix = customText3Prefix;
	}
	public String getCustomText4Prefix() {
		return customText4Prefix;
	}
	public void setCustomText4Prefix(String customText4Prefix) {
		this.customText4Prefix = customText4Prefix;
	}
	public String getCustomText1Regex() {
		return customText1Regex;
	}
	public void setCustomText1Regex(String customText1Regex) {
		this.customText1Regex = customText1Regex;
	}
	public String getCustomText2Regex() {
		return customText2Regex;
	}
	public void setCustomText2Regex(String customText2Regex) {
		this.customText2Regex = customText2Regex;
	}
	public String getCustomText3Regex() {
		return customText3Regex;
	}
	public void setCustomText3Regex(String customText3Regex) {
		this.customText3Regex = customText3Regex;
	}
	public String getCustomText4Regex() {
		return customText4Regex;
	}
	public void setCustomText4Regex(String customText4Regex) {
		this.customText4Regex = customText4Regex;
	}
	public BigDecimal getCustomNumber1Min() {
		return customNumber1Min;
	}
	public void setCustomNumber1Min(BigDecimal customNumber1Min) {
		this.customNumber1Min = customNumber1Min;
	}
	public BigDecimal getCustomNumber2Min() {
		return customNumber2Min;
	}
	public void setCustomNumber2Min(BigDecimal customNumber2Min) {
		this.customNumber2Min = customNumber2Min;
	}
	public BigDecimal getCustomNumber3Min() {
		return customNumber3Min;
	}
	public void setCustomNumber3Min(BigDecimal customNumber3Min) {
		this.customNumber3Min = customNumber3Min;
	}
	public BigDecimal getCustomNumber4Min() {
		return customNumber4Min;
	}
	public void setCustomNumber4Min(BigDecimal customNumber4Min) {
		this.customNumber4Min = customNumber4Min;
	}
	public BigDecimal getCustomNumber1Max() {
		return customNumber1Max;
	}
	public void setCustomNumber1Max(BigDecimal customNumber1Max) {
		this.customNumber1Max = customNumber1Max;
	}
	public BigDecimal getCustomNumber2Max() {
		return customNumber2Max;
	}
	public void setCustomNumber2Max(BigDecimal customNumber2Max) {
		this.customNumber2Max = customNumber2Max;
	}
	public BigDecimal getCustomNumber3Max() {
		return customNumber3Max;
	}
	public void setCustomNumber3Max(BigDecimal customNumber3Max) {
		this.customNumber3Max = customNumber3Max;
	}
	public BigDecimal getCustomNumber4Max() {
		return customNumber4Max;
	}
	public void setCustomNumber4Max(BigDecimal customNumber4Max) {
		this.customNumber4Max = customNumber4Max;
	}
	public Date getCustomDate1Min() {
		return customDate1Min;
	}
	public void setCustomDate1Min(Date customDate1Min) {
		this.customDate1Min = customDate1Min;
	}
	public Date getCustomDate2Min() {
		return customDate2Min;
	}
	public void setCustomDate2Min(Date customDate2Min) {
		this.customDate2Min = customDate2Min;
	}
	public Date getCustomDate3Min() {
		return customDate3Min;
	}
	public void setCustomDate3Min(Date customDate3Min) {
		this.customDate3Min = customDate3Min;
	}
	public Date getCustomDate4Min() {
		return customDate4Min;
	}
	public void setCustomDate4Min(Date customDate4Min) {
		this.customDate4Min = customDate4Min;
	}
	public Date getCustomDate1Max() {
		return customDate1Max;
	}
	public void setCustomDate1Max(Date customDate1Max) {
		this.customDate1Max = customDate1Max;
	}
	public Date getCustomDate2Max() {
		return customDate2Max;
	}
	public void setCustomDate2Max(Date customDate2Max) {
		this.customDate2Max = customDate2Max;
	}
	public Date getCustomDate3Max() {
		return customDate3Max;
	}
	public void setCustomDate3Max(Date customDate3Max) {
		this.customDate3Max = customDate3Max;
	}
	public Date getCustomDate4Max() {
		return customDate4Max;
	}
	public void setCustomDate4Max(Date customDate4Max) {
		this.customDate4Max = customDate4Max;
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
 
		
	public PriceCode getPriceCode() {
		return priceCode;
	}
	public void setPriceCode(PriceCode priceCode) {
		this.priceCode = priceCode;
	}
	public OneShotChargeTemplate getChargeTemplateIn() {
		return chargeTemplateIn;
	}
	public void setChargeTemplateIn(OneShotChargeTemplate chargeTemplateIn) {
		this.chargeTemplateIn = chargeTemplateIn;
	}
	public BigDecimal getChargeInPrice1() {
		return chargeInPrice1;
	}
	public void setChargeInPrice1(BigDecimal chargeInPrice1) {
		this.chargeInPrice1 = chargeInPrice1;
	}
	public BigDecimal getChargeInPrice2() {
		return chargeInPrice2;
	}
	public void setChargeInPrice2(BigDecimal chargeInPrice2) {
		this.chargeInPrice2 = chargeInPrice2;
	}
	public OneShotChargeTemplate getChargeTemplateOut() {
		return chargeTemplateOut;
	}
	public void setChargeTemplateOut(OneShotChargeTemplate chargeTemplateOut) {
		this.chargeTemplateOut = chargeTemplateOut;
	}
	public BigDecimal getChargeOutPrice1() {
		return chargeOutPrice1;
	}
	public void setChargeOutPrice1(BigDecimal chargeOutPrice1) {
		this.chargeOutPrice1 = chargeOutPrice1;
	}
	public BigDecimal getChargeOutPrice2() {
		return chargeOutPrice2;
	}
	public void setChargeOutPrice2(BigDecimal chargeOutPrice2) {
		this.chargeOutPrice2 = chargeOutPrice2;
	}
	public boolean isContinueRating() {
		return continueRating;
	}
	public void setContinueRating(boolean continueRating) {
		this.continueRating = continueRating;
	}

    
}
