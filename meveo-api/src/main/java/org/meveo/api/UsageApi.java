package org.meveo.api;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.usage.CatUsageDto;
import org.meveo.api.dto.usage.ChargeAggregateDto;
import org.meveo.api.dto.usage.SubCatUsageDto;
import org.meveo.api.dto.usage.UsageChargeAggregateResponseDto;
import org.meveo.api.dto.usage.UsageDto;
import org.meveo.api.dto.usage.UsageRequestDto;
import org.meveo.api.dto.usage.UsageResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

@Stateless
public class UsageApi extends BaseApi {

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;

	/**
	 *
	 * @param usageRequestDto
	 * @param user
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
	public UsageChargeAggregateResponseDto chargeAggregate(UsageRequestDto usageRequestDto, User user) throws MissingParameterException, EntityDoesNotExistsException {

		if (StringUtils.isBlank(usageRequestDto.getUserAccountCode())) {
			missingParameters.add("UserAccountCode");
		}

		handleMissingParameters();

		UserAccount userAccount = userAccountService.findByCode(usageRequestDto.getUserAccountCode(), user.getProvider());

		if (userAccount == null) {
			throw new EntityDoesNotExistsException(UserAccount.class, usageRequestDto.getUserAccountCode());
		}
		UsageChargeAggregateResponseDto  response = new UsageChargeAggregateResponseDto();
		String currencyCode = userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency().getCurrencyCode();
		List<Object[]>  rows = walletOperationService.openWalletOperationsByCharge(userAccount.getWallet());
		for(Object[] row : rows){
			try{
				log.debug("chargeAggregate  desc {},  quantity {}, amount {}, unit {}",row[0],row[1],row[2],row[3]);
				ChargeAggregateDto chargeAggregate = new ChargeAggregateDto();
				chargeAggregate.setDescription((String) row[0]);
				chargeAggregate.setAmount(""+NumberUtils.round((BigDecimal)row[2], 2)+" "+currencyCode);
				BigDecimal quantity = BigDecimal.ZERO;
				String quantityToDisplay = "0";
				if((BigDecimal)row[1] != null){
					quantity = NumberUtils.round((BigDecimal)row[1], 2);
					quantityToDisplay = quantity.toPlainString();

					if(("mn".equals((String) row[3]) || "min".equals((String) row[3]) ) && quantity.doubleValue() >59 ){
						long hours = quantity.longValue() / 60;
						quantityToDisplay = hours+"h " ;
						long mins = quantity.longValue() % 60;
						quantityToDisplay += mins +(String) row[3] ;
					}else if(!StringUtils.isBlank((String) row[3])){
						quantityToDisplay +=  " "+(String) row[3];
					}
				}
				log.debug("chargeAggregate  quantityToDisplay {}",quantityToDisplay);
				chargeAggregate.setQuantity(quantityToDisplay);

				response.getListChargeAggregate().add(chargeAggregate);
			}catch(Exception e){
				log.error("usage row error:",e);
			}
		}
		return response;
	}

	/**
	 *
	 * @param usageRequestDto
	 * @param user
	 * @return
	 * @throws MissingParameterException
	 * @throws EntityDoesNotExistsException
	 */
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
		ChargeTemplate chargeTemplate;
		for (InvoiceCategory invoiceCategory : invoiceCats) {
			List<InvoiceSubCategory> invoiceSubCats = invoiceCategory.getInvoiceSubCategories();
			CatUsageDto catUsageDto = new CatUsageDto();
			catUsageDto.setCode(invoiceCategory.getCode());
			catUsageDto.setDescription(invoiceCategory.getDescription());

			for (InvoiceSubCategory invoiceSubCategory : invoiceSubCats) {
				SubCatUsageDto subCatUsageDto = new SubCatUsageDto();
				subCatUsageDto.setCode(invoiceSubCategory.getCode());
				subCatUsageDto.setDescription(invoiceSubCategory.getDescription());
				List<WalletOperation> walletOperations = walletOperationService.openWalletOperationsBySubCat(userAccount.getWallet(), invoiceSubCategory, usageRequestDto.getFromDate(), usageRequestDto.getToDate());
				for (WalletOperation op : walletOperations) {
					chargeTemplate = op.getChargeInstance().getChargeTemplate();
					chargeTemplate = usageChargeTemplateService.findById(chargeTemplate.getId());
					if(chargeTemplate != null){
						UsageDto usageDto = new UsageDto();
						usageDto.setCode(op.getCode());
						usageDto.setDescription(op.getDescription());
						usageDto.setAmountWithoutTax(op.getAmountWithoutTax());
						usageDto.setDateEvent(op.getOperationDate());
						usageDto.setOfferCode(op.getOfferCode());
						usageDto.setParameter1(op.getParameter1());
						usageDto.setParameter2(op.getParameter2());
						usageDto.setParameter3(op.getParameter3());
						usageDto.setPriceplanCode(op.getPriceplan() == null ? null : op.getPriceplan().getCode());
						usageDto.setQuantity(op.getQuantity());
						usageDto.setUnitAmountWithoutTax(op.getUnitAmountWithoutTax());
						usageDto.setUnityDescription(op.getInputUnitDescription());
						subCatUsageDto.getListUsage().add(usageDto);
					}
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