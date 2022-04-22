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

import java.math.BigDecimal;
import java.util.*;

import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.standardReport.AgedReceivable;
import org.meveo.apiv2.standardReport.ImmutableAgedReceivable;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.shared.Name;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
public class AgedReceivableMapper extends ResourceMapper<AgedReceivable, AgedReceivableDto> {

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
				.funcCurrency(agedReceivableDto.getFuncCurrency())
				.netAmountByPeriod(agedReceivableDto.getNetAmountByPeriod())
				.taxAmountByPeriod(agedReceivableDto.getTaxAmountByPeriod())
				.totalAmountByPeriod(agedReceivableDto.getTotalAmountByPeriod())
				.invoiceId(agedReceivableDto.getInvoiceId())
				.invoiceNumber(agedReceivableDto.getInvoiceNumber())
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
			agedReceivableDto.setCustomerAccountCode(agedList[0].toString());
			agedReceivableDto.setNotYetDue((BigDecimal)agedList[1]);
			agedReceivableDto.setSum1To30((BigDecimal)agedList[2]);
			agedReceivableDto.setSum31To60((BigDecimal) agedList[5]);
			agedReceivableDto.setSum61To90((BigDecimal)agedList[8]);
			agedReceivableDto.setSum90Up((BigDecimal)agedList[11]);
			agedReceivableDto.setGeneralTotal(((BigDecimal)agedList[2])
											.add((BigDecimal)agedList[5])
											.add((BigDecimal)agedList[8])
											.add((BigDecimal)agedList[11]));
			agedReceivableDto.setDunningLevel((DunningLevelEnum) agedList[14]);
			agedReceivableDto.setCustomerAccountName(agedList[15] == null ? null : getName((Name) agedList[15]));
			agedReceivableDto.setCustomerAccountDescription((String) agedList[16]);
			agedReceivableDto.setDueDate(agedList[17] == null ? null : ((Date) agedList[17]));
			agedReceivableDto.setFuncCurrency((String) agedList[18]);
			agedReceivableDto.setNetAmountByPeriod(asList((BigDecimal) agedList[2],
					(BigDecimal) agedList[5], (BigDecimal) agedList[8], (BigDecimal) agedList[11]));
			agedReceivableDto.setTotalAmountByPeriod(asList((BigDecimal) agedList[3],
					(BigDecimal) agedList[6], (BigDecimal) agedList[9], (BigDecimal) agedList[11]));
			agedReceivableDto.setTaxAmountByPeriod(asList((BigDecimal) agedList[4],
					(BigDecimal) agedList[7], (BigDecimal) agedList[10], (BigDecimal) agedList[12]));
			agedReceivableDto.setInvoiceId((Long) agedList[19]);
			agedReceivableDto.setInvoiceNumber((String) agedList[20]);
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

	public List<AgedReceivableDto> buildDynamicResponse(List<Object[]> agedReceivables, int numberOfPeriods) {
		List<AgedReceivableDto> responseDto = new  ArrayList<>();
		for (int index = 0; index < agedReceivables.size(); index++) {
			Object[] agedReceivable = agedReceivables.get(index);
			AgedReceivableDto agedReceivableDto = new AgedReceivableDto();
			agedReceivableDto.setCustomerAccountCode(agedReceivable[0].toString());
			agedReceivableDto.setNotYetDue((BigDecimal) agedReceivable[1]);
			int sumIndex;
			int startingSumIndex = 2;
			agedReceivableDto.setNetAmountByPeriod(new ArrayList<>());
			agedReceivableDto.setTotalAmountByPeriod(new ArrayList<>());
			agedReceivableDto.setTaxAmountByPeriod(new ArrayList<>());
			for (sumIndex = 0; sumIndex <= numberOfPeriods; sumIndex++) {
				agedReceivableDto.getNetAmountByPeriod().add((BigDecimal) agedReceivable[startingSumIndex]);
				agedReceivableDto.getTotalAmountByPeriod().add((BigDecimal) agedReceivable[startingSumIndex + 1]);
				agedReceivableDto.getTaxAmountByPeriod().add((BigDecimal) agedReceivable[startingSumIndex + 2]);
				startingSumIndex += 3;
			}
			agedReceivableDto.setDunningLevel((DunningLevelEnum) agedReceivable[startingSumIndex]);
			agedReceivableDto.setCustomerAccountName(agedReceivable[++startingSumIndex] == null ? null : getName((Name) agedReceivable[startingSumIndex]));
			agedReceivableDto.setCustomerAccountDescription((String) agedReceivable[++startingSumIndex]);
			agedReceivableDto.setDueDate(agedReceivable[++startingSumIndex] == null ? null : ((Date) agedReceivable[startingSumIndex]));
			agedReceivableDto.setFuncCurrency((String) agedReceivable[++startingSumIndex]);
			BigDecimal generalTotal = agedReceivableDto.getNetAmountByPeriod()
					.stream()
					.reduce(ZERO, BigDecimal::add);
			agedReceivableDto.setGeneralTotal(generalTotal);
			agedReceivableDto.setInvoiceId((Long) agedReceivable[22]);
			agedReceivableDto.setInvoiceNumber((String) agedReceivable[23]);
			responseDto.add(agedReceivableDto);
		}
		return responseDto;
	}
   
}