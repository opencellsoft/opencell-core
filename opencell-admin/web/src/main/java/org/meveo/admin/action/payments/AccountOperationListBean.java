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

package org.meveo.admin.action.payments;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;

@Named
@ConversationScoped
public class AccountOperationListBean extends AccountOperationBean {

    private static final long serialVersionUID = 6901290794255512020L;

    private List<PartialMatchingOccToSelect> partialMatchingOps = new ArrayList<PartialMatchingOccToSelect>();

    public List<PartialMatchingOccToSelect> getPartialMatchingOps() {
        return partialMatchingOps;
    }

    public void setPartialMatchingOps(List<PartialMatchingOccToSelect> partialMatchingOps) {
        this.partialMatchingOps = partialMatchingOps;
    }

    // called from page of selection partial operation
    public String partialMatching(PartialMatchingOccToSelect partialMatchingOccSelected) {
        List<Long> operationIds = new ArrayList<Long>();
        for (PartialMatchingOccToSelect p : getPartialMatchingOps()) {
            operationIds.add(p.getAccountOperation().getId());
        }
        try {
            MatchingReturnObject result = matchingCodeService.matchOperations(partialMatchingOccSelected.getAccountOperation().getCustomerAccount().getId(), null, operationIds,
                partialMatchingOccSelected.getAccountOperation().getId());
            if (result.isOk()) {
                messages.info(new BundleKey("messages", "customerAccount.matchingSuccessful"));
            } else {
                messages.error(new BundleKey("messages", "customerAccount.matchingFailed"));
            }
        } catch (NoAllOperationUnmatchedException ee) {
            messages.error(new BundleKey("messages", "customerAccount.noAllOperationUnmatched"));
        } catch (Exception e) {
            log.error("failed to partial matching", e);
            messages.error(e.getMessage());
        }
        return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + partialMatchingOccSelected.getAccountOperation().getCustomerAccount().getId()
                + "&edit=true&mainTab=1&faces-redirect=true";
    }
}
