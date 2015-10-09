package org.meveo.service.billing.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CreditNote;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CreditNoteService extends PersistenceService<CreditNote> {

	@Inject
	private ProviderService providerService;

	@Inject
	private SellerService sellerService;

	public String getCreditNoteNumber(CreditNote creditNote, User currentUser) {
		Seller seller = creditNote.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		String prefix = seller.getCreditNotePrefix();

		if (prefix == null) {
			prefix = seller.getProvider().getCreditNotePrefix();
		}
		if (prefix == null) {
			prefix = "";
		}

		if (prefix != null && !StringUtils.isBlank(prefix)) {
			if (prefix.indexOf("%") >= 0) {
				int startIndex = prefix.indexOf("%") + 1;
				int endIndex = prefix.indexOf("%", startIndex);
				if (endIndex > 0) {
					String datePattern = prefix.substring(startIndex, endIndex);
					String creditNoteDate = DateUtils.formatDateWithPattern(new Date(), datePattern);
					prefix = prefix.replace("%" + datePattern + "%", creditNoteDate);
				}
			}
		}

		long nextCreditNoteNb = getNextValue(seller, currentUser);

		int padSize = getNBOfChars(seller, currentUser);

		StringBuffer num1 = new StringBuffer(StringUtils.leftPad("", padSize, "0"));
		num1.append(nextCreditNoteNb + "");

		String creditNoteNumber = num1.substring(num1.length() - padSize);

		return (prefix + creditNoteNumber);
	}

	public synchronized int getNBOfChars(Seller seller, User currentUser) {
		int result = 9;

		if (seller != null) {
			if (seller.getCreditNoteSequenceSize() != null) {
				result = seller.getCreditNoteSequenceSize();
			} else {
				if (seller.getProvider().getCreditNoteSequenceSize() != null) {
					result = seller.getProvider().getCreditNoteSequenceSize();
				}
			}
		}

		return result;
	}

	public synchronized long getNextValue(Seller seller, User currentUser) {
		long result = 0;

		if (seller != null) {
			if (seller.getCurrentCreditNoteNb() != null) {
				long currentCreditNoteNo = seller.getCurrentCreditNoteNb();
				result = 1 + currentCreditNoteNo;
			} else {
				result = getNextValue(seller.getProvider(), currentUser);
			}
		}

		return result;
	}

	public synchronized long getNextValue(Provider provider, User currentUser) {
		long result = 0;

		if (provider != null) {
			long currentCreditNoteNo = provider.getCurrentCreditNoteNb() != null ? provider.getCurrentCreditNoteNb()
					: 0;
			result = 1 + currentCreditNoteNo;
		}

		return result;
	}

	public void updateCreditNoteNb(CreditNote creditNote, Long creditNoteNo) {
		Seller seller = creditNote.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
		if (seller != null && seller.getCurrentCreditNoteNb() != null
				&& (creditNoteNo - 1 == seller.getCurrentCreditNoteNb())) {
			seller.setCurrentCreditNoteNb(creditNoteNo);

			sellerService.update(seller, getCurrentUser());
		} else {
			Provider provider = seller.getProvider();
			provider.setCurrentCreditNoteNb(creditNoteNo);

			providerService.update(provider, getCurrentUser());
		}
	}

}
