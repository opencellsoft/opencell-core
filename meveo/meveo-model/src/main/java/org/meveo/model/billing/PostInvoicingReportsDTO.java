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

import java.math.BigDecimal;

public class PostInvoicingReportsDTO {

	private Integer invoicesNumber = 0;
	private Integer positiveInvoicesNumber = 0;
	private Integer negativeInvoicesNumber = 0;

	private BigDecimal globalAmount = BigDecimal.ZERO;

	private BigDecimal positiveInvoicesAmountHT = BigDecimal.ZERO;
	private BigDecimal positiveInvoicesTaxAmount = BigDecimal.ZERO;
	private BigDecimal positiveInvoicesAmount = BigDecimal.ZERO;

	private BigDecimal negativeInvoicesAmountHT = BigDecimal.ZERO;
	private BigDecimal negativeInvoicesTaxAmount = BigDecimal.ZERO;
	private BigDecimal negativeInvoicesAmount = BigDecimal.ZERO;

	private Integer emptyInvoicesNumber = 0;
	private Integer electronicInvoicesNumber = 0;

	private Integer checkInvoicesNumber = 0;
	private Integer directDebitInvoicesNumber = 0;
	private Integer tipInvoicesNumber = 0;
	private Integer wiretransferInvoicesNumber = 0;

	private BigDecimal checkAmuontHT = BigDecimal.ZERO;
	private BigDecimal directDebitAmuontHT = BigDecimal.ZERO;
	private BigDecimal tipAmuontHT = BigDecimal.ZERO;
	private BigDecimal wiretransferAmuontHT = BigDecimal.ZERO;

	private BigDecimal checkAmuont = BigDecimal.ZERO;
	private BigDecimal directDebitAmuont = BigDecimal.ZERO;
	private BigDecimal tipAmuont = BigDecimal.ZERO;
	private BigDecimal wiretransferAmuont = BigDecimal.ZERO;

	public Integer getInvoicesNumber() {
		return invoicesNumber;
	}

	public void setInvoicesNumber(Integer invoicesNumber) {
		this.invoicesNumber = invoicesNumber;
	}

	public Integer getPositiveInvoicesNumber() {
		return positiveInvoicesNumber;
	}

	public void setPositiveInvoicesNumber(Integer positiveInvoicesNumber) {
		this.positiveInvoicesNumber = positiveInvoicesNumber;
	}

	public Integer getNegativeInvoicesNumber() {
		return negativeInvoicesNumber;
	}

	public void setNegativeInvoicesNumber(Integer negativeInvoicesNumber) {
		this.negativeInvoicesNumber = negativeInvoicesNumber;
	}

	public BigDecimal getGlobalAmount() {
		return globalAmount;
	}

	public void setGlobalAmount(BigDecimal globalAmount) {
		this.globalAmount = globalAmount;
	}

	public BigDecimal getPositiveInvoicesAmountHT() {
		return positiveInvoicesAmountHT;
	}

	public void setPositiveInvoicesAmountHT(BigDecimal positiveInvoicesAmountHT) {
		this.positiveInvoicesAmountHT = positiveInvoicesAmountHT;
	}

	public BigDecimal getPositiveInvoicesTaxAmount() {
		return positiveInvoicesTaxAmount;
	}

	public void setPositiveInvoicesTaxAmount(BigDecimal positiveInvoicesTaxAmount) {
		this.positiveInvoicesTaxAmount = positiveInvoicesTaxAmount;
	}

	public BigDecimal getPositiveInvoicesAmount() {
		return positiveInvoicesAmount;
	}

	public void setPositiveInvoicesAmount(BigDecimal positiveInvoicesAmount) {
		this.positiveInvoicesAmount = positiveInvoicesAmount;
	}

	public BigDecimal getNegativeInvoicesAmountHT() {
		return negativeInvoicesAmountHT;
	}

	public void setNegativeInvoicesAmountHT(BigDecimal negativeInvoicesAmountHT) {
		this.negativeInvoicesAmountHT = negativeInvoicesAmountHT;
	}

	public BigDecimal getNegativeInvoicesTaxAmount() {
		return negativeInvoicesTaxAmount;
	}

	public void setNegativeInvoicesTaxAmount(BigDecimal negativeInvoicesTaxAmount) {
		this.negativeInvoicesTaxAmount = negativeInvoicesTaxAmount;
	}

	public BigDecimal getNegativeInvoicesAmount() {
		return negativeInvoicesAmount;
	}

	public void setNegativeInvoicesAmount(BigDecimal negativeInvoicesAmount) {
		this.negativeInvoicesAmount = negativeInvoicesAmount;
	}

	public Integer getEmptyInvoicesNumber() {
		return emptyInvoicesNumber;
	}

	public void setEmptyInvoicesNumber(Integer emptyInvoicesNumber) {
		this.emptyInvoicesNumber = emptyInvoicesNumber;
	}

	public Integer getElectronicInvoicesNumber() {
		return electronicInvoicesNumber;
	}

	public void setElectronicInvoicesNumber(Integer electronicInvoicesNumber) {
		this.electronicInvoicesNumber = electronicInvoicesNumber;
	}

	public Integer getCheckInvoicesNumber() {
		return checkInvoicesNumber;
	}

	public void setCheckInvoicesNumber(Integer checkInvoicesNumber) {
		this.checkInvoicesNumber = checkInvoicesNumber;
	}

	public Integer getDirectDebitInvoicesNumber() {
		return directDebitInvoicesNumber;
	}

	public void setDirectDebitInvoicesNumber(Integer directDebitInvoicesNumber) {
		this.directDebitInvoicesNumber = directDebitInvoicesNumber;
	}

	public Integer getTipInvoicesNumber() {
		return tipInvoicesNumber;
	}

	public void setTipInvoicesNumber(Integer tipInvoicesNumber) {
		this.tipInvoicesNumber = tipInvoicesNumber;
	}

	public Integer getWiretransferInvoicesNumber() {
		return wiretransferInvoicesNumber;
	}

	public void setWiretransferInvoicesNumber(Integer wiretransferInvoicesNumber) {
		this.wiretransferInvoicesNumber = wiretransferInvoicesNumber;
	}

	public BigDecimal getCheckAmuontHT() {
		return checkAmuontHT;
	}

	public void setCheckAmuontHT(BigDecimal checkAmuontHT) {
		this.checkAmuontHT = checkAmuontHT;
	}

	public BigDecimal getDirectDebitAmuontHT() {
		return directDebitAmuontHT;
	}

	public void setDirectDebitAmuontHT(BigDecimal directDebitAmuontHT) {
		this.directDebitAmuontHT = directDebitAmuontHT;
	}

	public BigDecimal getTipAmuontHT() {
		return tipAmuontHT;
	}

	public void setTipAmuontHT(BigDecimal tipAmuontHT) {
		this.tipAmuontHT = tipAmuontHT;
	}

	public BigDecimal getWiretransferAmuontHT() {
		return wiretransferAmuontHT;
	}

	public void setWiretransferAmuontHT(BigDecimal wiretransferAmuontHT) {
		this.wiretransferAmuontHT = wiretransferAmuontHT;
	}

	public BigDecimal getCheckAmuont() {
		return checkAmuont;
	}

	public void setCheckAmuont(BigDecimal checkAmuont) {
		this.checkAmuont = checkAmuont;
	}

	public BigDecimal getDirectDebitAmuont() {
		return directDebitAmuont;
	}

	public void setDirectDebitAmuont(BigDecimal directDebitAmuont) {
		this.directDebitAmuont = directDebitAmuont;
	}

	public BigDecimal getTipAmuont() {
		return tipAmuont;
	}

	public void setTipAmuont(BigDecimal tipAmuont) {
		this.tipAmuont = tipAmuont;
	}

	public BigDecimal getWiretransferAmuont() {
		return wiretransferAmuont;
	}

	public void setWiretransferAmuont(BigDecimal wiretransferAmuont) {
		this.wiretransferAmuont = wiretransferAmuont;
	}

}
