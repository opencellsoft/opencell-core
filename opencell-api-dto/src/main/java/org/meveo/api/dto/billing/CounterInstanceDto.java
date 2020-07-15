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

package org.meveo.api.dto.billing;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.shared.DateUtils;

/**
 * The Class CounterInstanceDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterInstanceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -72154111229222183L;

    /**
     * The Counter periods
     */
    private CountersPeriodsDto counterPeriods;

    
    /**
     * Instantiates a new counter instance dto.
     */
    public CounterInstanceDto() {
    }

    /**
     * Instantiates a new counter instance dto.
     *
     * @param counterInstance the counter instance
     */
    public CounterInstanceDto(CounterInstance counterInstance) {
        super(counterInstance);
        fillCounterPeriods(counterInstance.getCounterPeriods());
    }

    private void fillCounterPeriods(List<CounterPeriod> periods) {
        if (periods == null) {
            return;
        }
        periods.sort(Comparator.comparing(CounterPeriod::getPeriodStartDate));
        List<CounterPeriodDto> counterPeriodDtos = periods.stream().map(cp -> {
            CounterPeriodDto dto = new CounterPeriodDto();
            dto.setCounterType(cp.getCounterType());
            dto.setLevel(cp.getLevel());
            dto.setPeriodEndDate(DateUtils.formatDateWithPattern(cp.getPeriodEndDate(), "yyyy-MM-dd"));
            dto.setPeriodStartDate(DateUtils.formatDateWithPattern(cp.getPeriodStartDate(), "yyyy-MM-dd"));
            dto.setValue(cp.getValue());
            dto.setAccumulatedValues(cp.getAccumulatedValues());
            dto.setAccumulator(cp.getAccumulator());
            dto.setAccumulatorType(cp.getAccumulatorType());
            return dto;
        }).collect(Collectors.toList());
        this.counterPeriods = new CountersPeriodsDto(counterPeriodDtos);

    }

    @Override
    public String toString() {
        return "CounterInstanceDto [code=" + code + ", description=" + description + "]";
    }
}