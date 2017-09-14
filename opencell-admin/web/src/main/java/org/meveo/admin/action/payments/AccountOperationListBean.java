package org.meveo.admin.action.payments;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

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
