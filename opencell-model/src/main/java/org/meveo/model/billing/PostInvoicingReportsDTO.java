/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
public class PostInvoicingReportsDTO implements Serializable {

    private static final long serialVersionUID = -5837078664368622411L;

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
    private Integer creditDebitCardInvoicesNumber = 0;
    private Integer npmInvoicesNumber = 0;

    private BigDecimal checkAmuontHT = BigDecimal.ZERO;
    private BigDecimal directDebitAmuontHT = BigDecimal.ZERO;
    private BigDecimal tipAmuontHT = BigDecimal.ZERO;
    private BigDecimal wiretransferAmuontHT = BigDecimal.ZERO;
    private BigDecimal creditDebitCardAmountHT = BigDecimal.ZERO;
    private BigDecimal npmAmountHT = BigDecimal.ZERO;

    private BigDecimal checkAmuont = BigDecimal.ZERO;
    private BigDecimal directDebitAmuont = BigDecimal.ZERO;
    private BigDecimal tipAmuont = BigDecimal.ZERO;
    private BigDecimal wiretransferAmuont = BigDecimal.ZERO;
    private BigDecimal creditDebitCardAmount = BigDecimal.ZERO;
    private BigDecimal npmAmount = BigDecimal.ZERO;

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

    public Integer getCreditDebitCardInvoicesNumber() {
        return creditDebitCardInvoicesNumber;
    }

    public void setCreditDebitCardInvoicesNumber(Integer creditDebitCardInvoicesNumber) {
        this.creditDebitCardInvoicesNumber = creditDebitCardInvoicesNumber;
    }

    public BigDecimal getCreditDebitCardAmountHT() {
        return creditDebitCardAmountHT;
    }

    public void setCreditDebitCardAmountHT(BigDecimal creditDebitCardAmountHT) {
        this.creditDebitCardAmountHT = creditDebitCardAmountHT;
    }

    public BigDecimal getCreditDebitCardAmount() {
        return creditDebitCardAmount;
    }

    public void setCreditDebitCardAmount(BigDecimal creditDebitCardAmount) {
        this.creditDebitCardAmount = creditDebitCardAmount;
    }

    public Integer getNpmInvoicesNumber() {
        return npmInvoicesNumber;
    }

    public void setNpmInvoicesNumber(Integer npmInvoicesNumber) {
        this.npmInvoicesNumber = npmInvoicesNumber;
    }

    public BigDecimal getNpmAmountHT() {
        return npmAmountHT;
    }

    public void setNpmAmountHT(BigDecimal npmAmountHT) {
        this.npmAmountHT = npmAmountHT;
    }

    public BigDecimal getNpmAmount() {
        return npmAmount;
    }

    public void setNpmAmount(BigDecimal npmAmount) {
        this.npmAmount = npmAmount;
    }

}
