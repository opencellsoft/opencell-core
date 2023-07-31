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

package org.meveo.apiv2.standardReport.impl;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.standardReport.AgedReceivable;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivable;
import org.meveo.model.admin.Currency;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.shared.Name;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
public class AgedReceivableMapper extends ResourceMapper<AgedReceivable, AgedReceivableDto> {

	private Provider appProvider;

	@Override
    public AgedReceivable toResource(AgedReceivableDto agedReceivableDto) {
        return ImmutableAgedReceivable.builder().customerAccountCode(agedReceivableDto.getCustomerAccountCode())
				.customerAccountName(agedReceivableDto.getCustomerAccountName())
				.customerAccountDescription(agedReceivableDto.getCustomerAccountDescription())
				.dunningLevel(agedReceivableDto.getDunningLevel())
				.notYetDue(agedReceivableDto.getNotYetDue())
				.sum_1_30(agedReceivableDto.getSum1To30())
				.sum_31_60(agedReceivableDto.getSum31To60())
				.sum_61_90(agedReceivableDto.getSum61To90())
				.sum_90_up(agedReceivableDto.getSum90Up())
				.general_total(agedReceivableDto.getGeneralTotal())
				.dueDate(agedReceivableDto.getDueDate())
				.funcCurrency(ofNullable(appProvider.getCurrency()).map(Currency::getCurrencyCode).orElse(null))
				.netAmountByPeriod(agedReceivableDto.getNetAmountByPeriod())
				.taxAmountByPeriod(agedReceivableDto.getTaxAmountByPeriod())
				.totalAmountByPeriod(agedReceivableDto.getTotalAmountByPeriod())
				.invoiceId(agedReceivableDto.getInvoiceId())
				.invoiceNumber(agedReceivableDto.getInvoiceNumber())
				.tradingCurrency(agedReceivableDto.getTradingCurrency())
				.billedAmount(agedReceivableDto.getBilledAmount())
				.customerId(agedReceivableDto.getCustomerId())
				.sellerDescription(agedReceivableDto.getSellerDescription())
				.sellerCode(agedReceivableDto.getSellerCode())
				.transactional_NotYetDue(agedReceivableDto.getTransactionalNotYetDue())
				.transactionalSum_1_30(agedReceivableDto.getTransactionalSum1To30())
				.transactionalSum_31_60(agedReceivableDto.getTransactionalSum31To60())
				.transactionalSum_61_90(agedReceivableDto.getTransactionalSum61To90())
				.transactionalSum_90_Up(agedReceivableDto.getTransactionalSum90Up())
				.transactional_GeneralTotal(agedReceivableDto.getTransactionalGeneralTotal())
				.transactional_TotalAmountByPeriod(agedReceivableDto.getTransactionalTotalAmountByPeriod())
				.transactional_NetAmountByPeriod(agedReceivableDto.getTransactionalNetAmountByPeriod())
				.transactional_TaxAmountByPeriod(agedReceivableDto.getTransactionalTaxAmountByPeriod())
				.build();
    }


	@Override
	protected AgedReceivableDto toEntity(AgedReceivable resource) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public AgedReceivable toResourceAgedReceivable(AgedReceivable agedReceivable) {
        return ImmutableAgedReceivable.copyOf(agedReceivable);
    }

	protected List<AgedReceivableDto> toEntityList(List<Object[]> resource) {
		List<AgedReceivableDto> dtoList = new  ArrayList<>();
		for (var i = 0; i < resource.size(); i++) {
			Object[] agedList = resource.get(i);
			var agedReceivableDto = new AgedReceivableDto();
			agedReceivableDto.setCustomerAccountCode((String) agedList[37]);
			agedReceivableDto.setNotYetDue((BigDecimal)agedList[1]);
			agedReceivableDto.setSum1To30((BigDecimal)agedList[3]);
			agedReceivableDto.setSum31To60((BigDecimal) agedList[6]);
			agedReceivableDto.setSum61To90((BigDecimal)agedList[9]);
			agedReceivableDto.setSum90Up((BigDecimal)agedList[12]);
			agedReceivableDto.setGeneralTotal(((BigDecimal)agedList[4])
											.add((BigDecimal)agedList[7])
											.add((BigDecimal)agedList[10])
											.add((BigDecimal)agedList[13]));

			agedReceivableDto.setDunningLevel((DunningLevelEnum) agedList[27]);
			agedReceivableDto.setCustomerAccountName(agedList[28] == null ? null : getName((Name) agedList[28]));
			agedReceivableDto.setCustomerAccountDescription((String) agedList[29]);
			agedReceivableDto.setDueDate(agedList[32] == null ? null : ((Date) agedList[32]));
			agedReceivableDto.setTradingCurrency((String) agedList[33]);

			agedReceivableDto.setNetAmountByPeriod(asList((BigDecimal) agedList[2],
					(BigDecimal) agedList[5], (BigDecimal) agedList[8], (BigDecimal) agedList[11]));
			agedReceivableDto.setTotalAmountByPeriod(asList((BigDecimal) agedList[3],
					(BigDecimal) agedList[6], (BigDecimal) agedList[9], (BigDecimal) agedList[12]));
			agedReceivableDto.setTaxAmountByPeriod(asList((BigDecimal) agedList[4],
					(BigDecimal) agedList[7], (BigDecimal) agedList[10], (BigDecimal) agedList[13]));
			agedReceivableDto.setInvoiceId((Long) agedList[34]);
			agedReceivableDto.setInvoiceNumber((String) agedList[35]);
			if(agedList[36] == null)
				agedReceivableDto.setBilledAmount((BigDecimal) agedList[38]);
			else
				agedReceivableDto.setBilledAmount((BigDecimal) agedList[36]);
			agedReceivableDto.setCustomerId((Long) agedList[39]);
			agedReceivableDto.setFuncCurrency(ofNullable(appProvider.getCurrency()).map(Currency::getCurrencyCode)
					.orElse(null));

			agedReceivableDto.setTransactionalNotYetDue((BigDecimal)agedList[2]);
			agedReceivableDto.setTransactionalSum1To30((BigDecimal)agedList[15]);
			agedReceivableDto.setTransactionalSum31To60((BigDecimal) agedList[18]);
			agedReceivableDto.setTransactionalSum61To90((BigDecimal)agedList[21]);
			agedReceivableDto.setTransactionalSum90Up((BigDecimal)agedList[24]);
			agedReceivableDto.setTransactionalGeneralTotal((getBigDecimal(agedList[16]))
					.add(getBigDecimal(agedList[19]))
					.add(getBigDecimal(agedList[22]))
					.add(getBigDecimal(agedList[25])));

			agedReceivableDto.setTransactionalNetAmountByPeriod(asList(
					getBigDecimal(agedList[15]),
					getBigDecimal(agedList[18]),
					getBigDecimal(agedList[21]),
					getBigDecimal(agedList[24])));
			agedReceivableDto.setTransactionalTotalAmountByPeriod(asList(
					getBigDecimal(agedList[16]),
					getBigDecimal(agedList[19]),
					getBigDecimal(agedList[22]),
					getBigDecimal(agedList[25])));
			agedReceivableDto.setTransactionalTaxAmountByPeriod(asList(
					getBigDecimal(agedList[17]),
					getBigDecimal(agedList[20]),
					getBigDecimal(agedList[23]),
					getBigDecimal(agedList[26])));

			dtoList.add(agedReceivableDto);
		} 
		return dtoList;
	}

	protected List<AgedReceivableDto> buildEntityList(List<Object[]> resource) {
		List<AgedReceivableDto> dtoList = new  ArrayList<>();
		for (var i = 0; i < resource.size(); i++) {
			Object[] agedList = resource.get(i);
			var agedReceivableDto = new AgedReceivableDto();
			agedReceivableDto.setNotYetDue((BigDecimal)agedList[1]);
			agedReceivableDto.setSum1To30((BigDecimal)agedList[3]);
			agedReceivableDto.setSum31To60((BigDecimal) agedList[6]);
			agedReceivableDto.setSum61To90((BigDecimal)agedList[9]);
			agedReceivableDto.setSum90Up((BigDecimal)agedList[12]);
			agedReceivableDto.setDunningLevel((DunningLevelEnum) agedList[14]);
			agedReceivableDto.setCustomerAccountName(agedList[15] == null ? null : getName((Name) agedList[15]));
			agedReceivableDto.setCustomerAccountDescription((String) agedList[16]);
			agedReceivableDto.setSellerDescription((String) agedList[17]);
			agedReceivableDto.setSellerCode((String) agedList[18]);
			agedReceivableDto.setDueDate(agedList[19] == null ? null : ((Date) agedList[19]));
			agedReceivableDto.setTradingCurrency((String) agedList[20]);
			agedReceivableDto.setInvoiceId((Long) agedList[21]);
			agedReceivableDto.setInvoiceNumber((String) agedList[22]);
			agedReceivableDto.setCustomerAccountCode((String) agedList[24]);

			agedReceivableDto.setGeneralTotal(((BigDecimal)agedList[3]).add((BigDecimal)agedList[6]).add((BigDecimal)agedList[9]).add((BigDecimal)agedList[12]));
			agedReceivableDto.setNetAmountByPeriod(asList((BigDecimal) agedList[2], (BigDecimal) agedList[5], (BigDecimal) agedList[8], (BigDecimal) agedList[11]));
			agedReceivableDto.setTotalAmountByPeriod(asList((BigDecimal) agedList[3], (BigDecimal) agedList[6], (BigDecimal) agedList[9], (BigDecimal) agedList[12]));
			agedReceivableDto.setTaxAmountByPeriod(asList((BigDecimal) agedList[4], (BigDecimal) agedList[7], (BigDecimal) agedList[10], (BigDecimal) agedList[13]));

			if(agedList[25] == null)
				agedReceivableDto.setBilledAmount((BigDecimal) agedList[23]);
			else
				agedReceivableDto.setBilledAmount((BigDecimal) agedList[25]);

			agedReceivableDto.setCustomerId((Long) agedList[26]);
			agedReceivableDto.setFuncCurrency(ofNullable(appProvider.getCurrency()).map(Currency::getCurrencyCode).orElse(null));
			dtoList.add(agedReceivableDto);
		}
		return dtoList;
	}

	protected List<AgedReceivableDto> fromListObjectToListEntity(List<Object[]> resource) {
		List<AgedReceivableDto> dtoList = new  ArrayList<>();
		for (var i = 0; i < resource.size(); i++) {
			Object[] agedList = resource.get(i);
			var agedReceivableDto = new AgedReceivableDto();
			agedReceivableDto.setNotYetDue((BigDecimal)agedList[1]);
			agedReceivableDto.setSum1To30((BigDecimal)agedList[3]);
			agedReceivableDto.setSum31To60((BigDecimal) agedList[6]);
			agedReceivableDto.setSum61To90((BigDecimal)agedList[9]);
			agedReceivableDto.setSum90Up((BigDecimal)agedList[12]);
			if(agedList[27] instanceof DunningLevelEnum) {
				agedReceivableDto.setDunningLevel((DunningLevelEnum) agedList[27]);
			}

			agedReceivableDto.setCustomerAccountDescription((String) agedList[29]);

			if(agedList[28] != null && agedList[28] instanceof Name) {
				agedReceivableDto.setCustomerAccountName(getName((Name) agedList[28]));
				agedReceivableDto.setCustomerAccountDescription(getName((Name) agedList[28]));
			}

			agedReceivableDto.setSellerDescription((String) agedList[30]);
			agedReceivableDto.setSellerCode((String) agedList[31]);
			agedReceivableDto.setDueDate(agedList[32] == null ? null : ((Date) agedList[32]));
			agedReceivableDto.setTradingCurrency((String) agedList[33]);
			agedReceivableDto.setInvoiceId((Long) agedList[34]);
			agedReceivableDto.setInvoiceNumber((String) agedList[35]);
			agedReceivableDto.setCustomerAccountCode((String) agedList[37]);

			agedReceivableDto.setGeneralTotal(((BigDecimal)agedList[3]).add((BigDecimal)agedList[6]).add((BigDecimal)agedList[9]).add((BigDecimal)agedList[12]));
			agedReceivableDto.setNetAmountByPeriod(asList((BigDecimal) agedList[2], (BigDecimal) agedList[5], (BigDecimal) agedList[8], (BigDecimal) agedList[11]));
			agedReceivableDto.setTotalAmountByPeriod(asList((BigDecimal) agedList[3], (BigDecimal) agedList[6], (BigDecimal) agedList[9], (BigDecimal) agedList[12]));
			agedReceivableDto.setTaxAmountByPeriod(asList((BigDecimal) agedList[4], (BigDecimal) agedList[7], (BigDecimal) agedList[10], (BigDecimal) agedList[13]));

			if(agedList[38] == null)
				agedReceivableDto.setBilledAmount((BigDecimal) agedList[36]);
			else
				agedReceivableDto.setBilledAmount((BigDecimal) agedList[38]);

			agedReceivableDto.setCustomerId((Long) agedList[39]);
			agedReceivableDto.setFuncCurrency(ofNullable(appProvider.getCurrency()).map(Currency::getCurrencyCode).orElse(null));
			dtoList.add(agedReceivableDto);
		}
		return dtoList;
	}


	/**
	 * @param name
	 * @return
	 */
	private String getName(Name name) {
		return (name.getFirstName() != null ? name.getFirstName() : "")
				+ (name.getLastName() != null ? " " + name.getLastName() : "");
	}

	/**
	 * Build dynamic response from list of objects
	 * @param agedReceivables List of objects
	 * @param numberOfPeriods Number of periods
	 * @return List of {@link AgedReceivableDto}
	 */
	public List<AgedReceivableDto> buildDynamicResponse(List<Object[]> agedReceivables, int numberOfPeriods) {
		List<AgedReceivableDto> responseDto = new  ArrayList<>();

		for (int index = 0; index < agedReceivables.size(); index++) {
			Object[] agedReceivable = agedReceivables.get(index);
			AgedReceivableDto agedReceivableDto = new AgedReceivableDto();
			agedReceivableDto.setNotYetDue((BigDecimal) agedReceivable[1]);
			agedReceivableDto.setTransactionalNotYetDue((BigDecimal) agedReceivable[2]);
			int sumIndex;
			int startingSumIndex = 3;
			agedReceivableDto.setNetAmountByPeriod(new ArrayList<>());
			agedReceivableDto.setTotalAmountByPeriod(new ArrayList<>());
			agedReceivableDto.setTaxAmountByPeriod(new ArrayList<>());

			for (sumIndex = 0; sumIndex < numberOfPeriods; sumIndex++) {
				agedReceivableDto.getNetAmountByPeriod().add(getBigDecimal(agedReceivable[startingSumIndex]));
				agedReceivableDto.getTotalAmountByPeriod().add(getBigDecimal(agedReceivable[startingSumIndex + 1]));
				agedReceivableDto.getTaxAmountByPeriod().add(getBigDecimal(agedReceivable[startingSumIndex + 2]));

				agedReceivableDto.getTransactionalNetAmountByPeriod().add(getBigDecimal(agedReceivable[startingSumIndex + 3]));
				agedReceivableDto.getTransactionalTotalAmountByPeriod().add(getBigDecimal(agedReceivable[startingSumIndex + 4]));
				agedReceivableDto.getTransactionalTaxAmountByPeriod().add(getBigDecimal(agedReceivable[startingSumIndex + 5]));
				startingSumIndex += 6;
			}

			agedReceivableDto.setDunningLevel((DunningLevelEnum) agedReceivable[startingSumIndex]);
			agedReceivableDto.setCustomerAccountName(agedReceivable[++startingSumIndex] == null ? null : getName((Name) agedReceivable[startingSumIndex]));
			agedReceivableDto.setCustomerAccountDescription(agedReceivableDto.getCustomerAccountName() != null ? agedReceivableDto.getCustomerAccountName() : (String) agedReceivable[++startingSumIndex]);
			if(agedReceivableDto.getCustomerAccountName() != null) {
				++startingSumIndex;
			}
			agedReceivableDto.setSellerDescription((String) agedReceivable[++startingSumIndex]);
			agedReceivableDto.setSellerCode((String) agedReceivable[++startingSumIndex]);
			agedReceivableDto.setDueDate(agedReceivable[++startingSumIndex] == null ? null : ((Date) agedReceivable[startingSumIndex]));
			agedReceivableDto.setTradingCurrency((String) agedReceivable[++startingSumIndex]);
			BigDecimal generalTotal = agedReceivableDto.getTotalAmountByPeriod()
					.stream()
					.reduce(ZERO, BigDecimal::add);
			agedReceivableDto.setGeneralTotal(generalTotal);
			agedReceivableDto.setInvoiceId((Long) agedReceivable[++startingSumIndex]);
			agedReceivableDto.setInvoiceNumber((String) agedReceivable[++startingSumIndex]);
			agedReceivableDto.setBilledAmount((BigDecimal) agedReceivable[++startingSumIndex]);
			agedReceivableDto.setFuncCurrency(ofNullable(appProvider.getCurrency()).map(Currency::getCurrencyCode).orElse(null));
			agedReceivableDto.setCustomerAccountCode((String) agedReceivable[++startingSumIndex]);

			if(agedReceivable[++startingSumIndex] != null) {
				agedReceivableDto.setBilledAmount(getBigDecimal(agedReceivable[startingSumIndex]));
			}

			agedReceivableDto.setCustomerId((Long) agedReceivable[++startingSumIndex]);
			BigDecimal transactionalGeneralTotal = agedReceivableDto.getTransactionalTotalAmountByPeriod()
					.stream()
					.reduce(ZERO, BigDecimal::add);
			agedReceivableDto.setTransactionalGeneralTotal(transactionalGeneralTotal);
			responseDto.add(agedReceivableDto);
		}
		return responseDto;
	}

	public void setAppProvider(Provider appProvider) {
		this.appProvider = appProvider;
	}

	private BigDecimal getBigDecimal(Object value) {
		return value == null ? ZERO : (BigDecimal) value;
	}
   
}