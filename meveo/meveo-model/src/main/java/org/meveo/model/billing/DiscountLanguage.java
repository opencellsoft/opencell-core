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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

/**
 * DiscountLanguage entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "BILLING_DISCOUNT_LANGUAGE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_DISCOUNT_LANGUAGE_SEQ")

public class DiscountLanguage  extends AuditableEntity{
	private static final long serialVersionUID = 1L;
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISCOUNT_ID")
	private Discount discount;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_LANGUAGE_ID")
	private TradingLanguage tradingLanguage;
	
	
	@Column(name = "DESCRIPTION", length = 50)
	private String description;
	
	

	public Discount getDiscount() {
		return discount;
	}


	public void setDiscount(Discount discount) {
		this.discount = discount;
	}


	public TradingLanguage getTradingLanguage() {
		return tradingLanguage;
	}


	public void setTradingLanguage(TradingLanguage tradingLanguage) {
		this.tradingLanguage = tradingLanguage;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
