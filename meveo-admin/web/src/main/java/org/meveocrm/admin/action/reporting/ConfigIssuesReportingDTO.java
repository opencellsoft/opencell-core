/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveocrm.admin.action.reporting;

import java.io.Serializable;

 
public class ConfigIssuesReportingDTO implements Serializable {

    private static final long serialVersionUID = 8775799547993130837L;

    private Integer nbrLanguagesNotAssociated;
    private Integer nbrTaxesNotAssociated;
    private Integer nbrInvoiceCatNotAssociated;
    private Integer nbrInvoiceSubCatNotAssociated;
    private Integer nbrUsagesChrgNotAssociated;
    private Integer nbrCountersNotAssociated;
    private Integer nbrRecurringChrgNotAssociated;
    private Integer nbrSubChrgNotAssociated;
    private Integer nbrTerminationChrgNotAssociated;
    private Integer nbrServicesWithNotOffer;
    private Integer nbrChargesWithNotPricePlan;
    private Integer nbrRejectedEDR;
    private Integer nbrWalletOpOpen;
    private Integer nbrWalletOpTreated;
    private Integer nbrWalletOpCancled;
    private Integer nbrWalletOpReserved;
    private Integer nbrWalletOpTorerate;
    private Integer nbrWalletOpRerated;
    private Integer nbrEdrRejected;
    private Integer nbrEdrRated;
    private Integer nbrEdrOpen;
    
  
	public ConfigIssuesReportingDTO() {
		super();
	}


	public ConfigIssuesReportingDTO(Integer nbrLanguagesNotAssociated,
			Integer nbrTaxesNotAssociated, Integer nbrInvoiceCatNotAssociated,
			Integer nbrInvoiceSubCatNotAssociated,
			Integer nbrUsagesChrgNotAssociated,
			Integer nbrCountersNotAssociated,
			Integer nbrRecurringChrgNotAssociated,
			Integer nbrSubChrgNotAssociated,
			Integer nbrTerminationChrgNotAssociated,
			Integer nbrServicesWithNotOffer,
			Integer nbrChargesWithNotPricePlan, Integer nbrRejectedEDR,
			Integer nbrWalletOpOpen, Integer nbrWalletOpTreated,
			Integer nbrWalletOpCancled, Integer nbrWalletOpReserved,
			Integer nbrWalletOpTorerate, Integer nbrWalletOpRerated,
			Integer nbrEdrRejected, Integer nbrEdrRated, Integer nbrEdrOpen) {
		super();
		this.nbrLanguagesNotAssociated = nbrLanguagesNotAssociated;
		this.nbrTaxesNotAssociated = nbrTaxesNotAssociated;
		this.nbrInvoiceCatNotAssociated = nbrInvoiceCatNotAssociated;
		this.nbrInvoiceSubCatNotAssociated = nbrInvoiceSubCatNotAssociated;
		this.nbrUsagesChrgNotAssociated = nbrUsagesChrgNotAssociated;
		this.nbrCountersNotAssociated = nbrCountersNotAssociated;
		this.nbrRecurringChrgNotAssociated = nbrRecurringChrgNotAssociated;
		this.nbrSubChrgNotAssociated = nbrSubChrgNotAssociated;
		this.nbrTerminationChrgNotAssociated = nbrTerminationChrgNotAssociated;
		this.nbrServicesWithNotOffer = nbrServicesWithNotOffer;
		this.nbrChargesWithNotPricePlan = nbrChargesWithNotPricePlan;
		this.nbrRejectedEDR = nbrRejectedEDR;
		this.nbrWalletOpOpen = nbrWalletOpOpen;
		this.nbrWalletOpTreated = nbrWalletOpTreated;
		this.nbrWalletOpCancled = nbrWalletOpCancled;
		this.nbrWalletOpReserved = nbrWalletOpReserved;
		this.nbrWalletOpTorerate = nbrWalletOpTorerate;
		this.nbrWalletOpRerated = nbrWalletOpRerated;
		this.nbrEdrRejected = nbrEdrRejected;
		this.nbrEdrRated = nbrEdrRated;
		this.nbrEdrOpen = nbrEdrOpen;
	}


	public Integer getNbrLanguagesNotAssociated() {
		return nbrLanguagesNotAssociated;
	}


	public void setNbrLanguagesNotAssociated(Integer nbrLanguagesNotAssociated) {
		this.nbrLanguagesNotAssociated = nbrLanguagesNotAssociated;
	}


	public Integer getNbrTaxesNotAssociated() {
		return nbrTaxesNotAssociated;
	}


	public void setNbrTaxesNotAssociated(Integer nbrTaxesNotAssociated) {
		this.nbrTaxesNotAssociated = nbrTaxesNotAssociated;
	}


	public Integer getNbrInvoiceCatNotAssociated() {
		return nbrInvoiceCatNotAssociated;
	}


	public void setNbrInvoiceCatNotAssociated(Integer nbrInvoiceCatNotAssociated) {
		this.nbrInvoiceCatNotAssociated = nbrInvoiceCatNotAssociated;
	}


	public Integer getNbrInvoiceSubCatNotAssociated() {
		return nbrInvoiceSubCatNotAssociated;
	}


	public void setNbrInvoiceSubCatNotAssociated(
			Integer nbrInvoiceSubCatNotAssociated) {
		this.nbrInvoiceSubCatNotAssociated = nbrInvoiceSubCatNotAssociated;
	}


	public Integer getNbrUsagesChrgNotAssociated() {
		return nbrUsagesChrgNotAssociated;
	}


	public void setNbrUsagesChrgNotAssociated(Integer nbrUsagesChrgNotAssociated) {
		this.nbrUsagesChrgNotAssociated = nbrUsagesChrgNotAssociated;
	}


	public Integer getNbrCountersNotAssociated() {
		return nbrCountersNotAssociated;
	}


	public void setNbrCountersNotAssociated(Integer nbrCountersNotAssociated) {
		this.nbrCountersNotAssociated = nbrCountersNotAssociated;
	}


	public Integer getNbrRecurringChrgNotAssociated() {
		return nbrRecurringChrgNotAssociated;
	}


	public void setNbrRecurringChrgNotAssociated(
			Integer nbrRecurringChrgNotAssociated) {
		this.nbrRecurringChrgNotAssociated = nbrRecurringChrgNotAssociated;
	}


	public Integer getNbrSubChrgNotAssociated() {
		return nbrSubChrgNotAssociated;
	}


	public void setNbrSubChrgNotAssociated(Integer nbrSubChrgNotAssociated) {
		this.nbrSubChrgNotAssociated = nbrSubChrgNotAssociated;
	}


	public Integer getNbrTerminationChrgNotAssociated() {
		return nbrTerminationChrgNotAssociated;
	}


	public void setNbrTerminationChrgNotAssociated(
			Integer nbrTerminationChrgNotAssociated) {
		this.nbrTerminationChrgNotAssociated = nbrTerminationChrgNotAssociated;
	}


	public Integer getNbrServicesWithNotOffer() {
		return nbrServicesWithNotOffer;
	}


	public void setNbrServicesWithNotOffer(Integer nbrServicesWithNotOffer) {
		this.nbrServicesWithNotOffer = nbrServicesWithNotOffer;
	}


	public Integer getNbrChargesWithNotPricePlan() {
		return nbrChargesWithNotPricePlan;
	}


	public void setNbrChargesWithNotPricePlan(Integer nbrChargesWithNotPricePlan) {
		this.nbrChargesWithNotPricePlan = nbrChargesWithNotPricePlan;
	}


	public Integer getNbrRejectedEDR() {
		return nbrRejectedEDR;
	}


	public void setNbrRejectedEDR(Integer nbrRejectedEDR) {
		this.nbrRejectedEDR = nbrRejectedEDR;
	}


	public Integer getNbrWalletOpOpen() {
		return nbrWalletOpOpen;
	}


	public void setNbrWalletOpOpen(Integer nbrWalletOpOpen) {
		this.nbrWalletOpOpen = nbrWalletOpOpen;
	}


	public Integer getNbrWalletOpTreated() {
		return nbrWalletOpTreated;
	}


	public void setNbrWalletOpTreated(Integer nbrWalletOpTreated) {
		this.nbrWalletOpTreated = nbrWalletOpTreated;
	}


	public Integer getNbrWalletOpCancled() {
		return nbrWalletOpCancled;
	}


	public void setNbrWalletOpCancled(Integer nbrWalletOpCancled) {
		this.nbrWalletOpCancled = nbrWalletOpCancled;
	}


	public Integer getNbrWalletOpReserved() {
		return nbrWalletOpReserved;
	}


	public void setNbrWalletOpReserved(Integer nbrWalletOpReserved) {
		this.nbrWalletOpReserved = nbrWalletOpReserved;
	}


	public Integer getNbrWalletOpTorerate() {
		return nbrWalletOpTorerate;
	}


	public void setNbrWalletOpTorerate(Integer nbrWalletOpTorerate) {
		this.nbrWalletOpTorerate = nbrWalletOpTorerate;
	}


	public Integer getNbrWalletOpRerated() {
		return nbrWalletOpRerated;
	}


	public void setNbrWalletOpRerated(Integer nbrWalletOpRerated) {
		this.nbrWalletOpRerated = nbrWalletOpRerated;
	}


	public Integer getNbrEdrRejected() {
		return nbrEdrRejected;
	}


	public void setNbrEdrRejected(Integer nbrEdrRejected) {
		this.nbrEdrRejected = nbrEdrRejected;
	}


	public Integer getNbrEdrRated() {
		return nbrEdrRated;
	}


	public void setNbrEdrRated(Integer nbrEdrRated) {
		this.nbrEdrRated = nbrEdrRated;
	}


	public Integer getNbrEdrOpen() {
		return nbrEdrOpen;
	}


	public void setNbrEdrOpen(Integer nbrEdrOpen) {
		this.nbrEdrOpen = nbrEdrOpen;
	}


	

	
}