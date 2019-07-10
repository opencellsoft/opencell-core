package org.meveo.api.dto.billing;

import java.util.List;

public class CountersPeriodsDto {

    private List<CounterPeriodDto> counterPeriods;

    public CountersPeriodsDto(List<CounterPeriodDto> counterPeriodDtos) {
        this.counterPeriods = counterPeriodDtos;
    }

    public List<CounterPeriodDto> getCounterPeriodDto() {
        return counterPeriods;
    }

    public void setCounterPeriodDto(List<CounterPeriodDto> counterPeriodDto) {
        this.counterPeriods = counterPeriodDto;
    }
}
