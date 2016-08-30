package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.usage.CatUsageDto;
import org.meveo.api.dto.usage.SubCatUsageDto;
import org.meveo.api.dto.usage.UsageDto;
import org.meveo.api.dto.usage.UsageRequestDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;

@Stateless
public class UsageApi extends BaseApi {

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	public UsageResponseDto find(UsageRequestDto usageRequestDto, User user) throws MissingParameterException, EntityDoesNotExistsException {

		if (StringUtils.isBlank(usageRequestDto.getUserAccountCode())) {
			missingParameters.add("UserAccountCode");
		}
		UsageResponseDto result = new UsageResponseDto();
		handleMissingParameters();

		UserAccount userAccount = userAccountService.findByCode(usageRequestDto.getUserAccountCode(), user.getProvider());

		if (userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, usageRequestDto.getUserAccountCode());
		}
		List<InvoiceCategory> invoiceCats = invoiceCategoryService.list(user.getProvider());
		for (InvoiceCategory invoiceCategory : invoiceCats) {
			List<InvoiceSubCategory> invoiceSubCats = invoiceCategory.getInvoiceSubCategories();
			CatUsageDto catUsageDto = new CatUsageDto();
			catUsageDto.setCode(invoiceCategory.getCode());
			catUsageDto.setDescription(invoiceCategory.getDescription());

			for (InvoiceSubCategory invoiceSubCategory : invoiceSubCats) {
				SubCatUsageDto subCatUsageDto = new SubCatUsageDto();
				subCatUsageDto.setCode(invoiceSubCategory.getCode());
				subCatUsageDto.setDescription(invoiceSubCategory.getDescription());
				List<RatedTransaction> ratedTransactions = ratedTransactionService.openRTbySubCat(userAccount.getWallet(), invoiceSubCategory, usageRequestDto.getFromDate(), usageRequestDto.getToDate());
				for (RatedTransaction rt : ratedTransactions) {
					UsageDto usageDto = new UsageDto();
					usageDto.setCode(rt.getCode());
					usageDto.setDescription(rt.getDescription());
					usageDto.setAmountWithoutTax(rt.getAmountWithoutTax());
					usageDto.setDateEvent(rt.getUsageDate());
					usageDto.setOfferCode(rt.getOfferCode());
					usageDto.setParameter1(rt.getParameter1());
					usageDto.setParameter2(rt.getParameter2());
					usageDto.setParameter3(rt.getParameter3());
					usageDto.setPriceplanCode(rt.getPriceplan() == null ? null : rt.getPriceplan().getCode());
					usageDto.setQuantity(rt.getQuantity());
					usageDto.setUnitAmountWithoutTax(rt.getUnitAmountWithoutTax());
					usageDto.setUnityDescription(rt.getUnityDescription());
					subCatUsageDto.getListUsage().add(usageDto);
				}
				if(subCatUsageDto.getListUsage().size()>0){
				catUsageDto.getListSubCatUsage().add(subCatUsageDto);
				}
			}
			if(catUsageDto.getListSubCatUsage().size()>0){
		     result.getListCatUsage().add(catUsageDto);
			}
		}
		return result;
	}
}