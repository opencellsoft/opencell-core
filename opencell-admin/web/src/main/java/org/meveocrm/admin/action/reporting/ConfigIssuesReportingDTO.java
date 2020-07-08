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
package org.meveocrm.admin.action.reporting;

import java.io.Serializable;

public class ConfigIssuesReportingDTO implements Serializable {

    private static final long serialVersionUID = 8775799547993130837L;

    private Integer nbrRejectedEDR;
    private Integer nbrWalletOpOpen;
    private Integer nbrWalletOpTreated;
    private Integer nbrWalletOpCancled;
    private Integer nbrWalletOpReserved;
    private Integer nbrWalletOpTorerate;
    private Integer nbrWalletOpRerated;
    private Integer nbrWalletOpScheduled;
    private Integer nbrWalletOpRejected;
    private Integer nbrEdrRejected;
    private Integer nbrEdrRated;
    private Integer nbrEdrOpen;
    private Integer nbrEdrMediating;
    private Integer nbrEdrAggregated;

    public ConfigIssuesReportingDTO() {
        super();
        this.nbrRejectedEDR = 0;
        this.nbrWalletOpOpen = 0;
        this.nbrWalletOpTreated = 0;
        this.nbrWalletOpCancled = 0;
        this.nbrWalletOpReserved = 0;
        this.nbrWalletOpTorerate = 0;
        this.nbrWalletOpRerated = 0;
        this.nbrWalletOpScheduled = 0;
        this.nbrWalletOpRejected = 0;
        this.nbrEdrRejected = 0;
        this.nbrEdrRated = 0;
        this.nbrEdrOpen = 0;
        this.nbrEdrMediating = 0;
        this.nbrEdrAggregated = 0;
    }

    public ConfigIssuesReportingDTO(Integer nbrLanguagesNotAssociated, Integer nbrTaxesNotAssociated, Integer nbrInvoiceCatNotAssociated, Integer nbrInvoiceSubCatNotAssociated,
            Integer nbrUsagesChrgNotAssociated, Integer nbrCountersNotAssociated, Integer nbrRecurringChrgNotAssociated, Integer nbrSubChrgNotAssociated,
            Integer nbrTerminationChrgNotAssociated, Integer nbrServicesWithNotOffer, Integer nbrChargesWithNotPricePlan, Integer nbrRejectedEDR, Integer nbrWalletOpOpen,
            Integer nbrWalletOpTreated, Integer nbrWalletOpCancled, Integer nbrWalletOpReserved, Integer nbrWalletOpTorerate, Integer nbrWalletOpRerated, Integer nbrWalletOpRejected, 
            Integer nbrEdrRejected, Integer nbrEdrRated, Integer nbrEdrOpen, Integer nbrJasperDir) {
        super();
        this.nbrRejectedEDR = nbrRejectedEDR;
        this.nbrWalletOpOpen = nbrWalletOpOpen;
        this.nbrWalletOpTreated = nbrWalletOpTreated;
        this.nbrWalletOpCancled = nbrWalletOpCancled;
        this.nbrWalletOpReserved = nbrWalletOpReserved;
        this.nbrWalletOpTorerate = nbrWalletOpTorerate;
        this.nbrWalletOpRerated = nbrWalletOpRerated;
        this.nbrWalletOpRejected = nbrWalletOpRejected;
        this.nbrEdrRejected = nbrEdrRejected;
        this.nbrEdrRated = nbrEdrRated;
        this.nbrEdrOpen = nbrEdrOpen;
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

    public Integer getNbrWalletOpScheduled() {
        return nbrWalletOpScheduled;
    }

    public void setNbrWalletOpScheduled(Integer nbrWalletOpScheduled) {
        this.nbrWalletOpScheduled = nbrWalletOpScheduled;
    }

    public Integer getNbrEdrMediating() {
        return nbrEdrMediating;
    }

    public void setNbrEdrMediating(Integer nbrEdrMediating) {
        this.nbrEdrMediating = nbrEdrMediating;
    }

    public Integer getNbrEdrAggregated() {
        return nbrEdrAggregated;
    }

    public void setNbrEdrAggregated(Integer nbrEdrAggregated) {
        this.nbrEdrAggregated = nbrEdrAggregated;
    }

	public Integer getNbrWalletOpRejected() {
		return nbrWalletOpRejected;
	}

	public void setNbrWalletOpRejected(Integer nbrWalletOpRejected) {
		this.nbrWalletOpRejected = nbrWalletOpRejected;
	}

}